package jk.codespace.restapi.exception

import com.fasterxml.jackson.annotation.JsonProperty
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class RestAPIAdvice {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(AppException::class)
    fun handleAppException(req: HttpServletRequest, ex: AppException): ResponseEntity<JsonError> {
        log.error(ex) { "Application Exception: status=${ex.httpStatus}, reason=${ex.errorMessage}, " }
        return ResponseEntity.status(ex.httpStatus)
            .body(
                JsonError(
                    timestamp = Date(),
                    status = ex.httpStatus.value(),
                    error = ex.httpStatus.reasonPhrase,
                    message = ex.message,
                    path = req.servletPath
                )
            )
    }

    data class JsonError(
        @JsonProperty("timestamp") val timestamp: Date,
        @JsonProperty("status") val status: Int,
        @JsonProperty("error") val error: String?,
        @JsonProperty("message") val message: String?,
        @JsonProperty("path") val path: String
    )

}