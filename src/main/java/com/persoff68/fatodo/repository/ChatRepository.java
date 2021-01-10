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

    String QUERY = """
                with unified as (
                    select   id, text, timestamp, null as type
                    from     ftd_chat_message
                    union
                    select   id, null, timestamp, type
                    from     member_event
                    where    user_id = 1),
                            
                validated as (
                    select   unified.*,
                             sum(case type when 1 then 1 when 2 then -1 else 0 end)
                                over (order by timestamp rows unbounded preceding) *
                             min(case type when 3 then 0 else 1 end)
                                over (order by timestamp rows between current row and unbounded following) valid
                    from     unified
                    order by timestamp)
                            
                select   id, text, timestamp
                from     validated
                where    type is null and valid = 1
                order by timestamp
            """;

    @Query("select c from Chat c where c.isDirect = true and c.id in "
            + "(select me.chatId from MemberEvent me where me.userId in ?1 "
            + "group by me.chatId having count(me.chatId) = 2)")
    Optional<Chat> findDirectChat(List<UUID> userIdList);

    @Query("select c from Chat c join c.memberEvents as m "
            + "where m.userId = ?1 "
            + "order by c.lastModifiedAt")
    Page<Chat> findAllByUserId(UUID userId, Pageable pageable);

    @Query("select c from Chat c join c.memberEvents as m "
            + "where m.userId = ?1 "
            + "and c.lastModifiedAt > ?2 "
            + "order by c.lastModifiedAt desc")
    List<Chat> findAllNewByUserId(UUID userId, Date date);

//    @Query(value = """
//                with unified as (
//                    select   id, text, timestamp, null as type
//                    from     ftd_chat_message
//                    union
//                    select   id, null, timestamp, type
//                    from     member_event
//                    where    user_id = 1),
//
//                validated as (
//                    select   unified.*,
//                             sum(case type when 1 then 1 when 2 then -1 else 0 end)
//                                over (order by timestamp rows unbounded preceding) *
//                             min(case type when 3 then 0 else 1 end)
//                                over (order by timestamp rows between current row and unbounded following) valid
//                    from     unified
//                    order by timestamp)
//
//                select   id, text, timestamp
//                from     validated
//                where    type is null and valid = 1
//                order by timestamp
//        """, nativeQuery = true)
//    List<Chat> testFind();

}
