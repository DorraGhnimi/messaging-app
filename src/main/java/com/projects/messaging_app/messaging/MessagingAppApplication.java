package com.projects.messaging_app.messaging;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.projects.messaging_app.messaging.email.Email;
import com.projects.messaging_app.messaging.email.EmailRepository;
import com.projects.messaging_app.messaging.emailList.EmailListItem;
import com.projects.messaging_app.messaging.emailList.EmailListItemKey;
import com.projects.messaging_app.messaging.emailList.EmailListItemRepository;
import com.projects.messaging_app.messaging.folders.Folder;
import com.projects.messaging_app.messaging.folders.FolderRepository;
import com.projects.messaging_app.messaging.folders.UnreadEmailStatsRepository;

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
	@Autowired private EmailListItemRepository emailListItemRepository;
	@Autowired private EmailRepository emailRepository;
	@Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;

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
		folderRepository.save(new Folder("DorraGhnimi", "Inbox", "blue"));
		folderRepository.save(new Folder("DorraGhnimi", "Sent", "yellow"));
		folderRepository.save(new Folder("DorraGhnimi", "Important", "green"));

		for(int i=0; i<2; i++) {
			EmailListItemKey key = new EmailListItemKey("DorraGhnimi", "Inbox", Uuids.timeBased());
			EmailListItem emailListItem = new EmailListItem(key, Arrays.asList("DorraGhnimi", "Rag"), "Subject " + i, true);
			emailListItemRepository.save(emailListItem);
			Email email = new Email(key.getTimeUuid(),"floki", emailListItem.getTo(), emailListItem.getSubject(), "body "+i);
			unreadEmailStatsRepository.incrementUnreadCounter("DorraGhnimi", "Inbox");

			emailRepository.save(email);
		}
	}
}
