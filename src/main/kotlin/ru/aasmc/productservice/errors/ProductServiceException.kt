package ru.aasmc.productservice.errors

class ProductServiceException(
    message: String,
    val status: Int
): RuntimeException(message)