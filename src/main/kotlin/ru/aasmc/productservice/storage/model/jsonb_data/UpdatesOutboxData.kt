package ru.aasmc.productservice.storage.model.jsonb_data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import ru.aasmc.productservice.dto.*
import ru.aasmc.productservice.storage.model.jsonb_data.ProductUpdateReason.*
import ru.aasmc.productservice.storage.model.jsonb_data.SkuUpdateReason.UPDATE_PRICE
import ru.aasmc.productservice.storage.model.jsonb_data.SkuUpdateReason.UPDATE_STOCK
import java.math.BigDecimal


enum class ProductUpdateReason(
    @field:JsonValue
    val reasonName: String
) {
    UPDATE_PRODUCT_VARIANT_NAME("pv_name"),
    UPDATE_PRODUCT_NAME("p_name"),
    UPDATE_PRODUCT_DESCRIPTION("p_description"),
    UPDATE_PRODUCT_VARIANT_PHOTOS("pv_photos"),
    UPDATE_PRODUCT_VARIANT_ATTRIBUTES("pv_attributes"),
    UPDATE_PRODUCT_VARIANT_PRICE("pv_price")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "reason",
    visible = true
)
@JsonSubTypes(*arrayOf(
    JsonSubTypes.Type(
        value = UpdatePVName::class,
        name = "pv_name"
    ),
    JsonSubTypes.Type(
        value = UpdateProductName::class,
        name = "p_name"
    ),
    JsonSubTypes.Type(
        value = UpdatePVPhotos::class,
        name = "pv_photos"
    ),
    JsonSubTypes.Type(
        value = UpdateProductDescription::class,
        name = "p_description"
    ),
    JsonSubTypes.Type(
        value = UpdatePVAttributes::class,
        name = "pv_attributes"
    ),
    JsonSubTypes.Type(
        value = UpdatePVPrice::class,
        name = "pv_price"
    )
))
sealed class ProductUpdate(
    open val reason: ProductUpdateReason
)

data class UpdatePVPhotos(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_VARIANT_PHOTOS,
    val variantId: Long,
    val newPhotos: ImageCollection
): ProductUpdate(reason)

data class UpdatePVName(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_VARIANT_NAME,
    val variantId: Long,
    val newName: String,
): ProductUpdate(reason)

data class UpdatePVPrice(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_VARIANT_PRICE,
    val variantId: Long,
    val newPrice: BigDecimal
): ProductUpdate(reason)

data class UpdatePVAttributes(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_VARIANT_ATTRIBUTES,
    val variantId: Long,
    val newAttributes: AttributeCollection
): ProductUpdate(reason)

data class UpdateProductName(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_NAME,
    val productId: Long,
    val newName: String,
): ProductUpdate(reason)

data class UpdateProductDescription(
    override val reason: ProductUpdateReason = UPDATE_PRODUCT_DESCRIPTION,
    val productId: Long,
    val newDescription: String
): ProductUpdate(reason)


enum class SkuUpdateReason(
    @field:JsonValue
    val reasonName: String
) {
    UPDATE_STOCK("stock"),
    UPDATE_PRICE("price")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "reason"
)
@JsonSubTypes(
    *arrayOf(
        JsonSubTypes.Type(
            value = UpdateSkuStock::class,
            name = "stock"
        ),
        JsonSubTypes.Type(
            value = UpdateSkuPrice::class,
            name = "price"
        )
    )
)
sealed class SkuUpdate(
    open val reason: SkuUpdateReason,
    open val variantId: Long,
    open val sku: String,
)

data class UpdateSkuStock(
    override val reason: SkuUpdateReason = UPDATE_STOCK,
    override val variantId: Long,
    override val sku: String,
    val newStock: Int
): SkuUpdate(reason, variantId, sku)

data class UpdateSkuPrice(
    override val reason: SkuUpdateReason = UPDATE_PRICE,
    override val variantId: Long,
    override val sku: String,
    val newPrice: BigDecimal
): SkuUpdate(reason, variantId, sku)