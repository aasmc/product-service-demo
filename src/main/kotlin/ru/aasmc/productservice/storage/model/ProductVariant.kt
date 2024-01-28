package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import ru.aasmc.productservice.dto.AttributeCollection
import ru.aasmc.productservice.dto.AttributeDto
import ru.aasmc.productservice.dto.ImageCollection
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "product_variants")
class ProductVariant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "variant_name", nullable = false)
    var variantName: String,
    @Column(nullable = false)
    var price: BigDecimal,
    var stock: Int,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb", name = "attributes")
    var attributes: AttributeCollection = AttributeCollection(),
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb", name = "images")
    var images: ImageCollection = ImageCollection(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

) {

    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null
    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "ProductVariant(id=$id, price=$price, stock=$stock, attributes=$attributes, images=$images)"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? ProductVariant ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}