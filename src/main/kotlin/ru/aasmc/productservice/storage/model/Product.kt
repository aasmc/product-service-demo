package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: Shop,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
    var name: String,
    var description: String,
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    @BatchSize(size = 10)
    val variants: MutableSet<ProductVariant> = hashSetOf(),
    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun addVariant(variant: ProductVariant) {
        variants.add(variant)
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name', description='$description')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? Product ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}