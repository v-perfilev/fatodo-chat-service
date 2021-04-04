package contracts.chatcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'rename chat by id'
    description 'should return status 200 and ChatDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/chat/" + uuid().toString())),
                producer("/api/chat/8f9a7cae-73c8-4ad6-b135-5bd109b51d2e")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body($(
                consumer(regex(".+")),
                producer("test")
        ))
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                "id"      : "8f9a7cae-73c8-4ad6-b135-5bd109b51d2e",
                "title"   : "test",
                "isDirect": false
        ])
    }
}
