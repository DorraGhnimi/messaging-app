package com.projects.messaging_app.messaging.email;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.projects.messaging_app.messaging.emailList.EmailListItem;
import com.projects.messaging_app.messaging.emailList.EmailListItemKey;
import com.projects.messaging_app.messaging.emailList.EmailListItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired private EmailRepository emailRepository;
    @Autowired private EmailListItemRepository emailListItemRepository;

    public void send(String from, List<String> toIdsList, String subject, String body) {
        // create email
        Email email = new Email(Uuids.timeBased(), from, toIdsList, subject, body);
        emailRepository.save(email);

        // save email item in sender emailItem list in the sent folder
        toIdsList.forEach(toId -> {
            final EmailListItem receivedEmailListItem = createEmailListItem(toIdsList, subject, toId, email, "Inbox");
            emailListItemRepository.save(receivedEmailListItem);
        });

        // save email item in recipients emailItem list in the inbox folder
        final EmailListItem sentEmailListItem = createEmailListItem(toIdsList, subject, from, email, "Sent");
        emailListItemRepository.save(sentEmailListItem);
    }

    private static EmailListItem createEmailListItem(List<String> toIdsList, String subject, String toId, Email email, String folderLabel) {
        EmailListItemKey key = new EmailListItemKey(toId, folderLabel, email.getId());
        return new EmailListItem(key, toIdsList, subject, true);
    }
}
