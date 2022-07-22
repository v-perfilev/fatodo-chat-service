package contracts.infocontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get all message info by ids'
    description 'should return status 200 and list of MessageInfoDTO'
    request {
        method POST()
        url("/api/info/messages")
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
                        "6796a82a-93c6-4fdf-bf5d-2da77ce2c338"
                ])
        ))
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([[
                      "id"  : anyUuid(),
                      "text": anyNonBlankString()
              ]])
    }
}
