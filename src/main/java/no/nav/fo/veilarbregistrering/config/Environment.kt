package no.nav.fo.veilarbregistrering.config

fun requireProperty(property: String) =
    System.getProperty(property) ?: throw IllegalStateException("Missing required property $property")

fun requireClusterName() =
    requireProperty("NAIS_CLUSTER_NAME")

fun requireApplicationName() =
    requireProperty("NAIS_APP_NAME")

fun isDevelopment() =
    requireClusterName().startsWith("dev")