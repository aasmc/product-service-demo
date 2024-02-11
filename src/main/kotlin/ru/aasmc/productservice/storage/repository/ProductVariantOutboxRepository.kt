package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.ProductVariantOutbox

interface ProductVariantOutboxRepository: JpaRepository<ProductVariantOutbox, Long> {
}