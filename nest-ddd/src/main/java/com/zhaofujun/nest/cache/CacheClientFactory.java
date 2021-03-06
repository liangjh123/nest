package com.zhaofujun.nest.cache;

import com.zhaofujun.nest.cache.provider.CacheProvider;
import com.zhaofujun.nest.cache.provider.DefaultCacheProvider;
import com.zhaofujun.nest.configuration.CacheConfiguration;
import com.zhaofujun.nest.configuration.ConfigurationManager;
import com.zhaofujun.nest.core.BeanFinder;
import com.zhaofujun.nest.core.CacheClient;

public class CacheClientFactory {

    private BeanFinder beanFinder;

    public CacheClientFactory(BeanFinder beanFinder) {
        this.beanFinder = beanFinder;
    }

    public CacheClient getCacheClient(String cacheCode) {
        ConfigurationManager configurationManager = ConfigurationManager.getCurrent(beanFinder);
        CacheConfiguration cacheConfiguration = configurationManager.getCacheConfigurationByCode(cacheCode);
        CacheProvider beanProvider = beanFinder.getInstances(CacheProvider.class).stream()
                .filter(p -> p.getCode().equals(cacheConfiguration.getProviderCode()))
                .findFirst()
                .orElse(null);
        if (beanProvider == null)
            beanProvider = new DefaultCacheProvider(beanFinder);
        CacheClient cacheClient = new DefaultCacheClient(beanProvider, cacheConfiguration);
        return cacheClient;
    }
}
