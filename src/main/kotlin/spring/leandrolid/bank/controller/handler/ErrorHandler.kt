package spring.leandrolid.bank.controller.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun illegalArgumentException(
        request: HttpServletRequest,
        exception: Exception
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.message!!)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun runtimeException(
        request: HttpServletRequest,
        exception: Exception
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.message!!)
        return ResponseEntity.badRequest().body(errorResponse)
    }
}

data class ErrorResponse(val statusCode: Int, val message: String)