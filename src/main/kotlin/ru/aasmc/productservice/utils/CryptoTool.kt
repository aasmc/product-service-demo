package ru.aasmc.productservice.utils

import org.hashids.Hashids
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.errors.ProductServiceException

@Component
class CryptoTool(
    private val hashids: Hashids
) {

    fun hashOf(value: Long): String = hashids.encode(value)

    fun idOf(value: String): Long {
        val res = hashids.decode(value)
        if (res != null && res.isNotEmpty()) {
            return res[0]
        }
        val msg = "Error decrypting id from value: $value"
        throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
    }

}