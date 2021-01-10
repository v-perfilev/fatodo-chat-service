package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Reaction.ReactionId> {

    List<Reaction> findAllByMessageId(UUID messageId);

}
