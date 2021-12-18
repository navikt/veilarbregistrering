package no.nav.fo.veilarbregistrering.config

fun requireProperty(property: String) =
    getPropertyOrNull(property) ?: throw IllegalStateException("Missing required property $property")

fun getPropertyOrNull(property: String): String? =
    System.getProperty(property, System.getenv(property))

fun requireClusterName() =
    requireProperty("NAIS_CLUSTER_NAME")

fun requireApplicationName() =
    requireProperty("NAIS_APP_NAME")

fun applicationNameOrNull() =
    getPropertyOrNull("NAIS_APP_NAME")

fun isDevelopment(): Boolean = !isProduction()

fun isProduction(): Boolean =
    getPropertyOrNull("NAIS_CLUSTER_NAME")?.contains("prod") ?: false

fun isOnPrem(): Boolean =
    !requireClusterName().endsWith("gcp")