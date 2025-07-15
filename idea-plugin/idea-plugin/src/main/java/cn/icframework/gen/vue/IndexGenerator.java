package cn.icframework.gen.vue;

import cn.icframework.gen.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.icframework.temp.vue.VueIndexTemplate.VUE_INDEX_TEMPLATE;


/**
 * @author iceFire
 * @since 2023/6/6
 */
public class IndexGenerator {
    private final Info info;

    public IndexGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableInfo() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        String moduleName = info.getModuleName() == null ? "" : "/" + info.getModuleName();
        String content = getTableFieldsContent(info.getTableInfo());
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getPageVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getPageVueSrcPath();
            savePath = pathPrefix + "/pages/" + info.getModuleName() + "/" + luCaseToCharSplit;
        }
        GenUtils.createFile(savePath, info.getOver(), "index.vue", VUE_INDEX_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#CONTENT", content)
                .replaceAll("#MODULE", moduleName)
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#MODEL_SPLIT_NAME", luCaseToCharSplit)
        );
    }

    /**
     * 遍历全部字段
     * 包括继承的
     *
     * @param tableInfo
     * @return
     */
    private String getTableFieldsContent(TableInfo tableInfo) {
        if (tableInfo == null) {
            return "";
        }
        List<String> tableFields = new ArrayList<>();
        List<TableField> fields = tableInfo.getFields();
        for (TableField field : fields) {
            if (field.isId()) {
                continue;
            }
            tableFields.add(String.format("  {title: '%s', colKey: '%s'},", StringUtils.isNotEmpty(field.getComment()) ? field.getComment() : field.getName(), field.getName()));
        }
        return String.join("\n", tableFields);
    }


}
