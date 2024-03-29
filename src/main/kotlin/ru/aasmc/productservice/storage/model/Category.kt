package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,
    @OneToMany(mappedBy = "parent")
    @org.hibernate.annotations.BatchSize(size = 5)
    val subCategories: MutableSet<Category> = hashSetOf(),
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
    @org.hibernate.annotations.BatchSize(size = 10)
    val categoryAttributes: MutableSet<CategoryAttribute> = hashSetOf(),
) {

    @Column(name = "created_at", nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null
    @Column(name = "updated_at", nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "Category(id=$id, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? Category ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}