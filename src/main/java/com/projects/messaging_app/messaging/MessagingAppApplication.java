package com.projects.messaging_app.messaging;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.projects.messaging_app.messaging.emailList.EmailListItem;
import com.projects.messaging_app.messaging.emailList.EmailListItemKey;
import com.projects.messaging_app.messaging.emailList.EmailListItemRepository;
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

	@Autowired
	private FolderRepository folderRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;

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
		folderRepository.save(new Folder("DorraGhnimi", "inbox", "blue"));
		folderRepository.save(new Folder("DorraGhnimi", "sent", "yellow"));
		folderRepository.save(new Folder("DorraGhnimi", "important", "green"));

		for(int i=0; i<10; i++) {
			EmailListItemKey key = new EmailListItemKey("DorraGhnimi", "Inbox", Uuids.timeBased());
			emailListItemRepository.save(new EmailListItem(key, Arrays.asList("Floki"), "Subject " + i, true));
		}
	}
}
