package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.CompositeAttributeValue
import java.util.Optional

interface CompositeAttributeValueRepository : JpaRepository<CompositeAttributeValue, Long> {

    fun findByName(name: String): Optional<CompositeAttributeValue>

}