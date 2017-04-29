package hitec.com.util;

import java.math.BigDecimal;

public class StringUtil {


    public static boolean isEmpty(String value) {
        return ((value == null) || value.isEmpty() || ("".equals(value)));
    }

    public static String doubleToString(Double d) {
        if (d == null)
            return null;
        if (d.isNaN() || d.isInfinite())
            return d.toString();

        // pre java 8, a value of 0 would yield "0.0" below
        if (d.doubleValue() == 0)
            return "0";
        return new BigDecimal(d.toString()).stripTrailingZeros().toPlainString();
    }
}
