package com.projects.messaging_app.messaging.emailList;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table(value = "messages_by_user_and_folder")
public class EmailListItem {
    @PrimaryKey
    private EmailListItemKey key;
    @CassandraType(type = CassandraType.Name.TEXT)
    private List<String> to;
    @CassandraType(type = CassandraType.Name.TEXT)
    private String subject;
    @CassandraType(type = CassandraType.Name.BOOLEAN)
    private boolean isUnread;
    @Transient
    private String agoTimeString;

    public EmailListItem(EmailListItemKey key, List<String> to, String subject, boolean isUnread) {
        this.key = key;
        this.to = to;
        this.subject = subject;
        this.isUnread = isUnread;
    }

    public EmailListItemKey getKey() {
        return key;
    }

    public void setKey(EmailListItemKey key) {
        this.key = key;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getAgoTimeString() {
        return agoTimeString;
    }

    public void setAgoTimeString(String agoTimeString) {
        this.agoTimeString = agoTimeString;
    }
}
