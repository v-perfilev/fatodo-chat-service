package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Status;
import com.persoff68.fatodo.model.StatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, StatusId> {
}
