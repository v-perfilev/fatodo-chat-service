package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.repository.projection.ChatMessagesStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    String UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS = """
            unified as (
                    select id, chat_id, user_id, is_stub, is_event, 
                           created_at as timestamp, null as type, null as read
                    from ftd_chat_message
                    where chat_id = ?1
                    union
                    select id, chat_id, null as user_id, null as is_stub, null as is_event,
                           timestamp, type, null as read
                    from ftd_chat_member_event
                    where chat_id = ?1 and user_id = ?2)
            """;

    String UNIFIED_ALL_CHATS_MESSAGES_AND_EVENTS = """
            unified as (
                    select m.id, m.chat_id, m.user_id, m.is_stub, m.is_event,
                           m.created_at as timestamp, null as type, null as read
                    from ftd_chat_message as m
                             right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                    where e.user_id = ?1
                    union
                    select id, chat_id, null as user_id, null as is_stub, null as is_event,
                           timestamp, type, null as read
                    from ftd_chat_member_event
                    where user_id = ?1)
            """;

    String UNIFIED_ALL_UNREAD_MESSAGES_AND_EVENTS = """
            unified as (
                    select m.id, m.chat_id, m.user_id, m.is_stub, m.is_event, 
                           m.created_at as timestamp, null as type, s.type as read
                    from ftd_chat_message as m
                             left join ftd_chat_status as s on m.id = s.message_id 
                                and s.user_id = ?1
                                and s.type = 'READ'
                             right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                    where e.user_id = ?1 and m.user_id <> ?1
                    union
                    select id, chat_id, null as user_id, null as is_stub, null as is_event, 
                           timestamp, type, null as read
                    from ftd_chat_member_event
                    where user_id = ?1)
            """;

    String VALIDATED = """
            validated as (
                         select id,
                                chat_id,
                                user_id,
                                is_stub,
                                is_event,
                                timestamp,
                                type,
                                read,
                                sum(case
                                        when type like 'ADD_MEMBER' then 1
                                        when type like 'DELETE_MEMBER' then -1
                                        else 0
                                        end)
                                    over (partition by chat_id order by timestamp rows unbounded preceding) *
                                min(case
                                        when type like 'CLEAR_CHAT' then 0
                                        when type like 'DELETE_CHAT' then 0
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
                           and is_stub = false
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
                           and (is_stub = false or (is_stub = true and user_id = ?1)))
            """;

    String UNREAD_MESSAGE_IDS = """
            message_id as (
                         select distinct id
                         from validated
                         where type is null 
                           and valid = 1
                           and is_stub = false
                           and is_event = false
                           and read is null
                        )
            """;

    @Query(value = "with " + UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + MESSAGE_IDS + """
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
                order by m.created_at 
            """, countQuery = "with "
            + UNIFIED_ONE_CHAT_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + MESSAGE_IDS + """
                select count(*) 
                from message_id 
            """, nativeQuery = true)
    Page<Message> findAllByChatIdAndUserId(UUID chatId, UUID userId, Pageable pageable);


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
    Page<Message> findAllByUserId(UUID userId, Pageable pageable);

    @Query(value = """
            select if(m.id = ?1, true, false) from ftd_chat_message as m 
            where m.chat_id = ?2 and m.is_stub = false order by m.created_at desc limit 1
            """, nativeQuery = true)
    boolean isMessageIdLastInChat(UUID id, UUID chatId);

    @Query(value = "with "
            + UNIFIED_ALL_UNREAD_MESSAGES_AND_EVENTS + ", " + VALIDATED + ", " + UNREAD_MESSAGE_IDS + """
                select m.chat_id as chatId, count(*) as messagesCount
                from ftd_chat_message as m
                where id in (select id from message_id)
                group by m.chat_id
            """, nativeQuery = true)
    List<ChatMessagesStats> findAllUnreadMessages(UUID userId);

}
