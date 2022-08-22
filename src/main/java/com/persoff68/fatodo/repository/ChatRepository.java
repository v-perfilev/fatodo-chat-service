package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    String USER_CHAT_IDS = """
            chat_id as (
                           select c.chat_id as id
                           from (
                                    select chat_id,
                                           sum(case
                                                   when type like 'ADD_MEMBER' then 1
                                                   when type like 'DELETE_MEMBER' then -1
                                                   when type like 'LEAVE_CHAT' then -1
                                                   else 0
                                                   end)
                                           over (partition by chat_id order by date rows unbounded preceding) sum
                                    from ftd_chat_member_event e
                                    where user_id = :userId
                                ) c
                           where c.sum > 0)
            """;

    @Query(value = "with "
            + USER_CHAT_IDS + """
                select c.*
                from ftd_chat as c
                where id in (select id from chat_id)
            """, nativeQuery = true)
    List<Chat> findAllByUserId(@Param("userId") UUID userId);

    @Query(value = "with "
            + USER_CHAT_IDS + """
                select c.*
                from ftd_chat as c
                where c.id in (select id from chat_id) and c.id in :chatIds
            """, nativeQuery = true)
    List<Chat> findAllByUserIdAndIds(@Param("userId") UUID userId,@Param("chatIds") List<UUID> chatIds);

    @Query(value = """
            select c from Chat c where c.isDirect = true and c.id in
            (select e.chat.id from MemberEvent e where e.userId in ?1
            group by e.chat.id having count(e.chat.id) = 2)
            """)
    Optional<Chat> findDirectChat(List<UUID> userIdList);

}
