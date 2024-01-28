package ru.aasmc.productservice.mapper

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.storage.model.Category
import ru.aasmc.productservice.storage.model.Product
import ru.aasmc.productservice.storage.model.Shop
import ru.aasmc.productservice.storage.repository.CategoryRepository
import ru.aasmc.productservice.storage.repository.ShopRepository
import ru.aasmc.productservice.utils.CryptoTool

@Component
class ProductMapper(
    private val cryptoTool: CryptoTool,
    private val shopRepository: ShopRepository,
    private val categoryRepository: CategoryRepository,
    private val productVariantMapper: ProductVariantMapper
) {

    fun toDomain(dto: CreateProductRequest): Product {
        val shop = getShop(dto.shopId)
        val category = getCategory(dto.categoryName)
        val product = Product(
            shop = shop,
            category = category,
            name = dto.name,
            description = dto.description
        )
        val variants = dto.variants.map { variant ->
            productVariantMapper.toDomain(variant, product)
        }
        product.variants.addAll(variants)
        shop.products.add(product)
        return product
    }


    fun toProductResponseDto(domain: Product): ProductResponse {
        val variants = domain.variants.map { variant ->
            ProductVariantResponse(
                productId = cryptoTool.hashOf(domain.id!!),
                variantId = cryptoTool.hashOf(variant.id!!),
                variantName = variant.variantName,
                price = variant.price,
                stock = variant.stock,
                attributesCollection = variant.attributes,
                images = variant.images,
                createdAt = variant.createdAt!!
            )
        }
        return ProductResponse(
            productId = cryptoTool.hashOf(domain.id!!),
            shopId = cryptoTool.hashOf(domain.shop.id!!),
            categoryName = domain.category.name,
            name = domain.name,
            description = domain.description,
            variants = variants,
            createdAt = domain.createdAt!!
        )
    }

    private fun getShop(hashedId: String): Shop {
        return shopRepository.findById(cryptoTool.idOf(hashedId))
            .orElseThrow {
                val msg = "Cannot create product for shop with ID=${hashedId}, because shop with that ID is not found."
                throw ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
    }

    private fun getCategory(categoryName: String): Category {
        return categoryRepository.findByName(categoryName)
            .orElseThrow {
                val msg = "Cannot create product with category=$categoryName, because category with that name doesn't exist."
                throw ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
    }

}