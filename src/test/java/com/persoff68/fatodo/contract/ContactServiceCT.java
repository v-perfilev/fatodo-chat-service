package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.ContactServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:contactservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class ContactServiceCT {

    @Autowired
    ContactServiceClient contactServiceClient;

    @Test
    void testAreUsersInContactList() {
        List<UUID> userIdList = Collections.singletonList(UUID.randomUUID());
        boolean areUsersInContactList = contactServiceClient.areUsersInContactList(userIdList);
        assertThat(areUsersInContactList).isFalse();
    }

}
