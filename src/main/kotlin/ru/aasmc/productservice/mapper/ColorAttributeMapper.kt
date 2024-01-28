package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.ColorAttributeDto
import ru.aasmc.productservice.storage.model.ColorAttribute
import ru.aasmc.productservice.utils.CryptoTool

@Component
class ColorAttributeMapper(
    private val cryptoTool: CryptoTool
): AttributeMapper<ColorAttributeDto, ColorAttribute> {
    override fun toDomain(dto: ColorAttributeDto): ColorAttribute {
        return ColorAttribute(
            name = dto.attributeName,
            shortName = dto.shortName,
            isFaceted = dto.isFaceted,
            colorValues = dto.availableValues.toMutableList()
        )
    }

    override fun toDto(domain: ColorAttribute, isRequired: Boolean?): ColorAttributeDto {
        return ColorAttributeDto(
            id = cryptoTool.hashOf(domain.id!!),
            attributeName = domain.name,
            shortName = domain.shortName,
            isFaceted = domain.isFaceted,
            isRequired = isRequired,
            createdAt = domain.createdAt,
            availableValues = domain.colorValues
        )
    }
}