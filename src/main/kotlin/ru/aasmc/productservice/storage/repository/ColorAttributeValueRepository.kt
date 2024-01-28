package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.aasmc.productservice.storage.model.ColorAttributeValue
import java.util.Optional

interface ColorAttributeValueRepository: JpaRepository<ColorAttributeValue, Long> {

    fun findByColorHex(hex: String): Optional<ColorAttributeValue>

}