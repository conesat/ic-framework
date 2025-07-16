package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.Funcs;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.wrapper.function.MysqlFunctionProvider;
import cn.icframework.mybatis.wrapper.function.SqlFunctionProvider;
import cn.icframework.mybatis.wrapper.function.SqlFunctionProviderFactory;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * FunctionWrapper 提供了常用 SQL 聚合与函数操作的静态方法封装，
 * 便于在 MyBatis 查询构建中以链式、类型安全的方式生成带函数的字段表达式。
 * <p>
 * 主要功能：
 * <ul>
 * <li>封装常用 SQL 函数（如 MAX、MIN、AVG、SUM、COUNT、UPPER、LOWER、LENGTH、ROUND、DATE_FORMAT
 * 等），
 * 通过静态方法快速生成对应的 QueryField 或 QueryTable 对象。</li>
 * <li>支持字符串处理、数值处理、日期处理等多种 SQL 函数表达式的构建。</li>
 * <li>支持 EXISTS/NOT EXISTS 子查询、GROUP_CONCAT 聚合拼接等高级 SQL 用法。</li>
 * <li>通过重载方法，既可传入 QueryField 字段对象，也可直接传入字段名字符串，灵活适配不同场景。</li>
 * <li>部分方法支持参数定制（如 SUBSTRING、ROUND、DATE_FORMAT、GROUP_CONCAT 等）。</li>
 * </ul>
 * <p>
 * 适用场景：
 * <ul>
 * <li>在 MyBatis 查询构建、动态 SQL 生成、复杂报表统计等场景下，
 * 需要以面向对象方式拼接 SQL 聚合/函数表达式时使用。</li>
 * <li>配合自定义 Wrapper、SelectWrapper、SqlWrapper 等类，实现更灵活的 SQL 组装。</li>
 * </ul>
 * <p>
 * 注意事项：
 * <ul>
 * <li>部分函数（如 CONCAT）参数类型需注意，字符串常量会自动加引号，字段对象会自动取表字段名。</li>
 * <li>部分方法参数不可为 null，使用时需保证传参有效。</li>
 * <li>本类仅负责 SQL 片段的表达式生成，不直接执行 SQL。</li>
 * </ul>
 *
 * @author 自动生成
 */
public class FunctionWrapper {
    /**
     * SQL 函数提供者
     * 根据具体的数据库类型，提供不同的 SQL 函数实现。
     */
    @Setter
    private static SqlFunctionProvider provider = new MysqlFunctionProvider();
    /**
     * 自动设置 SQL 函数提供者
     * 根据数据库类型，自动设置 SQL 函数提供者。
     *
     * @param dbType 数据库类型
     */
    public static void autoSetProvider(String dbType) {
        setProvider(SqlFunctionProviderFactory.getProvider(dbType));
    }

    /**
     * 获取字段的最大值 MAX(field)
     *
     * @param queryField 字段对象
     * @return 带有MAX函数的QueryField
     */
    public static QueryField<?> MAX(QueryField<?> queryField) {
        return MAX(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取字段的最大值 MAX(field)
     *
     * @param field 字段名
     * @return 带有MAX函数的QueryField
     */
    public static QueryField<?> MAX(String field) {
        return provider.max(field);
    }

    /**
     * 获取字段的最小值 MIN(field)
     *
     * @param queryField 字段对象
     * @return 带有MIN函数的QueryField
     */
    public static QueryField<?> MIN(QueryField<?> queryField) {
        return MIN(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取字段的最小值 MIN(field)
     *
     * @param field 字段名
     * @return 带有MIN函数的QueryField
     */
    public static QueryField<?> MIN(String field) {
        return provider.min(field);
    }

    /**
     * 获取字段的平均值 AVG(field)
     *
     * @param queryField 字段对象
     * @return 带有AVG函数的QueryField
     */
    public static QueryField<?> AVG(QueryField<?> queryField) {
        return AVG(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取字段的平均值 AVG(field)
     *
     * @param field 字段名
     * @return 带有AVG函数的QueryField
     */
    public static QueryField<?> AVG(String field) {
        return provider.avg(field);
    }

    /**
     * 获取字段的和 SUM(field)
     *
     * @param queryField 字段对象
     * @return 带有SUM函数的QueryField
     */
    public static QueryField<?> SUM(QueryField<?> queryField) {
        return SUM(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取字段的和 SUM(field)
     *
     * @param field 字段名
     * @return 带有SUM函数的QueryField
     */
    public static QueryField<?> SUM(String field) {
        return provider.sum(field);
    }

    /**
     * 字段转小写 LOWER(field)
     *
     * @param queryField 字段对象
     * @return 带有LOWER函数的QueryField
     */
    public static QueryField<?> LOWER(QueryField<?> queryField) {
        return LOWER(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 字段转小写 LOWER(field)
     *
     * @param field 字段名
     * @return 带有LOWER函数的QueryField
     */
    public static QueryField<?> LOWER(String field) {
        return provider.lower(field);
    }

    /**
     * 字段转大写 UPPER(field)
     *
     * @param queryField 字段对象
     * @return 带有UPPER函数的QueryField
     */
    public static QueryField<?> UPPER(QueryField<?> queryField) {
        return UPPER(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 字段转大写 UPPER(field)
     *
     * @param field 字段名
     * @return 带有UPPER函数的QueryField
     */
    public static QueryField<?> UPPER(String field) {
        return provider.upper(field);
    }

    /**
     * 获取字段长度 LENGTH(field)
     *
     * @param queryField 字段对象
     * @return 带有LENGTH函数的QueryField
     */
    public static QueryField<?> LENGTH(QueryField<?> queryField) {
        return LENGTH(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取字段长度 LENGTH(field)
     *
     * @param field 字段名
     * @return 带有LENGTH函数的QueryField
     */
    public static QueryField<?> LENGTH(String field) {
        return provider.length(field);
    }

    /**
     * 截取字符串 SUBSTRING(field, pos, len)
     *
     * @param queryField 字段对象
     * @param pos        起始位置
     * @param len        截取长度
     * @return 带有SUBSTRING函数的QueryField
     */
    public static QueryField<?> SUBSTRING(QueryField<?> queryField, int pos, int len) {
        return SUBSTRING(queryField.getAsNameOrNameWithTable(), pos, len);
    }

    /**
     * 截取字符串 SUBSTRING(field, pos, len)
     *
     * @param field 字段名
     * @param pos   起始位置
     * @param len   截取长度
     * @return 带有SUBSTRING函数的QueryField
     */
    public static QueryField<?> SUBSTRING(String field, int pos, int len) {
        return provider.substring(field, pos, len);
    }

    /**
     * 去除字符串首尾空格 TRIM(field)
     *
     * @param queryField 字段对象
     * @return 带有TRIM函数的QueryField
     */
    public static QueryField<?> TRIM(QueryField<?> queryField) {
        return TRIM(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 去除字符串首尾空格 TRIM(field)
     *
     * @param field 字段名
     * @return 带有TRIM函数的QueryField
     */
    public static QueryField<?> TRIM(String field) {
        return provider.trim(field);
    }

    /**
     * 四舍五入 ROUND(field, scale)
     *
     * @param queryField 字段对象
     * @param scale      保留小数位数
     * @return 带有ROUND函数的QueryField
     */
    public static QueryField<?> ROUND(QueryField<?> queryField, int scale) {
        return ROUND(queryField.getAsNameOrNameWithTable(), scale);
    }

    /**
     * 四舍五入 ROUND(field, scale)
     *
     * @param field 字段名
     * @param scale 保留小数位数
     * @return 带有ROUND函数的QueryField
     */
    public static QueryField<?> ROUND(String field, int scale) {
        return provider.round(field, scale);
    }

    /**
     * 向上取整 CEIL(field)
     *
     * @param queryField 字段对象
     * @return 带有CEIL函数的QueryField
     */
    public static QueryField<?> CEIL(QueryField<?> queryField) {
        return CEIL(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 向上取整 CEIL(field)
     *
     * @param field 字段名
     * @return 带有CEIL函数的QueryField
     */
    public static QueryField<?> CEIL(String field) {
        return provider.ceil(field);
    }

    /**
     * 向下取整 FLOOR(field)
     *
     * @param queryField 字段对象
     * @return 带有FLOOR函数的QueryField
     */
    public static QueryField<?> FLOOR(QueryField<?> queryField) {
        return FLOOR(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 向下取整 FLOOR(field)
     *
     * @param field 字段名
     * @return 带有FLOOR函数的QueryField
     */
    public static QueryField<?> FLOOR(String field) {
        return provider.floor(field);
    }

    /**
     * 绝对值 ABS(field)
     *
     * @param queryField 字段对象
     * @return 带有ABS函数的QueryField
     */
    public static QueryField<?> ABS(QueryField<?> queryField) {
        return ABS(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 绝对值 ABS(field)
     *
     * @param field 字段名
     * @return 带有ABS函数的QueryField
     */
    public static QueryField<?> ABS(String field) {
        return provider.abs(field);
    }

    /**
     * 获取当前时间 NOW()
     *
     * @return 带有NOW函数的QueryField
     */
    public static QueryField<?> NOW() {
        return provider.now();
    }

    /**
     * 日期格式化 DATE_FORMAT(field, format)
     *
     * @param queryField 字段对象
     * @param format     格式字符串
     * @return 带有DATE_FORMAT函数的QueryField
     */
    public static QueryField<?> DATE_FORMAT(QueryField<?> queryField, String format) {
        return DATE_FORMAT(queryField.getAsNameOrNameWithTable(), format);
    }

    /**
     * 日期格式化 DATE_FORMAT(field, format)
     *
     * @param field  字段名
     * @param format 格式字符串
     * @return 带有DATE_FORMAT函数的QueryField
     */
    public static QueryField<?> DATE_FORMAT(String field, String format) {
        return provider.dateFormat(field, format);
    }

    /**
     * 获取年份 YEAR(field)
     *
     * @param queryField 字段对象
     * @return 带有YEAR函数的QueryField
     */
    public static QueryField<?> YEAR(QueryField<?> queryField) {
        return YEAR(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取年份 YEAR(field)
     *
     * @param field 字段名
     * @return 带有YEAR函数的QueryField
     */
    public static QueryField<?> YEAR(String field) {
        return provider.year(field);
    }

    /**
     * 获取月份 MONTH(field)
     *
     * @param queryField 字段对象
     * @return 带有MONTH函数的QueryField
     */
    public static QueryField<?> MONTH(QueryField<?> queryField) {
        return MONTH(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取月份 MONTH(field)
     *
     * @param field 字段名
     * @return 带有MONTH函数的QueryField
     */
    public static QueryField<?> MONTH(String field) {
        return provider.month(field);
    }

    /**
     * 获取天 DAY(field)
     *
     * @param queryField 字段对象
     * @return 带有DAY函数的QueryField
     */
    public static QueryField<?> DAY(QueryField<?> queryField) {
        return DAY(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 获取天 DAY(field)
     *
     * @param field 字段名
     * @return 带有DAY函数的QueryField
     */
    public static QueryField<?> DAY(String field) {
        return provider.day(field);
    }

    /**
     * 计数
     *
     * @param queryField 字段对象
     * @return 带有COUNT函数的QueryField
     */
    public static QueryField<?> COUNT(QueryField<?> queryField) {
        return COUNT(queryField.getAsNameOrNameWithTable());
    }

    /**
     * 计数
     *
     * @param field 字段名
     * @return 带有COUNT函数的QueryField
     */
    public static QueryField<?> COUNT(String field) {
        return provider.count(field);
    }

    /**
     * 计数
     *
     * @return 带有COUNT函数的QueryField
     */
    public static QueryField<?> COUNT() {
        return provider.count();
    }

    /**
     * 聚合拼接
     *
     * @param groupConcat 聚合拼接对象
     * @return 带有GROUP_CONCAT函数的QueryField
     */
    public static QueryField<?> GROUP_CONCAT(GroupConcat groupConcat) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(groupConcat.toGroupConcat());
        return queryField;
    }

    /**
     * 存在
     */
    public static QueryTable<?> EXISTS(SqlWrapper sqlWrapper) {
        QueryTable<?> queryTable = new QueryTable<>();
        queryTable.setFunc(Funcs.EXISTS);
        queryTable.setSqlWrapper(sqlWrapper);
        return queryTable;
    }

    /**
     * 不存在
     */
    public static QueryTable<?> NOT_EXISTS(SqlWrapper sqlWrapper) {
        QueryTable<?> queryTable = new QueryTable<>();
        queryTable.setFunc(Funcs.NOT_EXISTS);
        queryTable.setSqlWrapper(sqlWrapper);
        return queryTable;
    }

    /**
     * 任意值
     *
     * @param queryField 字段对象
     * @return 带有ANY_VALUE函数的QueryField
     */
    public static QueryField<?> ANY_VALUE(QueryField<?> queryField) {
        return ANY_VALUE(queryField.getNameWithTable());
    }

    /**
     * 任意值
     *
     * @param column 字段名
     * @return 带有ANY_VALUE函数的QueryField
     */
    public static QueryField<?> ANY_VALUE(String column) {
        return provider.anyValue(column);
    }

    /**
     * 拼接字段
     *
     * @param vals
     * @return
     */
    public static QueryField<?> CONCAT(@NonNull Object... vals) {
        return provider.concat(vals);
    }

    /**
     * 去重
     *
     * @param queryField
     * @return
     */
    public static QueryField<?> DISTINCT(@NonNull QueryField<?> queryField) {
        return provider.distinct(queryField.getNameWithTable());
    }

    /**
     * 聚合拼接
     */
    public static class GroupConcat {
        private final String column;
        private String orderColumn;
        private String separator;
        private boolean orderAsc;

        public GroupConcat(String column) {
            this.column = column;
        }

        public GroupConcat(QueryField<?> column) {
            this.column = column.getNameWithTable();
        }

        public GroupConcat orderColumn(String orderColumn) {
            this.orderColumn = orderColumn;
            return this;
        }

        public GroupConcat orderColumn(QueryField<?> orderColumn) {
            this.orderColumn = orderColumn.getNameWithTable();
            return this;
        }

        public GroupConcat separator(String separator) {
            this.separator = separator;
            return this;
        }

        public GroupConcat orderAsc(boolean orderAsc) {
            this.orderAsc = orderAsc;
            return this;
        }

        private String toGroupConcat() {
            if (column == null) {
                throw new RuntimeException("column 参数必填");
            }

            if (orderColumn != null && separator != null) {
                return String.format("GROUP_CONCAT(%s ORDER BY %s %s SEPARATOR '%s')", column, orderColumn,
                        orderAsc ? "ASC" : "DESC", separator);
            }

            if (orderColumn != null) {
                return String.format("GROUP_CONCAT(%s ORDER BY %s %s)", column, orderColumn, orderAsc ? "ASC" : "DESC");
            }

            if (separator != null) {
                return String.format("GROUP_CONCAT(%s SEPARATOR '%s')", column, separator);
            }

            return String.format("GROUP_CONCAT(%s)", column);
        }
    }
}
