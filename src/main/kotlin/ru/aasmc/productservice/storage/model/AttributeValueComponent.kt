package ru.aasmc.productservice.storage.model

import jakarta.persistence.*

@Entity
@Table(name = "attribute_components")
class AttributeValueComponent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id")
    var attributeValue: AttributeValue? = null,
    @Column(name = "component_name", nullable = false)
    var componentName: String,
    @Column(name = "component_value", nullable = false)
    var componentValue: String
) {

    override fun toString(): String {
        return "AttributeValueComponent(id='$id', componentName='$componentName', componentValue='$componentValue')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? AttributeValueComponent ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}