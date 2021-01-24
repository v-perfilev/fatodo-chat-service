package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.MemberEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberEventRepository extends JpaRepository<MemberEvent, UUID> {
}
