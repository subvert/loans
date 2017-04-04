package world.jumo.loans.utils;

import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

public final class Money implements java.io.Serializable, Comparable {

    public static final BigDecimal BG_ZERO = BigDecimal.ZERO;
    public static final Money ZERO = new Money(BG_ZERO);

    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
    public static final int DEFAULT_PRECISION = 2;

    protected static Locale LOCALE = Locale.getDefault();
    public static DecimalFormat currencyFormat
            = (DecimalFormat) NumberFormat.getCurrencyInstance(LOCALE);
    private static final DecimalFormat format = new DecimalFormat("#0.00###;-0.00###");

    private BigDecimal value;

    static {
        String pattern = currencyFormat.toPattern();
        String tmp = "";
        if (pattern.indexOf(";") != -1) {
            tmp = pattern.substring(0, pattern.indexOf(";"));
            tmp += "#" + pattern.substring(pattern.indexOf(";")) + "#";
            pattern = tmp;
        }
        currencyFormat.applyPattern(pattern);
    }

    public Money() {
        // Need default constructor for web service.
    }

    public Money(BigDecimal value) {
        this(value, DEFAULT_PRECISION);
    }

    public Money(BigDecimal value, int precision) {
        this.value = value.setScale(precision, ROUNDING_MODE);
    }

    public Money(Money value) {
        this(value.value);
    }

    public Money(double value) {
        this(new BigDecimal(value));
    }

    public Money(double value, int precision) {
        this(new BigDecimal(value), precision);
    }

    public Money(Number value) {
        this(new BigDecimal(value.doubleValue()));
    }

    public Money abs() {
        return new Money(value.abs(), value.scale());
    }

    public Money add(Money value) {
        return add(value.value);
    }

    public Money add(Percent percent) {
        return add(value.multiply(percent.value));
    }

    private Money add(BigDecimal value) {
        return new Money(this.value.add(value), this.value.scale());
    }

    public Money subtract(Percent percent) {
        BigDecimal result = percent.add(new Percent(100.0)).value;
        return new Money(value.divide(result, ROUNDING_MODE), value.scale());
    }

    public Money multiply(Percent percent) {
        return multiply(percent.value);
    }

    public Money multiply(Numeric value) {
        return multiply(value.bigDecimalValue());
    }

    public Money multiply(int amount) {
        return multiply(new BigDecimal(amount));
    }

    public Money multiply(float qty) {
        return multiply(new BigDecimal(qty));
    }

    private Money multiply(BigDecimal value) {
        return new Money(this.value.multiply(value), this.value.scale());
    }

    public Money convCurrency(double exchangerate) {
        double ans = value.doubleValue() * exchangerate;
        return new Money(ans, value.scale());
    }

    public Money subtract(Money amount) {
        return new Money(this.value.subtract(amount.value), this.value.scale());
    }

    public Money negate() {
        return new Money(this.value.negate(), this.value.scale());
    }

    public Money divide(Numeric amount) {
        return divide(amount.bigDecimalValue());
    }

    public Money divide(float amount) {
        return divide(new Numeric(amount).bigDecimalValue());
    }

    public Money divide(Percent percent) {
        return divide(percent.value);
    }

    private Money divide(BigDecimal value) {
        return new Money(this.value.divide(value, ROUNDING_MODE),
                this.value.scale());
    }

    public Percent percentOf(Money value) {
        BigDecimal result = this.value.divide(
                value.value, DEFAULT_PRECISION * 2, ROUNDING_MODE);
        return new Percent(result.multiply(new BigDecimal(100)));
    }

    public Money min(Money value) {
        return new Money(this.value.min(value.value), this.value.scale());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Money)) {
            return false;
        }
        return this.value.equals(((Money) obj).value);
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public BigDecimal bigDecimalValue() {
        return value;
    }

    public static Money valueOf(String s) {
        return valueOf(s, DEFAULT_PRECISION);
    }

    public static Money valueOf(String s, int precision) {
        try {
            Number n = format.parse(s);
            return new Money(new BigDecimal(n.doubleValue()), precision);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot interpret as money " + s);
        }
    }

    public static Money valueOfMinorCurrencyUnits(String strVal)
            throws IllegalArgumentException {
        int digitFraction = NumberFormat.getInstance().getCurrency()
                .getDefaultFractionDigits();
        return valueOf(strVal, digitFraction);
    }

    public String toString() {
        return format.format(value);
    }

    public String toCurrencyString() {
        return currencyFormat.format(value);
    }

    public String getValue() {
        return format.format(value);
    }

    public void setValue(String strValue) {
        this.value = Money.valueOf(strValue).bigDecimalValue();
    }

    public int compareTo(Object obj) {
        if (obj == null) {
            return -1;
        }
        Money m = (Money) obj;
        return value.compareTo(m.value);
    }

    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }

    public BigInteger unscaledValue() {
        return value.unscaledValue();
    }

    public int scale() {
        return value.scale();
    }

    public void setBaseUnits(int intVal) {
        // Needed only method for bean structure.
    }

    public int getBaseUnits() {
//      return new BigDecimal(this.value.doubleValue())
//                           .multiply(new BigDecimal(100)).intValue();
        return this.value.multiply(new BigDecimal(100)).intValue();
    }

    public int getMinorCurrencyUnits() {
        int digitFraction = NumberFormat.getInstance().getCurrency()
                .getDefaultFractionDigits();
        if (digitFraction < 1) {
            return this.value.intValue();
        }

        return this.value.multiply(new BigDecimal(Math.round(Math.pow(10, digitFraction)))).intValue();
    }

    public void setMinorCurrencyUnits(int intVal) {
        // Needed only method for bean structure.
    }

    public static Money correctSign(Money amount, boolean isCredit,
            boolean reverseSign) {
        amount = correctSign(amount, isCredit);
        if (reverseSign) {
            amount = amount.multiply(-1);
        }
        return amount;
    }

    public static Money correctSign(Money amount, boolean isCredit) {
        amount = amount.abs();
        if (isCredit) {
            amount = amount.multiply(-1);
        }
        return amount;
    }

    public static double getCurrency(String str) {
        double value;
        try {
            value = currencyFormat.parse(str).doubleValue();
        } catch (ParseException ex) {
            try {
                value = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                value = 0;
            }
        }
        return value;
    }

    public static void setCurrency(double doubleVal) {
        // Needed only method for bean structure.
    }

}
