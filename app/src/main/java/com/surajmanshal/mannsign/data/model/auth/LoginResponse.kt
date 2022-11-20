package com.surajmanshal.mannsign.data.model.auth

import com.surajmanshal.mannsign.data.response.SimpleResponse


data class LoginResponse(
    val simpleResponse: SimpleResponse,
    val user: User = User()
)
