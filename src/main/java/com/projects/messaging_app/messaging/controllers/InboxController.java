package com.projects.messaging_app.messaging.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.projects.messaging_app.messaging.emailList.EmailListItem;
import com.projects.messaging_app.messaging.emailList.EmailListItemRepository;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;
import com.projects.messaging_app.messaging.folders.UnreadEmailStatsRepository;
import com.projects.messaging_app.messaging.services.FolderService;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class InboxController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private FolderService folderService;
    @Autowired private EmailListItemRepository emailListItemRepository;
    @Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @RequestMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, @RequestParam(required = false) String folder, Model model) {
        if(principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            System.out.println("Not logged in user");
            return "index";
        }

        // Fetch folders
        String userId = principal.getAttribute("login");
        model.addAttribute("userId", userId);
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        Map<String, Integer> counterLabelMAp = folderService.getMapCountersToLabels(userId);
        model.addAttribute("unreadStats", counterLabelMAp);

        //Fetch Messages
        if(!StringUtils.hasText(folder)) {
            folder = "Inbox";
        }
        model.addAttribute("folder", folder);
        List<EmailListItem> emailListItems = emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folder);
        model.addAttribute("emailListItems", emailListItems);

        PrettyTime prettyTime = new PrettyTime();
        emailListItems.stream().forEach(emailListItem -> {
            UUID timeUuid = emailListItem.getKey().getTimeUuid();
            Date date = new Date(Uuids.unixTimestamp(timeUuid));
            emailListItem.setAgoTimeString(prettyTime.format(date));
        });

        return "inbox-page";
    }
}
