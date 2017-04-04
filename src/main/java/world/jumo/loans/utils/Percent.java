package world.jumo.loans.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;

public final class Percent implements java.io.Serializable {

    public static final Percent ZERO = new Percent(new BigDecimal(0));

    public static final int DEFAULT_PRECISION = 4;
    public static final int DISPLAY_PRECISION = 2;

    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    public static final DecimalFormat format = new DecimalFormat("%#0.00");
    BigDecimal value;

    private Percent(BigDecimal value, boolean allowSign) {
        if (!allowSign) {
            if (value.compareTo(new BigDecimal(0)) < 0) {
                value = value.negate();
            }
        }
        this.value = value;
    }

    public Percent() {
    }

    public Percent(BigDecimal percent) {
        if (percent.compareTo(new BigDecimal(0)) < 0) {
            percent = percent.negate();
        }
        this.value = percent.divide(new BigDecimal(100), DEFAULT_PRECISION, ROUNDING_MODE);
    }

    public Percent(double percent) {
        this(new BigDecimal(percent));
    }

    public Percent(Percent percent) {
        this.value = percent.bigDecimalValue();
    }

    public double doubleValue() {
        return this.value.multiply(new BigDecimal(100))
                .setScale(DISPLAY_PRECISION, ROUNDING_MODE).doubleValue();
    }

    public BigDecimal bigDecimalValue() {
        return this.value.multiply(new BigDecimal(100))
                .setScale(DISPLAY_PRECISION, ROUNDING_MODE);
    }

    public static Percent valueOf(String s) {
        Percent p = null;
        try {
            Number n = format.parse(s);
            p = new Percent(new BigDecimal(n.doubleValue()), true);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot interpret as percent " + s);
        }

        return p;
    }

    public static Percent factorOf(double a, double b) {
        Percent p = null;
        return new Percent(new BigDecimal((a - b) / a), true);
    }

    public String toString() {
        return format.format(value);
    }

    public String toStringFormat(DecimalFormat format2) {
        return format2.format(value);
    }

    public Percent add(Percent amount) {
        BigDecimal result
                = this.value.add(amount.value);
        return new Percent(result, true);
    }

    public Percent multiply(Numeric amount) {
        return new Percent(this.value.multiply(amount.bigDecimalValue()), true);
    }

    public int compareTo(Percent other) {
        return this.value.compareTo(other.value);
    }

    public Percent subtract(Percent amount) {
        return new Percent(this.value.subtract(amount.value), true);
    }

    public Percent negate() {
        return new Percent(this.value.negate(), true);
    }

    public BigInteger unscaledValue() {
        return value.unscaledValue();
    }

    public int scale() {
        return value.scale();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Percent)) {
            return false;
        }
        return value.equals(((Percent) obj).value);
    }

    public String getValue() {
        return format.format(value);
    }

    public void setValue(String strValue) {
        this.value = valueOf(strValue).bigDecimalValue();
    }

}
