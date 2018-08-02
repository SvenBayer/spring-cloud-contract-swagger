package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import io.swagger.models.properties.*;

import java.math.BigDecimal;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_INT;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.createDefaultValueForType;

/**
 * Creates default values for {@link AbstractNumericProperty}.
 *
 * @author Sven Bayer
 */
public class NumericPropertyValueBuilder {

	/**
	 * Creates a default numeric value for the given property
	 *
	 * @param numeric the numeric property
	 * @return the default value
	 */
	Object createDefaultNumericValue(AbstractNumericProperty numeric) {
		BigDecimal numericPropertyValue = null;
		if (numeric.getMinimum() != null) {
			if (numeric.getExclusiveMinimum() != null && numeric.getExclusiveMinimum()) {
				numericPropertyValue = numeric.getMinimum().add(new BigDecimal(DEFAULT_INT));
			} else {
				numericPropertyValue = numeric.getMinimum();
			}
		}
		if (numeric.getMaximum() != null) {
			if (numeric.getExclusiveMaximum() != null && numeric.getExclusiveMaximum()) {
				numericPropertyValue = numeric.getMaximum().subtract(new BigDecimal(DEFAULT_INT));
			} else {
				numericPropertyValue = numeric.getMaximum();
			}
		}
		return getTypedNumericValue(numeric, numericPropertyValue);
	}

	/**
	 * Returns the typed value for the given numeric property and value
	 *
	 * @param numeric the property
	 * @param numericPropertyValue the value
	 * @return the typed value
	 */
	Object getTypedNumericValue(AbstractNumericProperty numeric, BigDecimal numericPropertyValue) {
		if (numericPropertyValue == null) {
			return createDefaultValueForType(numeric.getType(), numeric.getFormat(), numeric.getName());
		}
		if (numeric instanceof LongProperty) {
			return numericPropertyValue.longValue();
		}
		if (numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
			return numericPropertyValue.intValue();
		}
		if (numeric instanceof DoubleProperty) {
			return numericPropertyValue.doubleValue();
		}
		if (numeric instanceof FloatProperty) {
			return numericPropertyValue.floatValue();
		}
		if (numeric instanceof DecimalProperty) {
			return numericPropertyValue;
		}
		return DEFAULT_INT;
	}
}
