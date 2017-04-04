package world.jumo.loans.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.DecimalFormat;

public final class Numeric implements java.io.Serializable, Comparable {

    public static final Numeric ZERO = new Numeric(new BigDecimal(0));
    public static final Numeric ONE = new Numeric(new BigDecimal(1));

    public static final DecimalFormat format = new DecimalFormat("#0.00");

    private BigDecimal value;

    public Numeric(BigInteger unscaledVal, int scale) {
        this.value = new BigDecimal(unscaledVal, scale);
    }

    public Numeric(double value) {
        this.value = new BigDecimal(value);
    }

    public Numeric(BigDecimal amount) {
        this.value = amount;
    }

    public Numeric(Number amount) {
        this(amount.doubleValue());
    }

    public Numeric add(Numeric amount) {
        BigDecimal result
                = this.value.add(amount.value);

        return new Numeric(result);
    }

    public Numeric multiply(Numeric amount) {
        return new Numeric(this.value.multiply(amount.value));
    }

    @Override
    public int compareTo(Object other) {
        return this.value.compareTo(((Numeric) other).value);
    }

    public Numeric subtract(Numeric amount) {
        return new Numeric(this.value.subtract(amount.value));
    }

    public Numeric negate() {
        return new Numeric(this.value.negate());
    }

    public Numeric divide(Numeric amount, int roundingMode) {
        return new Numeric(this.value.divide(amount.value, roundingMode));
    }

    public Numeric min(Numeric val) {
        return new Numeric(this.value.min(val.value));
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public static Numeric valueOf(String s) {
        Numeric nm = null;

        try {
            Number n = format.parse(s);
            nm = new Numeric(n);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot interpret as Numeric " + s);
        }
        return nm;
    }

    @Override
    public String toString() {
        return format.format(value);
    }

    public BigDecimal bigDecimalValue() {
        return value;
    }

    public BigInteger unscaledValue() {
        return value.unscaledValue();
    }

    public int scale() {
        return value.scale();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Numeric) {
            return value.equals(((Numeric) obj).value);
        }
        return false;
    }

}
