package com.desynatdealer.model

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String>
)
