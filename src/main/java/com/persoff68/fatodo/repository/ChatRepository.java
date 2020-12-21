package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("select c from Chat c where c.isDirect = true and c.id in "
            + "(select cm.chatId from ChatMember cm where cm.userId in ?1 "
            + "group by cm.chatId having count(cm.chatId) > 1)")
    Optional<Chat> findDirectChat(List<UUID> userIdList);
}
