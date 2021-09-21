package no.nav.fo.veilarbregistrering.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun hentArbeidsforholdCache(): Cache {
        return CaffeineCache(
            HENT_ARBEIDSFORHOLD, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build()
        )
    }

    @Bean
    fun hentAlleEnheterV2Cache(): Cache {
        return CaffeineCache(
            HENT_ALLE_ENHETER_V2, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build()
        )
    }

    @Bean
    fun hentPersonCache(): Cache {
        return CaffeineCache(
            HENT_PERSON_FOR_AKTORID, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build()
        )
    }

    @Bean
    fun hentIdenterCache(): Cache {
        return CaffeineCache(
            HENT_PERSONIDENTER, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build()
        )
    }

    @Bean
    fun hentGeografiskTilknytningCache(): Cache {
        return CaffeineCache(
            HENT_GEOGRAFISK_TILKNYTNING, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build()
        )
    }

    companion object {
        const val HENT_ARBEIDSFORHOLD = "hentArbeidsforhold"
        const val HENT_ALLE_ENHETER_V2 = "hentAlleEnheterV2"
        const val HENT_PERSON_FOR_AKTORID = "hentPerson"
        const val HENT_PERSONIDENTER = "hentIdenter"
        const val HENT_GEOGRAFISK_TILKNYTNING = "hentGeografiskTilknytning"
    }
}