package ru.aasmc.productservice.docs.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class CreateSellerRequest(
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    @field:Email
    val email: String,
)

data class Seller(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val shops: List<Shop> = emptyList()
)