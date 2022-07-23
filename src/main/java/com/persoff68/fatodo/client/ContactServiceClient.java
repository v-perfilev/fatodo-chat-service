package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignAuthConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "contact-service", primary = false,
        configuration = {FeignAuthConfiguration.class},
        qualifiers = {"feignContactServiceClient"})
public interface ContactServiceClient {

    @GetMapping("/api/check/contact")
    boolean areUsersInContactList(@RequestParam("ids") List<UUID> userIdList);

}

