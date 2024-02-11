package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "shops")
@org.hibernate.annotations.BatchSize(size = 10)
class Shop(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    var seller: Seller,
    var name: String,
    var description: String,
    @OneToMany(mappedBy = "shop", cascade = [CascadeType.PERSIST])
    @org.hibernate.annotations.BatchSize(size = 10)
    val products: MutableSet<Product> = hashSetOf(),

) {

    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null
    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

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