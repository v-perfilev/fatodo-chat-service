package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID> {
}
