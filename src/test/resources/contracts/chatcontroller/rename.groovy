package contracts.chatcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'rename chat by id'
    description 'should return status 200 and ChatDTO'
    request {
        method PUT()
        url($(
                consumer(regex("/api/chat/" + uuid().toString())),
                producer("/api/chat/b73e8418-ff4a-472b-893d-4e248ae93797")
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
                producer([
                        "title": "test"
                ])
        ))
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                "id"      : "b73e8418-ff4a-472b-893d-4e248ae93797",
                "title"   : "test",
                "isDirect": false
        ])
    }
}
