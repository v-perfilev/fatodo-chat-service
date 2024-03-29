package com.persoff68.fatodo.service.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.MemberEvent;
import com.persoff68.fatodo.model.constant.MemberEventType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChatUtils {

    private ChatUtils() {
    }

    public static boolean isUserInChat(Chat chat, UUID userId) {
        List<UUID> activeUserIdLIst = getActiveUserIdList(chat);
        return activeUserIdLIst.contains(userId);
    }

    public static boolean isAnyUserInChat(Chat chat, List<UUID> userIdList) {
        List<UUID> activeUserIdList = getActiveUserIdList(chat);
        return !Collections.disjoint(activeUserIdList, userIdList);
    }

    public static boolean wasUserInChat(Chat chat, UUID userId) {
        List<MemberEvent> memberEventList = chat.getMemberEvents();

        return memberEventList.stream()
                .anyMatch(memberEvent -> memberEvent.getUserId().equals(userId));
    }

    public static boolean hasUserDeletedChat(Chat chat, UUID userId) {
        List<MemberEvent> memberEventList = chat.getMemberEvents();

        Optional<MemberEvent> lastMemberEventOptional = memberEventList.stream()
                .filter(memberEvent -> memberEvent.getUserId().equals(userId))
                .max(Comparator.comparing(MemberEvent::getDate));

        return lastMemberEventOptional
                .map(memberEvent -> memberEvent.getType().equals(MemberEventType.DELETE_MEMBER))
                .orElse(false);
    }

    public static List<UUID> getActiveUserIdList(Chat chat) {
        return getActiveUserIdList(chat.getMemberEvents());
    }

    public static List<UUID> getActiveUserIdList(List<MemberEvent> memberEventList) {

        Table<UUID, MemberEventType, Integer> memberEventCountTable = getMemberEventCountTable(memberEventList);

        return memberEventCountTable.rowMap().entrySet().stream()
                .filter(entry -> {
                    Map<MemberEventType, Integer> countMap = entry.getValue();
                    int addEventCount = Optional.ofNullable(countMap.get(MemberEventType.ADD_MEMBER))
                            .orElse(0);
                    int deleteEventCount = Optional.ofNullable(countMap.get(MemberEventType.DELETE_MEMBER))
                            .orElse(0);
                    int leaveEventCount = Optional.ofNullable(countMap.get(MemberEventType.LEAVE_CHAT))
                            .orElse(0);
                    return addEventCount > deleteEventCount + leaveEventCount;
                })
                .map(Map.Entry::getKey)
                .toList();
    }

    private static Table<UUID, MemberEventType, Integer> getMemberEventCountTable(List<MemberEvent> memberEventList) {
        return memberEventList.stream()
                .collect(Tables.toTable(
                        MemberEvent::getUserId,
                        MemberEvent::getType,
                        memberEvent -> 1,
                        Integer::sum,
                        HashBasedTable::create
                ));
    }

}
