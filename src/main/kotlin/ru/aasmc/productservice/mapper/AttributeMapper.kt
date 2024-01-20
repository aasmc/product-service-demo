package ru.aasmc.productservice.mapper

import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.Attribute
import ru.aasmc.productservice.storage.model.AttributeValue
import ru.aasmc.productservice.storage.model.AttributeValueComponent
import ru.aasmc.productservice.storage.repository.AttributeValueComponentRepository
import ru.aasmc.productservice.storage.repository.AttributeValueRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class AttributeMapper(
    private val attributeValueRepository: AttributeValueRepository,
    private val componentRepository: AttributeValueComponentRepository,
    private val cryptoTool: CryptoTool
) {

    fun toDomain(dto: CreateAttributeRequest): Attribute {
        val attribute = Attribute(name = dto.attributeName)
        val attributeValues = dto.attributeValues
            .map { attrValueDto ->
                getAttributeValueOrCreate(attrValueDto, attribute)
            }
            .toHashSet()
        attribute.attributeValues.addAll(attributeValues)
        return attribute
    }

    fun toDto(domain: Attribute): AttributeResponse {
        return AttributeResponse(
            id = cryptoTool.hashOf(domain.id!!),
            attributeName = domain.name,
            attributeValues = mapAttributeValues(domain.attributeValues)
        )
    }

    private fun mapAttributeValues(
        attributeValues: Set<AttributeValue>
    ): List<AttributeValueResponseDto> =
        attributeValues.map { value ->
            AttributeValueResponseDto(
                id = cryptoTool.hashOf(value.id!!),
                isComposite = value.isComposite,
                attributeValueName = value.value,
                components = mapComponents(value.components)
            )
        }

    private fun mapComponents(
        components: Set<AttributeValueComponent>
    ): List<AttributeValueComponentResponseDto> =
        components.map { component ->
            AttributeValueComponentResponseDto(
                id = cryptoTool.hashOf(component.id!!),
                componentName = component.componentName,
                componentValue = component.componentValue
            )
        }

    private fun getAttributeValueOrCreate(dto: AttributeValueDto, attribute: Attribute): AttributeValue =
        attributeValueRepository
            .findByValue(dto.attributeValueName)
            .orElseGet {
                val value = AttributeValue(
                    attribute = attribute,
                    value = dto.attributeValueName,
                    isComposite = dto.isComposite
                )
                val components = dto.components
                    .map { componentDto ->
                        getAttributeValueComponentOrCreate(componentDto, value)
                    }
                    .toHashSet()
                value.components.addAll(components)
                value
            }

    private fun getAttributeValueComponentOrCreate(
        dto: AttributeValueComponentDto,
        attributeValue: AttributeValue
    ): AttributeValueComponent =
        componentRepository
            .findByComponentNameAndComponentValue(dto.componentName, dto.componentValue)
            .orElseGet {
                AttributeValueComponent(
                    attributeValue = attributeValue,
                    componentName = dto.componentName,
                    componentValue = dto.componentValue
                )
            }
}