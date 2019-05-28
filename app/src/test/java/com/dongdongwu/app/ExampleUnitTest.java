package com.dongdongwu.app;

import android.util.Log;

import org.junit.Test;

import java.text.DecimalFormat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);

//        double v = testDoubleParse("0.");
//        String v = getDoubleDecimalFormat(".004000");
        boolean v = test("1..1.1", ".*\\..*\\..*");
        System.out.println(v);
    }

    private boolean test(String ss, String rule) {
        return ss.matches(rule);
    }

    private double testDoubleParse(String numberStr) {
        return Double.parseDouble(numberStr);
    }

    /**
     * 获取格式化的小数
     */
    private String getDoubleDecimalFormat(String numberStr) {
        if (numberStr.equals(".")) {
            numberStr = "0.0";
        }
        double number = 0;
        if (!numberStr.isEmpty()) {
            number = Double.valueOf(numberStr);
        }
        return formatDouble(number);
    }

    /**
     * 格式化小数
     */
    private String formatDouble(double number) {
//        String pattern = "#.#####";
        String pattern = "#";
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(number);
    }
}