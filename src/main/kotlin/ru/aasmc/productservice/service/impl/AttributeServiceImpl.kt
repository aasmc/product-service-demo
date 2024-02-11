package ru.aasmc.productservice.service.impl

import jakarta.transaction.Transactional
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.AttributeMappers
import ru.aasmc.productservice.service.AttributeService
import ru.aasmc.productservice.storage.model.Attribute
import ru.aasmc.productservice.storage.model.ColorAttribute
import ru.aasmc.productservice.storage.model.NumericAttribute
import ru.aasmc.productservice.storage.model.StringAttribute
import ru.aasmc.productservice.storage.repository.AttributeRepository
import ru.aasmc.productservice.storage.repository.CategoryAttributeRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
@Transactional
class AttributeServiceImpl(
    private val mapper: AttributeMappers,
    private val attributeRepository: AttributeRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
    private val cryptoTool: CryptoTool,
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
        return attributes.map { cAttr -> mapper.toDto(cAttr.attribute, cAttr.isRequired) }
    }

    override fun getAllAttributes(): List<AttributeDto> {
        return attributeRepository.findAll().map { attr ->
            mapper.toDto(attr, null)
        }
    }

    override fun getAttributeByName(name: String): AttributeDto {
        val attr = attributeRepository.findByName(name)
            .orElseThrow {
                val msg = "Attribute with name=$name not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        return mapper.toDto(attr, null)
    }

    override fun addAttributeValue(
        attributeId: String,
        dto: AttributeValueDto,
    ): AttributeValueDto {
        when(val attr = getAttributeOrThrow(attributeId)) {
            is StringAttribute -> {
                addStringAttributeValue(dto, attr)
            }
            is NumericAttribute -> {
                addNumericAttributeValue(dto, attr)
            }
            is ColorAttribute -> {
                addColorAttributeValue(dto, attr)
            }
            else -> {
                val msg = "Unknown attribute type when adding value to attribute"
                throw ProductServiceException(msg, HttpStatus.INTERNAL_SERVER_ERROR.value())
            }
        }

        log.debug("Successfully added value: {}, to attribute with ID={}", dto, attributeId)
        return dto
    }

    private fun addColorAttributeValue(
        dto: AttributeValueDto,
        attr: ColorAttribute
    ) {
        when (dto) {
            is ColorAttributeValueDto -> {
                attr.colorValues.add(dto)
            }

            else -> {
                val msg = "Attribute Value class ${dto::class} is not compatible with NumericAttribute type. "
                throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
            }
        }
    }

    private fun addNumericAttributeValue(
        dto: AttributeValueDto,
        attr: NumericAttribute
    ) {
        when (dto) {
            is NumericAttributeValueDto -> {
                attr.numericValues.add(dto)
            }

            else -> {
                val msg = "Attribute Value class ${dto::class} is not compatible with NumericAttribute type. "
                throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
            }
        }
    }

    private fun addStringAttributeValue(
        dto: AttributeValueDto,
        attr: StringAttribute
    ) {
        when (dto) {
            is StringAttributeValueDto -> {
                attr.stringValues.add(dto)
            }

            else -> {
                val msg = "Attribute Value class ${dto::class} is not compatible with StringAttribute type. "
                throw ProductServiceException(msg, HttpStatus.BAD_REQUEST.value())
            }
        }
    }

    private fun getAttributeOrThrow(attributeId: String): Attribute =
        attributeRepository.findById(cryptoTool.idOf(attributeId))
            .orElseThrow {
                val msg = "Attribute with ID=$attributeId not found."
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }

    companion object {
        private val log = LoggerFactory.getLogger(AttributeServiceImpl::class.java)
    }
}