package com.projects.messaging_app.messaging.controllers;

import com.projects.messaging_app.messaging.email.Email;
import com.projects.messaging_app.messaging.email.EmailRepository;
import com.projects.messaging_app.messaging.email.EmailService;
import com.projects.messaging_app.messaging.emailList.EmailListItem;
import com.projects.messaging_app.messaging.emailList.EmailListItemKey;
import com.projects.messaging_app.messaging.emailList.EmailListItemRepository;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;
import com.projects.messaging_app.messaging.folders.UnreadEmailStatsRepository;
import com.projects.messaging_app.messaging.services.FolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private FolderService folderService;
    @Autowired private EmailRepository emailRepository;
    @Autowired private EmailListItemRepository emailListItemRepository;
    @Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;
    @Autowired
    private EmailService emailService;

    @RequestMapping("/email/{id}")
    public String emailView(@AuthenticationPrincipal OAuth2User principal, @PathVariable(name = "id") UUID emailId, Model model, @RequestParam String folderLabel) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            System.out.println("Not logged in user");
            return "index";
        }

        // Fetch folders
        String userId = principal.getAttribute("login");
        model.addAttribute("userId", userId);
        model.addAttribute("username", principal.getAttribute("name"));
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        Optional<Email> emailOptional = emailRepository.findById(emailId);
        if(!emailOptional.isPresent()) {
            return "redirect:/";
        }
        Email email = emailOptional.get();
        String toListString = String.join(",",email.getTo());

        // check if user has permission to see requested email
        if(!emailService.doesHaveAccess(email, userId)) {
            System.out.println(userId + "can not see requested email!");
            return "redirect:/";
        }

        model.addAttribute("email", email);
        model.addAttribute("toListString", toListString);

        EmailListItemKey emailListItemKey = new EmailListItemKey(userId, folderLabel, email.getId());
        Optional<EmailListItem> emailListItemOptional = emailListItemRepository.findById(emailListItemKey);
        if (emailListItemOptional.isPresent()) {
            EmailListItem emailListItem = emailListItemOptional.get();
            if (emailListItem.isUnread()) {
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCounter(userId, folderLabel);
            }
        }
        model.addAttribute("unreadStats", folderService.getMapCountersToLabels(userId));

        return "email-page";
    }
}
