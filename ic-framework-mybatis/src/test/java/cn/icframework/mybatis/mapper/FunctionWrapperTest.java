package cn.icframework.mybatis.mapper;

import cn.icframework.mybatis.wrapper.SelectWrapper;
import org.junit.jupiter.api.Test;

import static cn.icframework.mybatis.wrapper.Wrapper.SELECT;
import static cn.icframework.mybatis.wrapper.FunctionWrapper.*;

public class FunctionWrapperTest {

    @Test
    public void testMin() {
        SelectWrapper age = SELECT(MIN("age").as("1"));
        System.out.println(age.sql());
    }

    @Test
    public void testAvg() {
        SelectWrapper avg = SELECT(AVG("score"));
        System.out.println(avg.sql());
    }

    @Test
    public void testSum() {
        SelectWrapper sum = SELECT(SUM("amount"));
        System.out.println(sum.sql());
    }

    @Test
    public void testLower() {
        SelectWrapper lower = SELECT(LOWER("name"));
        System.out.println(lower.sql());
    }

    @Test
    public void testUpper() {
        SelectWrapper upper = SELECT(UPPER("name"));
        System.out.println(upper.sql());
    }

    @Test
    public void testLength() {
        SelectWrapper length = SELECT(LENGTH("name"));
        System.out.println(length.sql());
    }

    @Test
    public void testSubstring() {
        SelectWrapper substring = SELECT(SUBSTRING("name", 2, 3));
        System.out.println(substring.sql());
    }

    @Test
    public void testTrim() {
        SelectWrapper trim = SELECT(TRIM("name"));
        System.out.println(trim.sql());
    }

    @Test
    public void testRound() {
        SelectWrapper round = SELECT(ROUND("price", 2));
        System.out.println(round.sql());
    }

    @Test
    public void testCeil() {
        SelectWrapper ceil = SELECT(CEIL("price"));
        System.out.println(ceil.sql());
    }

    @Test
    public void testFloor() {
        SelectWrapper floor = SELECT(FLOOR("price"));
        System.out.println(floor.sql());
    }

    @Test
    public void testAbs() {
        SelectWrapper abs = SELECT(ABS("offset"));
        System.out.println(abs.sql());
    }

    @Test
    public void testNow() {
        SelectWrapper now = SELECT(NOW());
        System.out.println(now.sql());
    }

    @Test
    public void testDateFormat() {
        SelectWrapper dateFormat = SELECT(DATE_FORMAT("create_time", "%Y-%m-%d"));
        System.out.println(dateFormat.sql());
    }

    @Test
    public void testYear() {
        SelectWrapper year = SELECT(YEAR("create_time"));
        System.out.println(year.sql());
    }

    @Test
    public void testMonth() {
        SelectWrapper month = SELECT(MONTH("create_time"));
        System.out.println(month.sql());
    }

    @Test
    public void testDay() {
        SelectWrapper day = SELECT(DAY("create_time"));
        System.out.println(day.sql());
    }
} 