package com.projects.messaging_app.messaging.services;

import com.projects.messaging_app.messaging.folders.Folder;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FolderService {

    public List<Folder> fetchDefaultFolders(String userId){
        return Arrays.asList(
                new Folder(userId, "inbox", "blue"),
                new Folder(userId, "inbox", "green"),
                new Folder(userId, "inbox", "red")
        );
    }
}
