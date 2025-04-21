package com.intellij.jira.util.provider;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.extension.ExtensionPointName;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class ProviderFactoryImpl implements ProviderFactory {

    private static final ExtensionPointName<Provider> VALUE_PROVIDER_EP = ExtensionPointName.create(Provider.class);
    private final Map<String, Provider> myProviderCache = new HashMap<>();

    public ProviderFactoryImpl() {
        for (Provider provider : VALUE_PROVIDER_EP.getExtensionList()) {
            myProviderCache.put(provider.getKey(), provider);
        }
    }

    @Override
    public Provider get(String key) {
        Provider provider = myProviderCache.get(key);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not found with key='" + key + "'");
        }

        return provider;
    }

}
