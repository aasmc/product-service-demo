package ru.aasmc.productservice.storage.model

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "attribute_values")
@DiscriminatorColumn(name = "av_type")
sealed class AttributeValue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    var attribute: Attribute,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composite_attribute_value_id")
    var compositeAttributeValue: CompositeAttributeValue? = null
) {

    override fun equals(other: Any?): Boolean {
        val o = other as? AttributeValue ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

@Entity
@DiscriminatorValue("SAV")
class StringAttributeValue(
    attribute: Attribute,
    compositeAttributeValue: CompositeAttributeValue?,
    @Column(name = "string_value")
    var stringValue: String,
    @Column(name = "string_ru_value")
    var stringRuValue: String?
): AttributeValue(attribute = attribute, compositeAttributeValue = compositeAttributeValue)

@Entity
@DiscriminatorValue("NAV")
class NumericAttributeValue(
    attribute: Attribute,
    compositeAttributeValue: CompositeAttributeValue?,
    @Column(name = "num_value")
    var numValue: Number,
    @Column(name = "num_ru_value")
    var numRuValue: Number?,
    @Column(name = "num_unit")
    var numUnit: String
): AttributeValue(attribute = attribute, compositeAttributeValue = compositeAttributeValue)

@Entity
@DiscriminatorValue("CAV")
class ColorAttributeValue(
    attribute: Attribute,
    compositeAttributeValue: CompositeAttributeValue?,
    @Column(name = "color_value")
    var colorValue: String,
    @Column(name = "color_hex_code")
    var colorHex: String
): AttributeValue(attribute = attribute, compositeAttributeValue = compositeAttributeValue)


@Entity
@Table(name = "composite_attribute_values")
class CompositeAttributeValue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, unique = true)
    var name: String,
    @OneToOne(mappedBy = "compositeAttributeValue", fetch = FetchType.LAZY)
    var value: AttributeValue,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    var attribute: Attribute
) {

    override fun toString(): String {
        return "CompositeAttributeValue(id=$id, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? CompositeAttributeValue ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}


