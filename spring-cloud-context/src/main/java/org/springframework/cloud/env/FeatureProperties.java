package org.springframework.cloud.env;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("feature")
public class FeatureProperties {
	@NestedConfigurationProperty
	Map<String, Flag<?>> flags = new HashMap<>();

	public Map<String, Flag<?>> getFlags() {
		return flags;
	}

	static class Flag<T> {
		private Map<String, Object> variants;
		private String defaultVariant;
		//private ContextEvaluator<T> contextEvaluator;

		//TODO: enabled or state

		public Map<String, Object> getVariants() {
			return variants;
		}

		public void setVariants(Map<String, Object> variants) {
			this.variants = variants;
		}

		public String getDefaultVariant() {
			return defaultVariant;
		}

		public void setDefaultVariant(String defaultVariant) {
			this.defaultVariant = defaultVariant;
		}
	}
}
