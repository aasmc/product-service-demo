package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.StringAttributeDto
import ru.aasmc.productservice.storage.model.StringAttribute
import ru.aasmc.productservice.utils.CryptoTool

@Component
class StringAttributeMapper(
    private val cryptoTool: CryptoTool
): AttributeMapper<StringAttributeDto, StringAttribute> {

    override fun toDomain(dto: StringAttributeDto): StringAttribute {
        return StringAttribute(
            name = dto.attributeName,
            shortName = dto.shortName,
            isFaceted = dto.isFaceted,
            stringValues = dto.availableValues.toMutableList()
        )
    }

    override fun toDto(domain: StringAttribute, isRequired: Boolean?): StringAttributeDto {
        return StringAttributeDto(
            id = cryptoTool.hashOf(domain.id!!),
            attributeName = domain.name,
            shortName = domain.shortName,
            isFaceted = domain.isFaceted,
            isRequired = isRequired,
            createdAt = domain.createdAt,
            availableValues = domain.stringValues
        )
    }

}