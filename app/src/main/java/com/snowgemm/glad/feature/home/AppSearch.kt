package com.snowgemm.glad.feature.home

internal fun appMatchesQuery(
    label: String,
    packageName: String,
    query: String,
): Boolean {
    val normalizedQuery = query.trim()
    return normalizedQuery.isEmpty() ||
        label.contains(normalizedQuery, ignoreCase = true) ||
        packageName.contains(normalizedQuery, ignoreCase = true)
}
