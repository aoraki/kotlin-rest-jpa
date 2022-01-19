package jk.codespace.restapi.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

class AppException(
    val httpStatus: HttpStatus,
    val errorMessage: String? = null
) : RuntimeException(errorMessage) {
    constructor(statusCode: Int, reason: String? = null) : this(
        httpStatus = HttpStatus.valueOf(statusCode),
        errorMessage = reason
    )
}