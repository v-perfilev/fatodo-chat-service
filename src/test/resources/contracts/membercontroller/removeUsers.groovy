package contracts.membercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'remove users from chat'
    description 'should return status 200'
    request {
        method POST()
        url($(
                consumer(regex("/api/members/remove/" + uuid().toString())),
                producer("/api/members/remove/b73e8418-ff4a-472b-893d-4e248ae93797")
        ))
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body($(
                consumer(regex(".+")),
                producer(["1b53a48c-2da5-4489-ac8a-e246c6445333"])
        ))
    }
    response {
        status 200
    }
}
