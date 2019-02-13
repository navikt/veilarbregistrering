package no.nav.fo.veilarbregistrering.config;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.sf.ehcache.store.MemoryStoreEvictionPolicy.LRU;
import static no.nav.dialogarena.aktor.AktorConfig.AKTOR_ID_FROM_FNR_CACHE;
import static no.nav.dialogarena.aktor.AktorConfig.FNR_FROM_AKTOR_ID_CACHE;
import static no.nav.sbl.dialogarena.common.abac.pep.context.AbacContext.ABAC_CACHE;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String HENT_ENHET = "hentEnhet";
    public static final String HENT_ARBEIDSFORHOLD = "hentArbeidsforhold";
    public static final String HENT_ALLE_ENHETER = "hentAlleEnheter";

    private static final CacheConfiguration HENT_ENHET_CACHE = new CacheConfiguration(HENT_ENHET, 10000)
            .memoryStoreEvictionPolicy(LRU)
            .timeToIdleSeconds(3600)
            .timeToLiveSeconds(3600);

    private static final CacheConfiguration HENT_ARBEIDSFORHOLD_CACHE = new CacheConfiguration(HENT_ARBEIDSFORHOLD, 100000)
            .memoryStoreEvictionPolicy(LRU)
            .timeToIdleSeconds(3600)
            .timeToLiveSeconds(3600);

    private static final CacheConfiguration HENT_ALLE_ENHETER_CACHE = new CacheConfiguration(HENT_ALLE_ENHETER, 100000)
            .memoryStoreEvictionPolicy(LRU)
            .timeToIdleSeconds(3600)
            .timeToLiveSeconds(3600);

    @Bean
    public CacheManager cacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(HENT_ENHET_CACHE);
        config.addCache(ABAC_CACHE);
        config.addCache(AKTOR_ID_FROM_FNR_CACHE);
        config.addCache(FNR_FROM_AKTOR_ID_CACHE);
        config.addCache(HENT_ARBEIDSFORHOLD_CACHE);
        config.addCache(HENT_ALLE_ENHETER_CACHE);
        return new EhCacheCacheManager(net.sf.ehcache.CacheManager.newInstance(config));
    }

}
