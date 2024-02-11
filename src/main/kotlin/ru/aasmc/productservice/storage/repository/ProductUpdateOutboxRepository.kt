package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.ProductUpdateOutbox

interface ProductUpdateOutboxRepository: JpaRepository<ProductUpdateOutbox, Long> {
}