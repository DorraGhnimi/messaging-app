package com.projects.messaging_app.messaging;

import com.projects.messaging_app.messaging.email.EmailService;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class MessagingAppApplication {

	@Autowired private FolderRepository folderRepository;
	@Autowired private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(MessagingAppApplication.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init() {
		folderRepository.save(new Folder("DorraGhnimi", "Work", "blue"));
		folderRepository.save(new Folder("DorraGhnimi", "Fun", "yellow"));
		folderRepository.save(new Folder("DorraGhnimi", "Family", "green"));

		for(int i=0; i<2; i++) {
			emailService.send("floki", Arrays.asList("DorraGhnimi", "Rag"), "test subject" + i, "test body" + i);
		}
	}
}
