package com.solbeg.newsservice.entity.listener;

import com.solbeg.newsservice.entity.Revision;
import com.solbeg.newsservice.util.AuthUtil;
import org.hibernate.envers.RevisionListener;

public class AuthorRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        ((Revision) revisionEntity).setUserId(AuthUtil.getId());
    }
}