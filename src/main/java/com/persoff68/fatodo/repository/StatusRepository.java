package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {

    List<Status> findAllByMessageId(UUID messageId);

}
