package no.nav.fo.veilarbregistrering.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching
public class CacheConfig {

    public static final String HENT_ARBEIDSFORHOLD = "hentArbeidsforhold";

    @Bean
    public Cache hentArbeidsforholdCache() {
        return new CaffeineCache(HENT_ARBEIDSFORHOLD, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build());
    }

    public static final String HENT_ALLE_ENHETER_V2 = "hentAlleEnheterV2";

    @Bean
    public Cache hentAlleEnheterV2Cache() {
        return new CaffeineCache(HENT_ALLE_ENHETER_V2, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build());
    }

    public static final String HENT_PERSON_FOR_AKTORID = "hentPerson";

    @Bean
    public Cache hentPersonCache() {
        return new CaffeineCache(HENT_PERSON_FOR_AKTORID, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build());
    }

    public static final String HENT_PERSONIDENTER = "hentIdenter";

    @Bean
    public Cache hentIdenterCache() {
        return new CaffeineCache(HENT_PERSONIDENTER, Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build());
    }
}
