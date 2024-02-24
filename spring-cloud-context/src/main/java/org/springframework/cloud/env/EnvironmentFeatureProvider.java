package org.springframework.cloud.env;

import java.util.List;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.EventProvider;
import dev.openfeature.sdk.Hook;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.ProviderState;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import dev.openfeature.sdk.exceptions.FlagNotFoundError;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * https://openfeature.dev/specification/
 */
public class EnvironmentFeatureProvider extends EventProvider {

	private static final String NAME = "EnvironmentFeatureProvider";

	private final ConfigurableEnvironment environment;

	public EnvironmentFeatureProvider(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public Metadata getMetadata() {
		return () -> NAME;
	}

	@Override
	public List<Hook> getProviderHooks() {
		return super.getProviderHooks();
	}

	@Override
	public void initialize(EvaluationContext evaluationContext) throws Exception {
		super.initialize(evaluationContext);
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public ProviderEvaluation<Boolean> getBooleanEvaluation(String key, Boolean defaultValue, EvaluationContext ctx) {
		return getEvaluation(key, Boolean.class, defaultValue, ctx);
	}

	@Override
	public ProviderEvaluation<String> getStringEvaluation(String key, String defaultValue, EvaluationContext ctx) {
		return getEvaluation(key, String.class, defaultValue, ctx);
	}

	@Override
	public ProviderEvaluation<Integer> getIntegerEvaluation(String key, Integer defaultValue, EvaluationContext ctx) {
		return getEvaluation(key, Integer.class, defaultValue, ctx);
	}

	@Override
	public ProviderEvaluation<Double> getDoubleEvaluation(String key, Double defaultValue, EvaluationContext ctx) {
		return getEvaluation(key, Double.class, defaultValue, ctx);
	}

	@Override
	public ProviderEvaluation<Value> getObjectEvaluation(String key, Value defaultValue, EvaluationContext ctx) {
		ProviderEvaluation<Object> evaluation = getEvaluation(key, Object.class, defaultValue, ctx);
		Value value = null;
		try {
			value = new Value(evaluation.getValue());
		} catch (InstantiationException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		new ProviderEvaluation<>(value, evaluation.getVariant(), evaluation.getReason(), evaluation.getErrorCode(), evaluation.getErrorMessage(), evaluation.getFlagMetadata());
		return getEvaluation(key, Value.class, defaultValue, ctx);
	}

	private <T> ProviderEvaluation<T> getEvaluation(String key, Class<T> targetType, @Nullable T defaultValue, EvaluationContext ctx) {
		// TODO: deal with state
		T property = environment.getProperty(key, targetType);
		String variant;
		String reason = null;
		if (property == null && defaultValue == null) {
			throw new FlagNotFoundError("flag " + key + "not found");
		}
		else if (property == null) {
			property = defaultValue;
			variant = "Passed in default";
			reason = Reason.DEFAULT.toString();
		}
		else {
			variant = "Found flag";
			reason = Reason.DEFAULT.toString();
		}
		return ProviderEvaluation.<T>builder()
				.value(property)
				.variant(variant)
				.reason(reason)
				.build();
	}

	@Override
	public ProviderState getState() {
		return ProviderState.READY;
	}
}
