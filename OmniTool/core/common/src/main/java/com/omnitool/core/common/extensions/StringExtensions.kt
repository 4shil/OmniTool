package com.omnitool.core.common.extensions

/**
 * String utility extensions
 */

fun String.capitalizeFirst(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { it.capitalizeFirst() }

fun String.removeExtraWhitespace(): String =
    replace(Regex("\\s+"), " ").trim()

fun String.countWords(): Int =
    if (isBlank()) 0 else split(Regex("\\s+")).size

fun String.countCharacters(includeSpaces: Boolean = true): Int =
    if (includeSpaces) length else replace(" ", "").length

fun String.reverseText(): String = reversed()

fun String.toSnakeCase(): String =
    replace(Regex("([a-z])([A-Z])"), "$1_$2")
        .replace(Regex("\\s+"), "_")
        .lowercase()

fun String.toCamelCase(): String =
    split(Regex("[\\s_-]+"))
        .mapIndexed { index, word ->
            if (index == 0) word.lowercase()
            else word.capitalizeFirst()
        }
        .joinToString("")

fun String.toPascalCase(): String =
    split(Regex("[\\s_-]+"))
        .joinToString("") { it.capitalizeFirst() }
