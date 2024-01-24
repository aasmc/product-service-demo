package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.StringAttributeValue
import java.util.Optional

interface StringAttributeValueRepository: JpaRepository<StringAttributeValue, Long> {

    fun findByStringValue(value: String): Optional<StringAttributeValue>

}