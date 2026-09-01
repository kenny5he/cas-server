/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Collections;

/**
 * Immutable runtime description of one configuration alias.
 *
 * @param sourcePrefix application-facing prefix
 * @param targetPrefix CAS prefix
 * @param propertyPaths mapped relative paths; empty means the whole prefix
 * @param overrideExisting whether an existing target property is overridden
 * @param mappingClass class declaring the mapping
 */
public record ConfigurationMappingDefinition(
        String sourcePrefix,
        String targetPrefix,
        Set<String> propertyPaths,
        boolean overrideExisting,
        String mappingClass) {

    public ConfigurationMappingDefinition {
        sourcePrefix = normalizePath(sourcePrefix, "source prefix");
        targetPrefix = normalizePath(targetPrefix, "target prefix");
        if (sourcePrefix.isEmpty()) {
            throw new IllegalArgumentException("Configuration mapping source prefix must not be empty");
        }
        if (targetPrefix.isEmpty()) {
            throw new IllegalArgumentException("Configuration mapping target prefix must not be empty");
        }
        if (sourcePrefix.equals(targetPrefix)) {
            throw new IllegalArgumentException("Configuration mapping source and target prefixes must differ: "
                    + sourcePrefix);
        }
        var normalizedPaths = new LinkedHashSet<String>();
        if (propertyPaths != null) {
            for (String path : propertyPaths) {
                var normalized = normalizePath(path, "property path");
                if (!normalized.isEmpty()) {
                    normalizedPaths.add(normalized);
                }
            }
        }
        propertyPaths = Collections.unmodifiableSet(normalizedPaths);
        mappingClass = mappingClass == null ? "unknown" : mappingClass;
    }

    public boolean mapsWholePrefix() {
        return propertyPaths.isEmpty();
    }

    /**
     * Converts a canonical source property name to its target name when this
     * definition owns the source property.
     *
     * @param propertyName property name in canonical dotted form
     * @return target property name, or {@code null} when it is outside this mapping
     */
    public String map(String propertyName) {
        var canonicalName = normalizePath(propertyName, "property name");
        String suffix;
        if (canonicalName.equals(sourcePrefix)) {
            suffix = "";
        } else if (canonicalName.startsWith(sourcePrefix + ".")) {
            suffix = canonicalName.substring(sourcePrefix.length() + 1);
        } else {
            return null;
        }

        if (!mapsWholePrefix() && propertyPaths.stream().noneMatch(path ->
                suffix.equals(path) || suffix.startsWith(path + ".") || suffix.startsWith(path + "["))) {
            return null;
        }
        return suffix.isEmpty() ? targetPrefix : targetPrefix + "." + suffix;
    }

    public String sourceProperty(String relativePath) {
        var suffix = normalizePath(relativePath, "property path");
        return suffix.isEmpty() ? sourcePrefix : sourcePrefix + "." + suffix;
    }

    private static String normalizePath(String value, String description) {
        if (value == null) {
            throw new IllegalArgumentException("Configuration mapping " + description + " must not be null");
        }
        var normalized = value.trim()
                .replace('_', '-')
                .replaceAll("\\.+", ".")
                .replaceAll("^\\.|\\.$", "")
                .toLowerCase(Locale.ROOT);
        return normalized;
    }
}
