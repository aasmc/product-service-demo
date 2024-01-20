package ru.aasmc.productservice.dto

data class CreateSellerRequest(
    val firstName: String,
    val lastName: String
)

data class CreateSellerResponse(
    val id: String
)

data class SellerResponse(
    val id: String,
    val firstName: String,
    val lastName: String
)


