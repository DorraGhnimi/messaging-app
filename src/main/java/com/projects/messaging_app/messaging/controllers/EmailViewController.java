package com.projects.messaging_app.messaging.controllers;

import com.projects.messaging_app.messaging.email.Email;
import com.projects.messaging_app.messaging.email.EmailRepository;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;
import com.projects.messaging_app.messaging.services.FolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private FolderService folderService;
    @Autowired private EmailRepository emailRepository;

    @RequestMapping("/email/{id}")
    public String emailView(@AuthenticationPrincipal OAuth2User principal, @PathVariable(name = "id") UUID emailId, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
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

        Optional<Email> emailOptional = emailRepository.findById(emailId);
        if(!emailOptional.isPresent()) {
            return "inbox-page";
        }
        Email email = emailOptional.get();
        model.addAttribute("email", email);

        String toListString = String.join(",",email.getTo());
        model.addAttribute("toListString", toListString);

        return "email-page";
    }
}
