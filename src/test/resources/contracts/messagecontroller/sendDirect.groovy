package contracts.messagecontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'send direct message'
    description 'should return status 201 and MessageDTO'
    request {
        method POST()
        url($(
                consumer(regex("/api/message/direct/" + uuid().toString())),
                producer("/api/message/direct/" + uuid().generate())
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
                        "text": "test"
                ])
        ))
    }
    response {
        status 201
        headers {
            contentType applicationJson()
        }
        body([
                "id"  : anyUuid(),
                "text": "test"
        ])
    }
}
