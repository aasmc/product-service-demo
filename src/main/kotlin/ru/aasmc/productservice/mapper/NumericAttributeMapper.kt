package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.NumericAttributeDto
import ru.aasmc.productservice.storage.model.NumericAttribute
import ru.aasmc.productservice.utils.CryptoTool

@Component
class NumericAttributeMapper(
    private val cryptoTool: CryptoTool
): AttributeMapper<NumericAttributeDto, NumericAttribute> {
    override fun toDomain(dto: NumericAttributeDto): NumericAttribute {
        return NumericAttribute(
            name = dto.attributeName,
            shortName = dto.shortName,
            isFaceted = dto.isFaceted,
            numericValues = dto.availableValues.toMutableList()
        )
    }

    override fun toDto(domain: NumericAttribute, isRequired: Boolean?): NumericAttributeDto {
        return NumericAttributeDto(
            id = cryptoTool.hashOf(domain.id!!),
            attributeName = domain.name,
            shortName = domain.shortName,
            isFaceted = domain.isFaceted,
            isRequired = isRequired,
            createdAt = domain.createdAt,
            availableValues = domain.numericValues
        )
    }
}