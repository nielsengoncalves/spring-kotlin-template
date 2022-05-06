package com.example.template.exception

import com.example.template.controller.response.ErrorResponse
import com.example.template.util.log
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ErrorResponse =
        handle(HttpStatus.BAD_REQUEST, exception.message, exception, Level.INFO)

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ErrorResponse =
        handle(HttpStatus.NOT_FOUND, exception.message, exception, Level.INFO)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleException(exception: Exception): ErrorResponse =
        handle(HttpStatus.NOT_FOUND, exception.message, exception, Level.ERROR)

    private fun handle(status: HttpStatus, message: String?, exception: Throwable, level: Level): ErrorResponse {
        logger.log("Globally handling exception with status `$status`", exception, level)
        return ErrorResponse(message)
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}
