package cn.icframework.mybatis.handler;

import cn.icframework.common.interfaces.IEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.Assert;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * IEnum类型处理器。
 * 用于处理实现了IEnum接口的枚举类型。
 * @param <E> 枚举类型，必须实现IEnum接口
 * @author hzl
 */

public class IEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private final E[] enums;

    public IEnumTypeHandler(Class<E> type) {
        this.enums = Objects.requireNonNull(type.getEnumConstants(), "枚举至少要有一项");
        Assert.isAssignable(IEnum.class, type, "枚举必须实现cc.framework.mybatis.dao.type.IEnum接口");
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        IEnum e = (IEnum) parameter;
        if (jdbcType == null) {
            ps.setObject(i, e.code());
        } else {
            ps.setObject(i, e.code(), jdbcType.TYPE_CODE);
        }

    }

    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int key = rs.getInt(columnName);
        return key == 0 && rs.wasNull() ? null : this.mapEnum(key);
    }

    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int key = rs.getInt(columnIndex);
        return key == 0 && rs.wasNull() ? null : this.mapEnum(key);
    }

    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int key = cs.getInt(columnIndex);
        return key == 0 && cs.wasNull() ? null : this.mapEnum(key);
    }

    private E mapEnum(int code) {
        IEnum[] ies = (IEnum[]) this.enums;
        for (IEnum e : ies) {
            if (e.code() == code) {
                return (E) e;
            }
        }
        return null;
    }
}
