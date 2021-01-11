package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Optional<Message> findByIdAndUserId(UUID id, UUID senderId);

    @Query(value = """
                    with unified as (
                        select   id, created_at as timestamp, null as type
                        from     ftd_chat_message
                        where    chat_id = ?1
                        union
                        select   id, timestamp, type
                        from     ftd_chat_member_event
                        where    chat_id = ?1 and user_id = ?2),

                    validated as (
                        select   id, type,
                                 sum(case type when 'ADD_MEMBER' then 1 when 'DELETE_MEMBER' then -1 else 0 end)
                                    over (order by timestamp rows unbounded preceding) *
                                 min(case type when 'CLEAR_HISTORY' then 0 when 'DELETE_DIALOG' then 0 else 1 end)
                                    over (order by timestamp rows between current row and unbounded following) valid
                        from     unified),
                        
                    valid_id as (
                        select   id
                        from     validated
                        where    type is null and valid = 1)

                    select *
                    from ftd_chat_message
                    where id in (select id from valid_id)
                    order by created_at desc 
            """, nativeQuery = true)
    Page<Message> findAllowedMessageIdsByChatIdAndUserId(UUID chatId, UUID userId, Pageable pageable);

}
