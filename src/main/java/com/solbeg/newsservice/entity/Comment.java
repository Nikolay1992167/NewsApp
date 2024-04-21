package com.solbeg.newsservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "news", name = "comments")
public class Comment extends BaseEntity {

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "username", nullable = false, length = 40)
    private String username;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private News news;
}