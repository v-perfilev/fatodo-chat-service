package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.ReactionType;
import lombok.Builder;

import java.util.UUID;

public class TestReaction extends Reaction {

    @Builder
    public TestReaction(UUID messageId, UUID userId, ReactionType type) {
        super(messageId, userId, type);
    }


    public static TestReaction.TestReactionBuilder defaultBuilder() {
        return TestReaction.builder();
    }

    public Reaction toParent() {
        Reaction reaction = new Reaction();
        reaction.setUserId(getUserId());
        reaction.setMessageId(getMessageId());
        reaction.setType(getType());
        reaction.setTimestamp(getTimestamp());
        return reaction;
    }

}
