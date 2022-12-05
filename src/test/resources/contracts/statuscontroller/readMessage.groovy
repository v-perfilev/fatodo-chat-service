package contracts.statuscontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'read status message'
    description 'should return status 201'
    request {
        method POST()
        url($(
                consumer(regex("/api/status/" + uuid().toString() + "/read")),
                producer("/api/status/6520f3e6-0a7f-4c32-b6f8-ba5ae3ed0bd1/read")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 201
    }
}
