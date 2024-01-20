package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.AttributeValueComponent
import java.util.*

interface AttributeValueComponentRepository: JpaRepository<AttributeValueComponent, Long> {

    fun findByComponentNameAndComponentValue(name: String, value: String): Optional<AttributeValueComponent>

}