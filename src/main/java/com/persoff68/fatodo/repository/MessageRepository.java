package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = """
                with unified as (
                    select id, chat_id, created_at as timestamp, null as type
                    from ftd_chat_message
                    where chat_id = ?1
                    union
                    select id, chat_id, timestamp, type
                    from ftd_chat_member_event
                    where chat_id = ?1 and user_id = ?2),
                
                     validated as (
                         select id,
                                chat_id,
                                timestamp,
                                type,
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
                                    over (partition by chat_id order by timestamp rows between current row and unbounded following) valid
                         from unified),
                
                     message_id as (
                         select distinct id
                         from validated
                         where type is null
                           and valid = 1)
                
                select m.*
                from ftd_chat_message as m
                where id in (select id from message_id)
                  and m.is_stub = false
                order by m.created_at desc 
            """, nativeQuery = true)
    Page<Message> findAllByChatIdAndUserId(UUID chatId, UUID userId, Pageable pageable);

    @Query(value = """
                with unified as (
                    select m.id, m.chat_id, m.user_id, m.is_stub, m.created_at as timestamp, null as type
                    from ftd_chat_message as m
                             right join ftd_chat_member_event as e on m.chat_id = e.chat_id
                    where e.user_id = ?1
                    union
                    select id, chat_id, null as user_id, null as is_stub, timestamp, type
                    from ftd_chat_member_event
                    where user_id = ?1),
                
                     validated as (
                         select id,
                                chat_id,
                                user_id,
                                is_stub,
                                timestamp,
                                type,
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
                                    over (partition by chat_id order by timestamp rows between current row and unbounded following) valid
                         from unified),
                
                     message_id as (
                         select distinct last_value(id)
                                                    over (partition by chat_id order by timestamp rows between unbounded preceding and unbounded following) last_id
                         from validated
                         where type is null
                           and valid = 1
                           and (is_stub = false or (is_stub = true and user_id = ?1)))
                
                select m.*
                from ftd_chat_message as m
                where id in (select last_id from message_id)
                order by m.created_at desc 
            """, nativeQuery = true)
    Page<Message> findAllByUserId(UUID userId, Pageable pageable);

}
