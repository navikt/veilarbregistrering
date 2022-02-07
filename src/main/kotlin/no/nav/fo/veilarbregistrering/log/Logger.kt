package no.nav.fo.veilarbregistrering.log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger

inline fun <reified T:Any> loggerFor(): Logger =
    getLogger(T::class.java) ?: throw IllegalStateException("Error creating logger")

inline val <reified T : Any> T.logger get() = getCachedLogger(T::class.java.name)

fun getCachedLogger(loggerName: String): Logger {
    return LoggerCache.getLogger(loggerName)
}

private object LoggerCache {
    private val existingLoggers = mutableMapOf<String, Logger>()

    fun getLogger(className: String): Logger {
        return existingLoggers[className]
            ?: run {
                val classLogger = LoggerFactory.getLogger(className)
                existingLoggers[className] = classLogger
                classLogger
            }
    }
}
