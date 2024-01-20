package ru.aasmc.productservice.storage.model

import jakarta.persistence.*

@Entity
@Table(name = "attribute_values")
class AttributeValue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    var attribute: Attribute,
    var value: String,
    @Column(name = "is_composite")
    var isComposite: Boolean = false,
    @OneToMany(mappedBy = "attributeValue", cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    val components: MutableSet<AttributeValueComponent> = hashSetOf()
) {

    override fun toString(): String {
        return "AttributeValue(id=$id, value='$value', isComposite=$isComposite)"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? AttributeValue ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}