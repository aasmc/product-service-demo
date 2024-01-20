package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "shops")
class Shop(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    var seller: Seller,
    var name: String,
    var description: String,
    @OneToMany(mappedBy = "shop")
    @BatchSize(size = 10)
    val products: MutableSet<Product> = hashSetOf()
) {

    override fun toString(): String {
        return "Shop(id=$id, name='$name', description='$description')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? Shop ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}