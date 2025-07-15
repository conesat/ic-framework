package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.DataSet;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.ModelClassUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InsertWrapper {
    SqlWrapper sqlWrapper = new SqlWrapper(StatementType.INSERT);

    /**
     * INTO
     *
     * @param queryTable
     * @return
     */
    public InsertAfterQueryTableWrapper INTO(QueryTable<?> queryTable) {
        sqlWrapper.from(queryTable);
        List<String> vs = new ArrayList<>();
        Set<String> columns = new HashSet<>();
        for (DataSet set : queryTable.getSets()) {
            if (!columns.contains(set.getColumn())) {
                sqlWrapper.columns.add(set.getColumn());
                columns.add(set.getColumn());
            }
            vs.add(sqlWrapper.putParamSync(set.getValue()));
        }
        sqlWrapper.valuesList.add(vs);
        return new InsertAfterQueryTableWrapper(sqlWrapper);
    }

    /**
     * INTO
     * SELECT
     * @param tableEntity
     * @return
     */
    public InsertAfterWrapper INTO(Class<?> tableEntity) {
        sqlWrapper.fromEntity(tableEntity);
        ModelClassUtils.handleTableFieldInfo(tableEntity, field -> {
            sqlWrapper.columns.add(field.getTableColumnName());
            return true;
        });
        return new InsertAfterWrapper(sqlWrapper);
    }

}
