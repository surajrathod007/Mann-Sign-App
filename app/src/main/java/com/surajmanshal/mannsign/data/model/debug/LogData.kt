package com.surajmanshal.mannsign.data.model.debug

import java.time.LocalDateTime

data class LogData(
    var logId : Long = 0,
    val loggedAt : LocalDateTime,
    val exc : String
)