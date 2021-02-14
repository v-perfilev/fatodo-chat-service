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

    @Query(value = """
            select c from Chat c where c.isDirect = true and c.id in
            (select e.chat.id from MemberEvent e where e.userId in ?1
            group by e.chat.id having count(e.chat.id) = 2)
            """)
    Optional<Chat> findDirectChat(List<UUID> userIdList);

}
