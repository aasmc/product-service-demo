package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.AttributeValueDto
import ru.aasmc.productservice.dto.CompositeAttributeValueDto
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.AttributeMapper
import ru.aasmc.productservice.mapper.AttributeValueMapper
import ru.aasmc.productservice.service.AttributeService
import ru.aasmc.productservice.storage.model.Attribute
import ru.aasmc.productservice.storage.repository.AttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryAttributeRepository
import ru.aasmc.productservice.storage.repository.CompositeAttributeValueRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class AttributeServiceImpl(
    private val mapper: AttributeMapper,
    private val attributeRepository: AttributeRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
    private val attributeValueMapper: AttributeValueMapper,
    private val cryptoTool: CryptoTool,
    private val compositeAttributeValueRepository: CompositeAttributeValueRepository
) : AttributeService {

    override fun createAttribute(dto: AttributeDto): AttributeDto {
        val attribute = try {
            attributeRepository.save(mapper.toDomain(dto))
        } catch (e: ConstraintViolationException) {
            val msg = "Attribute with name: ${dto.attributeName} already exists"
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())

        }
        log.debug("Successfully created attribute: {}", attribute)
        return mapper.toDto(attribute, dto.isRequired)
    }

    override fun getAllAttributesForCategory(categoryName: String): List<AttributeDto> {
        val attributes = categoryAttributeRepository
            .findByCategory_Name(categoryName)
        log.debug(
            "Retrieved all attributes for category with name = {}. Attributes: {}",
            categoryName,
            attributes
        )
        return attributes.map{ cAttr -> mapper.toDto(cAttr.attribute, cAttr.isRequired)}
    }

    override fun getAllAttributes(): List<AttributeDto> {
        return attributeRepository.findAll().map(mapper::toDto)
    }

    override fun getAttributeByName(name: String): AttributeDto {
        val attr = attributeRepository.findByName(name)
            .orElseThrow {
                val msg = "Attribute with name=$name not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        return mapper.toDto(attr)
    }

    override fun addAttributeValue(
        attributeId: String,
        dto: AttributeValueDto,
    ): AttributeValueDto {
        val attr = getAttributeOrThrow(attributeId)
        if (attr.isComposite) {
            val msg = "The attribute with ID=$attributeId is composite, " +
                    "and you are trying to add a simple value: $dto."
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        val attributeValue = attributeValueMapper.toDomain(dto, attr)
        attr.attributeValues.add(attributeValue)
        log.debug("Successfully added value: {}, to attribute with ID={}", dto, attributeId)
        // if we don't flush here, attributeValue will not have an ID, because hibernate
        // defers saving it to DB until the end of transaction. But the ID is necessary
        // for attributeValueMapper.toDto(attributeValue)
        attributeRepository.flush()
        return attributeValueMapper.toDto(attributeValue)
    }

    override fun addCompositeAttributeValue(
        attributeId: String,
        dto: CompositeAttributeValueDto,
    ): CompositeAttributeValueDto {
        val attribute = getAttributeOrThrow(attributeId)
        if (!attribute.isComposite) {
            val msg = "Cannot add composite value to a non-composite attribute with ID=$attributeId"
            throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
        }
        val compositeValue = attributeValueMapper
            .toCompositeDomain(dto, attribute)
        attribute.compositeAttributeValues.add(compositeValue)
        log.debug("Successfully added composite value: {}, to attribute with ID={}", dto, attributeId)
        attributeRepository.flush()
        return attributeValueMapper.toCompositeDto(compositeValue)
    }

    override fun addValueToCompositeAttributeValue(
        compositeValueId: String,
        dto: AttributeValueDto
    ): AttributeValueDto {
        val composite = compositeAttributeValueRepository
            .findById(cryptoTool.idOf(compositeValueId))
            .orElseThrow {
                val msg = "Composite Attribute Value with ID=$compositeValueId not found."
                throw ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        val value = attributeValueMapper.toDomain(dto, composite.attribute)
        composite.value.add(value)
        attributeRepository.flush()
        return attributeValueMapper.toDto(value)
    }

    private fun getAttributeOrThrow(attributeId: String): Attribute = attributeRepository.findById(cryptoTool.idOf(attributeId))
        .orElseThrow {
            val msg = "Attribute with ID=$attributeId not found."
            ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
        }

    companion object {
        private val log = LoggerFactory.getLogger(AttributeServiceImpl::class.java)
    }
}