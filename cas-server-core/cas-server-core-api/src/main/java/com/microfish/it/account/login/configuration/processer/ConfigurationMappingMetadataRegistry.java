/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import java.util.List;

/**
 * Exposes discovered mappings for diagnostics and tooling. Environment
 * mutation is performed by the post processors, not by this registry.
 */
public final class ConfigurationMappingMetadataRegistry {

    private final List<ConfigurationMappingDefinition> definitions;

    public ConfigurationMappingMetadataRegistry(List<ConfigurationMappingDefinition> definitions) {
        this.definitions = List.copyOf(definitions);
    }

    public List<ConfigurationMappingDefinition> getDefinitions() {
        return definitions;
    }
}
