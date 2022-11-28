package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.constant.MemberEventType;
import com.persoff68.fatodo.repository.ChatRepository;
import com.persoff68.fatodo.repository.MemberEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final ChatRepository chatRepository;
    private final MemberEventRepository memberEventRepository;

    @Transactional
    public void deleteAccountPermanently(UUID userId) {
        List<Chat> chatList = chatRepository.findAllByUserId(userId);

        List<MemberEvent> memberEventList = chatList.stream()
                .map(chat -> new MemberEvent(chat, userId, MemberEventType.DELETE_MEMBER))
                .toList();

        memberEventRepository.saveAll(memberEventList);
    }

}
