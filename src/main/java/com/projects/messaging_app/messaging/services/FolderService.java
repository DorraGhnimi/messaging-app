package com.projects.messaging_app.messaging.services;

import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.UnreadEmailStats;
import com.projects.messaging_app.messaging.folders.UnreadEmailStatsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> fetchDefaultFolders(String userId){
        return Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent", "green"),
                new Folder(userId, "Importanté", "red")
        );
    }

    public Map<String, Integer> getMapCountersToLabels(String userId) {
        List<UnreadEmailStats> unreadCounterEmailStatsList = unreadEmailStatsRepository.findAllById(userId);
        return unreadCounterEmailStatsList.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel, UnreadEmailStats::getUnreadCounter));
    }
}
