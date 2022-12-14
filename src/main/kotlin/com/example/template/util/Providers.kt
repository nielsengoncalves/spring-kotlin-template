package com.example.template.util

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Component
class UuidProvider {
    fun randomUuid(): UUID = UUID.randomUUID()
}

@Component
class DatetimeProvider {
    fun currentTime(): LocalDateTime = LocalDateTime.now(Clock.systemUTC())
}
