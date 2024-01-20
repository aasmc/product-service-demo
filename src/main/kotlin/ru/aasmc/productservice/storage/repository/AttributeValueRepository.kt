package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.AttributeValue
import java.util.*

interface AttributeValueRepository: JpaRepository<AttributeValue, Long> {

    fun findByValue(value: String): Optional<AttributeValue>

}