package contracts.chatcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get unread messages map'
    description 'should return status 200 and map of uuid'
    request {
        method GET()
        url("/api/chat/unread")
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                "b73e8418-ff4a-472b-893d-4e248ae93797": [
                        "6520f3e6-0a7f-4c32-b6f8-ba5ae3ed0bd1"
                ]
        ])
    }
}
