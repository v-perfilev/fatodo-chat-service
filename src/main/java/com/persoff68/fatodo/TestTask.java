package com.persoff68.fatodo;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import com.persoff68.fatodo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class TestTask {

    private static final UUID USER_1_ID = UUID.fromString("98a4f736-70c2-4c7d-b75b-f7a5ae7bbe8d");
    private static final UUID USER_2_ID = UUID.fromString("8d583dfd-acfb-4481-80e6-0b46170e2a18");
    private static final UUID USER_3_ID = UUID.fromString("5b8bfe7e-7651-4d39-a70c-22c997e376b1");

    private final ChatRepository chatRepository;
    private final MemberEventRepository memberEventRepository;
    private final MessageRepository messageRepository;

//    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void createDatabaseRecords() throws InterruptedException {
        chatRepository.deleteAll();
        memberEventRepository.deleteAll();
        messageRepository.deleteAll();

        Chat firstChat = chatRepository.save(new Chat(true));
        Chat secondChat = chatRepository.save(new Chat(true));
        Chat thirdChat = chatRepository.save(new Chat(true));

        memberEventRepository.save(new MemberEvent(firstChat, USER_1_ID, MemberEvent.Type.ADD_MEMBER));
        memberEventRepository.save(new MemberEvent(firstChat, USER_2_ID, MemberEvent.Type.ADD_MEMBER));

        memberEventRepository.save(new MemberEvent(secondChat, USER_1_ID, MemberEvent.Type.ADD_MEMBER));
        memberEventRepository.save(new MemberEvent(secondChat, USER_3_ID, MemberEvent.Type.ADD_MEMBER));

        memberEventRepository.save(new MemberEvent(thirdChat, USER_2_ID, MemberEvent.Type.ADD_MEMBER));
        memberEventRepository.save(new MemberEvent(thirdChat, USER_3_ID, MemberEvent.Type.ADD_MEMBER));

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            messageRepository.saveAndFlush(new Message(firstChat, USER_1_ID, "first-" + i, null));
        }

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            messageRepository.saveAndFlush(new Message(secondChat, USER_3_ID, "second-" + i, null));
        }

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            messageRepository.saveAndFlush(new Message(thirdChat, USER_2_ID, "third-" + i, null));
        }

        log.warn("INIT DB READY");
    }

}
