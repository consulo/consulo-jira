package com.intellij.jira.util.provider;

import consulo.component.extension.ExtensionPointName;

import java.util.HashMap;
import java.util.Map;

public class ProviderFactoryImpl implements ProviderFactory {

    private static final ExtensionPointName<Provider> VALUE_PROVIDER_EP = ExtensionPointName.create("com.intellij.jira.valueProvider");
    private static final Map<String, Provider> myProviderCache = new HashMap<>();

    static {
        for(Provider provider : VALUE_PROVIDER_EP.getExtensionList()){
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
