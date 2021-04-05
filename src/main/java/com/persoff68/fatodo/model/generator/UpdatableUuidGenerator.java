package com.persoff68.fatodo.model.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class UpdatableUuidGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Serializable id = session.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, session);
        return id != null ? id : super.generate(session, object);
    }

}
