package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    String UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS = """
            unified as (
                    select id, chat_id, user_id, is_event, is_private,
                           created_at as timestamp, null as type, null as is_read
                    from ftd_chat_message
                    where chat_id = :chatId
                    union
                    select id, chat_id, null as user_id, null as is_event, null as is_private,
                           timestamp, type, null as is_read
                    from ftd_chat_member_event
                    where chat_id = :chatId and user_id = :userId)
            """;

    String UNIFIED_ALL_CHATS_MESSAGES_AND_EVENTS = """
            unified as (
                    select m.id, m.chat_id, m.user_id, m.is_event, m.is_private,
                           m.created_at as timestamp, null as type, null as is_read
                    from ftd_chat_message as m
                             right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                    where e.user_id = :userId
                    union
                    select id, chat_id, null as user_id, null as is_event, null as is_private,
                           timestamp, type, null as is_read
                    from ftd_chat_member_event
                    where user_id = :userId)
            """;

    String UNIFIED_ALL_UNREAD_MESSAGES_AND_EVENTS = """
            unified as (
                    select m.id, m.chat_id, m.user_id, m.is_event, m.is_private,
                           m.created_at as timestamp, null as type, s.type as is_read
                    from ftd_chat_message as m
                             left join ftd_chat_status as s on m.id = s.message_id
                                and s.user_id = :userId
                                and s.type = 'READ'
                             right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                    where e.user_id = :userId and m.user_id <> :userId
                    union
                    select id, chat_id, null as user_id, null as is_event, null as is_private,
                           timestamp, type, null as is_read
                    from ftd_chat_member_event
                    where user_id = :userId)
            """;


    String UNIFIED_FILTERED_CHAT_MESSAGES_AND_EVENTS = """
            unified as (
                    select id, chat_id, user_id, is_event, is_private,
                           created_at as timestamp, null as type, null as is_read
                    from ftd_chat_message
                    where chat_id in :chatIdList
                    union
                    select id, chat_id, null as user_id, null as is_event, null as is_private,
                           timestamp, type, null as is_read
                    from ftd_chat_member_event
                    where chat_id in :chatIdList and user_id = :userId)
            """;

    String VALIDATED = """
            validated as (
                         select id,
                                chat_id,
                                user_id,
                                is_event,
                                is_private,
                                timestamp,
                                type,
                                is_read,
                                sum(case
                                        when type like 'ADD_MEMBER' then 1
                                        when type like 'DELETE_MEMBER' then -1
                                        when type like 'LEAVE_CHAT' then -1
                                        else 0
                                        end)
                                    over (partition by chat_id order by timestamp rows unbounded preceding) *
                                min(case
                                        when type like 'DELETE_MEMBER' then 0
                                        when type like 'CLEAR_CHAT' then 0
                                        else 1
                                        end)
                                    over (partition by chat_id order by timestamp
                                    rows between current row and unbounded following) valid
                         from unified)
            """;

    String MESSAGE_IDS = """
            message_id as (
                         select distinct id
                         from validated
                         where type is null
                           and valid = 1
                           and (is_private = false or (is_private = true and user_id = :userId))
                        )
            """;

    String LAST_MESSAGE_IDS = """
            message_id as (
                         select distinct last_value(id)
                            over (partition by chat_id order by timestamp
                            rows between unbounded preceding and unbounded following) id
                         from validated
                         where type is null
                           and valid = 1
                           and (is_private = false or (is_private = true and user_id = :userId))
                       )
            """;

    String UNREAD_MESSAGE_IDS = """
            message_id as (
                         select distinct id
                         from validated
                         where type is null
                           and valid = 1
                           and is_event = false
                           and is_read is null
                        )
            """;

    @Query(value = "with " + UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + MESSAGE_IDS + """
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
                order by m.created_at desc
            """, countQuery = "with "
            + UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + MESSAGE_IDS + """
                select count(*)
                from message_id
            """, nativeQuery = true)
    Page<Message> findAllByChatIdAndUserId(
            @Param("chatId") UUID chatId,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Query(value = "with "
            + UNIFIED_ALL_CHATS_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + LAST_MESSAGE_IDS + """
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
                order by m.created_at desc
            """, countQuery = "with "
            + UNIFIED_ALL_CHATS_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + LAST_MESSAGE_IDS + """
                select count(*)
                from message_id
            """, nativeQuery = true)
    Page<Message> findAllByUserId(
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Query(value = "with "
            + UNIFIED_ALL_UNREAD_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + UNREAD_MESSAGE_IDS + """
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
            """, nativeQuery = true)
    List<Message> findAllUnreadMessagesByUserId(
            @Param("userId") UUID userId
    );

    @Query(value = "with " + UNIFIED_FILTERED_CHAT_MESSAGES_AND_EVENTS
            + ", " + VALIDATED + ", " + LAST_MESSAGE_IDS + """
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
                order by m.created_at desc
            """, countQuery = "with "
            + UNIFIED_FILTERED_CHAT_MESSAGES_AND_EVENTS
            + ", " + VALIDATED + ", " + LAST_MESSAGE_IDS + """
                select count(*)
                from message_id
            """, nativeQuery = true)
    List<Message> findAllByChatIdListAndUserId(
            @Param("chatIdList") List<UUID> chatIdList,
            @Param("userId") UUID userId
    );

    @Query(value = """
            select m.* from ftd_chat_message as m
            where m.chat_id = :chatId  order by m.created_at desc limit 1
            """, nativeQuery = true)
    Message findLastMessageInChat(
            @Param("chatId") UUID chatId
    );

}
