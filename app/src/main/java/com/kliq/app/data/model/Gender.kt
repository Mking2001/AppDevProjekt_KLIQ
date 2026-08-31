package com.kliq.app.data.model

enum class Gender {
    MALE,
    FEMALE,
    DIVERSE,
    OTHER,
    PREFER_NOT_TO_SAY,
    UNSPECIFIED;

    companion object {
        fun fromString(value: String?): Gender {
            if (value.isNull_or_empty()) return UNSPECIFIED
            return values().firstOrNull {
                it.name.equals(value, ignoreCase = true)
            } ?: UNSPECIFIED
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
