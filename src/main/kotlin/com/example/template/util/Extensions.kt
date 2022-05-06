package com.example.template.util

import org.slf4j.Logger
import org.slf4j.event.Level

fun Logger.log(message: String?, exception: Throwable?, level: Level) {
    when (level) {
        Level.DEBUG -> this.debug(message, exception)
        Level.TRACE -> this.trace(message, exception)
        Level.ERROR -> this.error(message, exception)
        Level.WARN -> this.warn(message, exception)
        Level.INFO -> this.info(message, exception)
    }
}
