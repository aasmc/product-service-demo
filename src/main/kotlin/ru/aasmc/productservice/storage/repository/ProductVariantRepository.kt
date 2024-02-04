package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.aasmc.productservice.storage.model.ProductVariant
import java.util.Optional

interface ProductVariantRepository : JpaRepository<ProductVariant, Long> {

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

    @Transactional
    @Modifying
    @Query("""
        update product_variants
        set sku_collection = jsonb_set(sku_collection, '{skus,0,stock}', '100', false)
        where id = 1
    """, nativeQuery = true)
    fun update()

}