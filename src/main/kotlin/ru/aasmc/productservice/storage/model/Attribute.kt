package ru.aasmc.productservice.storage.model

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import ru.aasmc.productservice.dto.ColorAttributeValueDto
import ru.aasmc.productservice.dto.NumericAttributeValueDto
import ru.aasmc.productservice.dto.StringAttributeValueDto
import java.time.LocalDateTime

@Entity
@Table(name = "attributes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "a_type")
abstract class Attribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @Column(name = "short_name", nullable = false)
    var shortName: String,
    @Column(name = "is_faceted", nullable = false)
    var isFaceted: Boolean,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composite_attribute_id")
    var compositeAttribute: CompositeAttribute? = null
) {

    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        val o = other as? Attribute ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

@Entity
@DiscriminatorValue("SA")
class StringAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "string_values", columnDefinition = "jsonb")
    var stringValues: MutableList<StringAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted)  {

    override fun toString(): String {
        return "StringAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("NA")
class NumericAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "numeric_values", columnDefinition = "jsonb")
    var numericValues: MutableList<NumericAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "NumericAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("CLA")
class ColorAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "color_values", columnDefinition = "jsonb")
    var colorValues: MutableList<ColorAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "ColorAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("CA")
class CompositeAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @OneToMany(mappedBy = "compositeAttribute", cascade = [CascadeType.PERSIST])
    var subAttributes: MutableSet<Attribute> = hashSetOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "CompositeAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }

}
