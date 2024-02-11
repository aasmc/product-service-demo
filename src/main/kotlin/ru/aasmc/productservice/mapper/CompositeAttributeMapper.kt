package ru.aasmc.productservice.mapper

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.CompositeAttributeDto
import ru.aasmc.productservice.storage.model.CompositeAttribute
import ru.aasmc.productservice.utils.CryptoTool

@Component
class CompositeAttributeMapper(
    // need Lazy to work around circular dependency
    @Lazy
    private val mappers: AttributeMappers,
    private val cryptoTool: CryptoTool
): AttributeMapper<CompositeAttributeDto, CompositeAttribute> {
    override fun toDomain(dto: CompositeAttributeDto): CompositeAttribute {
        return CompositeAttribute(
            name = dto.attributeName,
            shortName = dto.shortName,
            isFaceted = dto.isFaceted,
            subAttributes = dto.subAttributes.map(mappers::toDomain).toHashSet()
        )
    }

    override fun toDto(domain: CompositeAttribute, isRequired: Boolean?): CompositeAttributeDto {
        return CompositeAttributeDto(
            id = cryptoTool.hashOf(domain.id!!),
            attributeName = domain.name,
            shortName = domain.shortName,
            isFaceted = domain.isFaceted,
            isRequired = isRequired,
            createdAt = domain.createdAt,
            subAttributes = domain.subAttributes.map{ attr -> mappers.toDto(attr, isRequired) }
        )
    }
}