package ru.aasmc.productservice.errors

import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DefaultControllerAdvice {

    @ExceptionHandler(ProductServiceException::class)
    fun handleProductServiceException(
        ex: ProductServiceException
    ): ResponseEntity<ErrorResponse> {
        log.error("Handling ProductServiceException. Message: {}, status: {}", ex.message, ex.status)
        val response = ErrorResponse(ex.message.orEmpty(), ex.status)
        return ResponseEntity(response, HttpStatusCode.valueOf(ex.status))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleDbConstraintViolations(
        ex: ConstraintViolationException
    ): ResponseEntity<ErrorResponse> {
        log.error("Handling constraint violation exception originated in DB. Message: {}. Constraint name: {}", ex.message, ex.constraintName)
        val response = ErrorResponse(ex.message ?: "Database constraint violation", HttpStatus.BAD_REQUEST.value())
        return ResponseEntity.badRequest().body(response)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultControllerAdvice::class.java)
    }

}