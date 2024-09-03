package com.projects.messaging_app.messaging.folders;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends CassandraRepository<Folder, String> {
    public List<Folder> findAllById(String id);
}
