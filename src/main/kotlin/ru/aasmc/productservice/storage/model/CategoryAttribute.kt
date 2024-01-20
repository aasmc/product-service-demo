package ru.aasmc.productservice.storage.model

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Embeddable
class CategoryAttributeId(
    @Column(name = "category_id")
    val categoryId: Long,
    @Column(name = "attribute_id")
    val attributeId: Long
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryAttributeId

        if (categoryId != other.categoryId) return false
        if (attributeId != other.attributeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryId.hashCode()
        result = 31 * result + (attributeId.hashCode())
        return result
    }
}

@Table(name = "category_attributes")
@Entity
class CategoryAttribute(
    @Column(name = "is_required", nullable = false)
    var isRequired: Boolean = false,
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    val category: Category,

    @ManyToOne
    @JoinColumn(name = "attribute_id", insertable = false, updatable = false)
    val attribute: Attribute,
) {

    @EmbeddedId
    val id: CategoryAttributeId =
        CategoryAttributeId(categoryId = category.id!!, attributeId = attribute.id!!)


    override fun toString(): String {
        return "CategoryAttribute(isRequired=$isRequired, id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? CategoryAttribute ?: return false
        return id.categoryId != null && id.categoryId == o.id.categoryId &&
                id.attributeId != null && id.attributeId == o.id.attributeId
    }

    override fun hashCode(): Int {
        return Objects.hash(id.categoryId, id.attributeId)
    }
}