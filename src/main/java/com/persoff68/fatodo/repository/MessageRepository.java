package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Optional<Message> findByIdAndUserId(UUID id, UUID senderId);
}
