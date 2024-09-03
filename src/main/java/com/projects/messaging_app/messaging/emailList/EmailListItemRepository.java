package com.projects.messaging_app.messaging.emailList;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailListItemRepository extends CassandraRepository<EmailListItem, EmailListItemKey> {
    public List<EmailListItem> findAllByKey_IdAndKey_Label(String id, String label);
}
