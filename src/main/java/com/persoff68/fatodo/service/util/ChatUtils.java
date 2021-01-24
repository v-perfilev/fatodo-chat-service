package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatUtils {

    private ChatUtils() {
    }

    public static List<UUID> getActiveUserIdList(Chat chat) {
        List<MemberEvent> memberEventList = chat.getMemberEvents();

        return memberEventList.stream()
                .collect(Collectors.groupingBy(
                        MemberEvent::getUserId,
                        Collectors.groupingBy(
                                MemberEvent::getType,
                                Collectors.counting()
                        )
                ))
                .entrySet().stream()
                .filter(entry -> {
                    Map<MemberEvent.Type, Long> countMap = entry.getValue();
                    long addEventCount = Optional.ofNullable(countMap.get(MemberEvent.Type.ADD_MEMBER))
                            .orElse(0L);
                    long deleteEventCount = Optional.ofNullable(countMap.get(MemberEvent.Type.DELETE_MEMBER))
                            .orElse(0L);
                    return addEventCount > deleteEventCount;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
