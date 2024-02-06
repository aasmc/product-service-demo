package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.aasmc.productservice.dto.AppImage
import ru.aasmc.productservice.storage.model.ProductVariant
import java.math.BigDecimal
import java.util.Optional

interface ProductVariantRepository : JpaRepository<ProductVariant, Long> {

    @Modifying
    @Query("""
        UPDATE product_variants
        SET image_collection = jsonb_set(
            image_collection,
            '{images}',
            COALESCE(
                (SELECT jsonb_agg(elem) 
                FROM jsonb_array_elements(image_collection->'images') elem 
                WHERE elem->>'url' != :imageUrl),
                '[]'\:\:jsonb
            )    
        )
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeImage(@Param("variantId") variantId: Long, @Param("imageUrl") imageUrl: String)

    @Modifying
    @Query("""
        UPDATE product_variants
        SET image_collection = jsonb_set(image_collection, '{images}', (image_collection->'images')\:\:jsonb || to_jsonb(:photo\:\:jsonb), false)
        WHERE id = :variantId
    """, nativeQuery = true)
    fun addImage(@Param("variantId") variantId: Long, @Param("photo") photo: String)

    @Modifying
    @Query(value = """
                    WITH sku_path AS (
                        SELECT ('{skus,'||index - 1||',stock}')\:\:text[] AS path
                        FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
                        WHERE id = :variantId AND sku_element->>'sku' = :sku
                    )
                    UPDATE product_variants
                    SET sku_collection = jsonb_set(sku_collection, sku_path.path, to_jsonb(:newStock), false)
                    FROM sku_path 
                    WHERE id = :variantId
""", nativeQuery = true)
    fun updateSkuStock(@Param("sku") sku: String, @Param("variantId") variantId: Long, @Param("newStock") newStock: Int)

    @Modifying
    @Query(value = """
                    WITH sku_path AS (
                        SELECT ('{skus,'||index - 1||',price}')\:\:text[] AS path
                        FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
                        WHERE id = :variantId AND sku_element->>'sku' = :sku
                    )
                    UPDATE product_variants
                    SET sku_collection = jsonb_set(sku_collection, sku_path.path, to_jsonb(:newPrice), false)
                    FROM sku_path 
                    WHERE id = :variantId
""", nativeQuery = true)
    fun updateSkuPrice(@Param("sku") sku: String, @Param("variantId") variantId: Long, @Param("newPrice") newPrice: BigDecimal)

}