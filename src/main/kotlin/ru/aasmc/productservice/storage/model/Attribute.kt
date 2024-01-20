package ru.aasmc.productservice.storage.model

import jakarta.persistence.*

@Entity
@Table(name = "attributes")
class Attribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @org.hibernate.annotations.BatchSize(size = 10)
    val attributeValues: MutableSet<AttributeValue> = hashSetOf()
) {

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