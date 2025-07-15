package cn.icframework.mybatis.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 数据集类。
 * 用于封装查询结果数据集。
 * @author hzl
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataSet implements Cloneable {
    /**
     * 主表字段
     */
    protected String column;
    /**
     * 单个参数
     */
    protected Object value;

    // 假设MyObject类有实现clone方法
    @Override
    public DataSet clone() throws CloneNotSupportedException {
        return (DataSet) super.clone();
    }
}
