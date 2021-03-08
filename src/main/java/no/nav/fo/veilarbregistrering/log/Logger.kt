package no.nav.fo.veilarbregistrering.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

inline fun <reified T:Any> loggerFor(): Logger =
    getLogger(T::class.java) ?: throw IllegalStateException("Error creating logger")