package ru.aasmc.productservice.docs.dto

sealed class ValidatedValue<T>(
    open val curValue: T?,
    open val pendingValue: T?,
    open val status: ValidationStatus,
    open val issues: List<ValidationIssue>?
)

data class ShopName(
    override val curValue: String? = null,
    override val pendingValue: String? = null,
    override val status: ValidationStatus = ValidationStatus.NEW,
    override val issues: List<ValidationIssue>? = null
): ValidatedValue<String>(curValue, pendingValue, status, issues)

data class ShopLogo(
    override val curValue: String? = null,
    override val pendingValue: String? = null,
    override val status: ValidationStatus = ValidationStatus.NEW,
    override val issues: List<ValidationIssue>? = null
): ValidatedValue<String>(curValue, pendingValue, status, issues)

data class ShopDescription(
    override val curValue: String? = null,
    override val pendingValue: String? = null,
    override val status: ValidationStatus = ValidationStatus.NEW,
    override val issues: List<ValidationIssue>? = null
): ValidatedValue<String>(curValue, pendingValue, status, issues)

