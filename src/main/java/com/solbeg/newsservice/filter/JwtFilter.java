package com.solbeg.newsservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.exception.model.IncorrectData;
import com.solbeg.newsservice.mapper.UserMapper;
import com.solbeg.newsservice.service.UserDataService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserDataService userDataService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req,
                                    @NotNull HttpServletResponse resp,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
                filterChain.doFilter(req, resp);
                return;
            }
            UserResponse userData = userDataService.getUserData(header);
            UserDetails userDetails = userMapper.toUserDetails(userData);

            UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                    userDetails, null, userDetails.getAuthorities());
            authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            filterChain.doFilter(req, resp);
        } catch (AuthenticationException | RestClientException exception) {
            handleException(resp, exception);
        }
    }

    public void handleException(HttpServletResponse response, Exception exception) throws IOException {
        int status = getStatus(exception);
        response.setStatus(status);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("utf-8");

        String responseMessage = objectMapper.writeValueAsString(getIncorrectData(exception, status));

        response.getWriter().write(responseMessage);
    }

    private IncorrectData getIncorrectData(Exception exception, int status) throws JsonProcessingException {
        String errorMessage = exception.getMessage();
        IncorrectData incorrectData;

        if (errorMessage.matches("^\\d{3} .*")) {
            incorrectData = getIncorrectDataFromExceptionMessage(errorMessage);
        } else {
            incorrectData = IncorrectData.builder()
                    .timestamp(LocalDateTime.now())
                    .errorMessage(exception.getMessage())
                    .errorStatus(status)
                    .build();
        }
        return incorrectData;
    }

    private static int getStatus(Exception exception) {
        int status;
        if (exception instanceof RestClientException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        } else {
            status = HttpStatus.UNAUTHORIZED.value();
        }
        return status;
    }

    private IncorrectData getIncorrectDataFromExceptionMessage(String errorMessage) throws JsonProcessingException {
        errorMessage = errorMessage.split(": ", 2)[1]
                .replace("\\\"", "\"")
                .replace("\"{", "{")
                .replace("}\"", "}");
        return objectMapper.readValue(errorMessage, IncorrectData.class);
    }
}