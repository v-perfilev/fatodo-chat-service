package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Reaction.ReactionId> {
}
