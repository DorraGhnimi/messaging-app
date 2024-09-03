package com.projects.messaging_app.messaging.controllers;

import com.projects.messaging_app.messaging.email.EmailService;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;
import com.projects.messaging_app.messaging.services.FolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FolderService folderService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/compose")
    public String composePage(@AuthenticationPrincipal OAuth2User principal, @RequestParam(required = false) String to, Model model) {
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

        // set to ids
        final List<String> toIdsString = splitIds(to);
        model.addAttribute("toIds", String.join(",", toIdsString));

        model.addAttribute("unreadStats", folderService.getMapCountersToLabels(userId));

        return "compose-page";
    }

    private List<String> splitIds(String to) {
        if (!StringUtils.hasText(to)) {
            return new ArrayList<String>();
        }
        final String[] splitIds = to.split(",");
        return Arrays.stream(splitIds)
                .map(StringUtils::trimWhitespace)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    @PostMapping("/sendEmail")
    public ModelAndView composePage(@AuthenticationPrincipal OAuth2User principal, @RequestBody MultiValueMap<String, String> formData) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return new ModelAndView("redirect:/");
        }
        String from = principal.getAttribute("login");
        String to = formData.getFirst("toIds");
        final List<String> toIdsList = splitIds(to);
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");

        emailService.send(from, toIdsList, subject, body);

        return new ModelAndView("redirect:/");
    }
}
