package ru.aasmc.productservice.storage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.productservice.storage.model.ProductVariant
import java.math.BigDecimal

interface ProductVariantRepository : JpaRepository<ProductVariant, Long> {



    @Query("select pv.id from ProductVariant pv where pv.id = :variantId")
    fun getIdIfPresent(@Param("variantId") variantId: Long): Long?

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        ),
         sub_attr_path AS (
            SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
            CAST((index - 1) AS INTEGER) AS idx
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            sap.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues') elem
                         WHERE elem->>'colorValue' != :colorValue 
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path ap, sub_attr_path sap 
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeCompositeAttributeColorValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("subAttrName") subAttrName: String,
        @Param("colorValue") colorValue: String,
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        ),
         sub_attr_path AS (
            SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
            CAST((index - 1) AS INTEGER) AS idx
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            sap.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues') elem
                         WHERE elem->>'stringValue' != :stringValue 
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path ap, sub_attr_path sap 
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeCompositeAttributeStringValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("subAttrName") subAttrName: String,
        @Param("stringValue") stringValue: String,
    )


    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        ),
         sub_attr_path AS (
            SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
            CAST((index - 1) AS INTEGER) AS idx
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            sap.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues') elem
                         WHERE CAST(elem->>'numValue' AS DOUBLE PRECISION) != :numValue 
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path ap, sub_attr_path sap 
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeCompositeAttributeNumericValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("subAttrName") subAttrName: String,
        @Param("numValue") numValue: Double,
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            attr_path.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->attr_path.idx->'availableValues') elem
                         WHERE CAST(elem->>'numValue' AS DOUBLE PRECISION) != :numValue 
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeNumericAttributeValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("numValue") numValue: Double,
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            attr_path.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->attr_path.idx->'availableValues') elem
                         WHERE elem->>'stringValue' != :stringValue
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeStringAttributeValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("stringValue") stringValue: String,
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            attr_path.path,
            COALESCE(
                     (SELECT jsonb_agg(elem)
                         FROM jsonb_array_elements(attribute_collection->'attributes'->attr_path.idx->'availableValues') elem
                         WHERE elem->>'colorValue' != :colorValue
                         AND elem->>'colorHex' != :colorHex
                     ),
                     '[]'\:\:jsonb
                 )
        )
        FROM attr_path
        WHERE id = :variantId
    """, nativeQuery = true)
    fun removeColorAttributeValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("colorValue") colorValue: String,
        @Param("colorHex") colorHex: String
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        ),
         sub_attr_path AS (
            SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
            CAST((index - 1) AS INTEGER) AS idx
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            sap.path,
            attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues'
           || (:valueStr)\:\:jsonb
        )
        FROM attr_path ap, sub_attr_path sap
        WHERE id = :variantId
    """, nativeQuery = true)
    fun addCompositeAttributeValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("subAttrName") subAttrName: String,
        @Param("valueStr") valueString: String
    )

    @Modifying
    @Query("""
        WITH attr_path AS (
            SELECT ('{attributes,'||index - 1||',availableValues}')\:\:text[] AS path,
            CAST((index - 1) AS INTEGER) AS idx 
            FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
            WHERE id = :variantId AND elem->>'attributeName' = :attrName
        )
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            attr_path.path,
            attribute_collection->'attributes'->attr_path.idx->'availableValues'
           || (:valueStr)\:\:jsonb
        )
        FROM attr_path
        WHERE id = :variantId
    """, nativeQuery = true)
    fun addAttributeValue(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String,
        @Param("valueStr") valueString: String
    )

    @Modifying
    @Query(
        """
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            '{attributes}',
            COALESCE(
                (SELECT jsonb_agg(elem)
                    FROM jsonb_array_elements(attribute_collection->'attributes') elem
                    WHERE elem->>'attributeName' != :attrName
                ),
                '[]'\:\:jsonb
            )
        )
        WHERE id = :variantId
    """, nativeQuery = true
    )
    fun removeVariantAttribute(
        @Param("variantId") variantId: Long,
        @Param("attrName") attrName: String
    )

    @Modifying
    @Query(
        """
        UPDATE product_variants
        SET attribute_collection = jsonb_set(
            attribute_collection,
            '{attributes}',
            COALESCE(
                (SELECT jsonb_agg(elem) 
                    FROM jsonb_array_elements(attribute_collection->'attributes') elem
                    WHERE elem->>'attributeName' != :attrName
                ),
                '[]'\:\:jsonb
            ) || (:attrStr)\:\:jsonb
        )
        WHERE id = :variantId
    """, nativeQuery = true
    )
    fun addOrReplaceVariantAttribute(
        @Param("variantId") variantId: Long,
        @Param("attrStr") attrString: String,
        @Param("attrName") attrName: String
    )

    @Modifying
    @Query(
        """
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
    """, nativeQuery = true
    )
    fun removeImage(@Param("variantId") variantId: Long, @Param("imageUrl") imageUrl: String)

    @Modifying
    @Query(
        """
        UPDATE product_variants
        SET image_collection = jsonb_set(image_collection, '{images}', (image_collection->'images')\:\:jsonb || (:photo)\:\:jsonb, false)
        WHERE id = :variantId
    """, nativeQuery = true
    )
    fun addImage(@Param("variantId") variantId: Long, @Param("photo") photo: String)

    @Modifying
    @Query(
        value = """
        WITH sku_path AS (
            SELECT ('{skus,'||index - 1||',stock}')\:\:text[] AS path
            FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
            WHERE id = :variantId AND sku_element->>'sku' = :sku
        )
        UPDATE product_variants
        SET sku_collection = jsonb_set(sku_collection, sku_path.path, to_jsonb(:newStock), false)
        FROM sku_path 
        WHERE id = :variantId
""", nativeQuery = true
    )
    fun updateSkuStock(@Param("sku") sku: String, @Param("variantId") variantId: Long, @Param("newStock") newStock: Int)

    @Modifying
    @Query(
        value = """
                    WITH sku_path AS (
                        SELECT ('{skus,'||index - 1||',price}')\:\:text[] AS path
                        FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
                        WHERE id = :variantId AND sku_element->>'sku' = :sku
                    )
                    UPDATE product_variants
                    SET sku_collection = jsonb_set(sku_collection, sku_path.path, to_jsonb(:newPrice), false)
                    FROM sku_path 
                    WHERE id = :variantId
""", nativeQuery = true
    )
    fun updateSkuPrice(
        @Param("sku") sku: String,
        @Param("variantId") variantId: Long,
        @Param("newPrice") newPrice: BigDecimal
    )

}