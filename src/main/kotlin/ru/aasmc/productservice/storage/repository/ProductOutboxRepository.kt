package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.ProductOutbox

interface ProductOutboxRepository: JpaRepository<ProductOutbox, Long> {
}