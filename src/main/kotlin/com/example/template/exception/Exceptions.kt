package com.example.template.exception

open class ResourceNotFoundException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
