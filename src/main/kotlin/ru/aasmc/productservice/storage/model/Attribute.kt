package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "attributes")
class Attribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @Column(name = "short_name", nullable = false)
    var shortName: String,
    @Column(name = "is_faceted", nullable = false)
    var isFaceted: Boolean,
    @Column(name = "is_composite", nullable = false)
    var isComposite: Boolean,
    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    @org.hibernate.annotations.BatchSize(size = 10)
    val attributeValues: MutableSet<AttributeValue> = hashSetOf(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attribute", cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    @org.hibernate.annotations.BatchSize(size = 10)
    val compositeAttributeValues: MutableSet<CompositeAttributeValue> = hashSetOf(),
) {

    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null
    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    override fun toString(): String {
        return "Attribute(id=$id, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? Attribute ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

