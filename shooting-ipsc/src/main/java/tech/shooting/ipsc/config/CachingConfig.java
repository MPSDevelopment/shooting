package tech.shooting.ipsc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class CachingConfig extends CachingConfigurerSupport {
	public static final String OPERATIONS_CACHE = "operations";
	//	@Bean
	//	public CacheManager cacheManager() {
	//		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	//	}
	//
	//	@Bean
	//	public EhCacheManagerFactoryBean ehCacheCacheManager() {
	//		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
	//		cmfb.setConfigLocation(new ClassPathResource("cache/ehcache.xml"));
	//		cmfb.setShared(true);
	//		return cmfb;
	//	}

	@Bean
	public CacheManager cacheManager () {
		log.info("Cache manager initialization");
		return new ConcurrentMapCacheManager(OPERATIONS_CACHE);
	}

	@Bean
	public CacheErrorHandler errorHandler () {
		return new CacheErrorHandler() {
			@Override
			public void handleCacheGetError (RuntimeException exception, Cache cache, Object key) {
				log.debug(cache.getName(), exception);
			}

			@Override
			public void handleCachePutError (RuntimeException exception, Cache cache, Object key, Object value) {
				log.debug(cache.getName(), exception);
			}

			@Override
			public void handleCacheEvictError (RuntimeException exception, Cache cache, Object key) {
				log.debug(cache.getName(), exception);
			}

			@Override
			public void handleCacheClearError (RuntimeException exception, Cache cache) {
				log.debug(cache.getName(), exception);
			}
		};
	}
}