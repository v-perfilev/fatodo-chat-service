package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("""
            select c from Chat c where c.isDirect = true and c.id in
            (select me.chatId from MemberEvent me where me.userId in ?1
            group by me.chatId having count(me.chatId) = 2)
            """)
    Optional<Chat> findDirectChat(List<UUID> userIdList);

    @Query("""
            select c from Chat c join c.memberEvents as m 
            where m.userId = ?1
            order by c.lastModifiedAt
            """)
    Page<Chat> findAllByUserId(UUID userId, Pageable pageable);

    @Query("""
            select c from Chat c join c.memberEvents as m
            where m.userId = ?1 and c.lastModifiedAt > ?2
            order by c.lastModifiedAt desc
            """)
    List<Chat> findAllNewByUserId(UUID userId, Date date);

    @Query(value = """
            with unified as (
                select   m.id, m.chat_id, created_at as timestamp, null as type
                from     ftd_chat_message as m
                right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                where    e.user_id = ?1
                union
                select   id, chat_id, timestamp, type
                from     ftd_chat_member_event
                where    user_id = ?1),

            validated_message as (
                select   id, chat_id, type, timestamp,
                         sum(case type when 'ADD_MEMBER' then 1 when 'DELETE_MEMBER' then -1 else 0 end)
                            over (order by timestamp rows unbounded preceding) *
                         min(case type when 'CLEAR_HISTORY' then 0 when 'DELETE_DIALOG' then 0 else 1 end)
                            over (order by timestamp rows between current row and unbounded following) valid
                from     unified),
                
            last_message_id as (
                select distinct last_value(id)
                                    over (partition by chat_id order by timestamp) id
                from     validated_message
                where    type is null and valid = 1),
                
            last_message as (
                select * 
                from ftd_chat_message
                where id in (select id from last_message_id)),
                
            validated_chat as (
                select   chat_id,
                         last_value(type) over (partition by chat_id order by timestamp) last_event
                from     unified),
                
            chat_id as (
                select distinct chat_id
                from validated_chat
                where last_event <> 'DELETE_DIALOG'
            )
            
            select c.*, m.id as last_allowed_message_id 
            from ftd_chat as c
            join last_message as m on c.id = m.chat_id
            where c.id in (select id from chat_id)
            order by m.created_at desc
            """, nativeQuery = true)
    Page<Chat> findChatsByUserId(UUID userId, Pageable pageable);

}
