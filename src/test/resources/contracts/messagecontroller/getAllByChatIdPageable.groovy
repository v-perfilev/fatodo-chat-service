package contracts.messagecontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get pageable messages for user'
    description 'should return status 200 and list of MessageDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/message/" + uuid().toString() + ".*")),
                producer("/api/message/b73e8418-ff4a-472b-893d-4e248ae93797")
        ))
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
                [
                        "id": "6796a82a-93c6-4fdf-bf5d-2da77ce2c338"
                ],
                [
                        "id": "6520f3e6-0a7f-4c32-b6f8-ba5ae3ed0bd1"
                ],
                [
                        "id": anyUuid()
                ],
        ])
    }
}
