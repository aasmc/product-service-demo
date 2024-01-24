package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.NumericAttributeValue
import java.util.Optional

interface NumericAttributeValueRepository: JpaRepository<NumericAttributeValue, Long> {

    fun findByNumValueAndNumRuValueAndNumUnit(numValue: Number, numRuValue: Number?, numUnit: String): Optional<NumericAttributeValue>

}