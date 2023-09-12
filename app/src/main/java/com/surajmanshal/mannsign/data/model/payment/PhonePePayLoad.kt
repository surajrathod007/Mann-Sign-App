package com.surajmanshal.mannsign.data.model.payment

import com.surajmanshal.mannsign.data.response.SimpleResponse

data class PhonePePayLoad(
    val simpleResponse: SimpleResponse,
    val base64Payload: String?,
    val checksum: String?
)