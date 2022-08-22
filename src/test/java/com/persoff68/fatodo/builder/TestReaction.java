package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.model.Reaction;
import com.persoff68.fatodo.model.constant.ReactionType;
import lombok.Builder;

import java.util.UUID;

public class TestReaction extends Reaction {

    @Builder
    public TestReaction(Message message, UUID userId, ReactionType type) {
        super();
        super.setMessage(message);
        super.setUserId(userId);
        super.setType(type);
    }

    public static TestReactionBuilder defaultBuilder() {
        return TestReaction.builder();
    }

    public Reaction toParent() {
        Reaction reaction = new Reaction();
        reaction.setUserId(getUserId());
        reaction.setMessage(getMessage());
        reaction.setType(getType());
        reaction.setTimestamp(getTimestamp());
        return reaction;
    }

}
