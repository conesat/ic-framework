package cn.icframework.gen.vue;

import cn.icframework.gen.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.icframework.temp.vue.VueSelectTemplate.VUE_SELECT_TEMPLATE;


/**
 * @author iceFire
 * @since 2023/6/6
 */
public class SelectGenerator {
    private final Info info;

    public SelectGenerator(Info info) {
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
        GenUtils.createFile(savePath, info.getOver(), "select.vue", VUE_SELECT_TEMPLATE
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
     * @param entityClass
     * @return
     */
    private String getTableFieldsContent(TableInfo entityClass) {
        if (info.getTableInfo() == null) {
            return "";
        }
        List<String> tableFields = new ArrayList<>();
        List<TableField> fields = entityClass.getFields();
        for (TableField field : fields) {
            if (field.isId()) {
                continue;
            }
            tableFields.add(String.format("  {title: '%s', colKey: '%s'},", StringUtils.isNotEmpty(field.getComment()) ? field.getComment() : field.getName(), field.getName()));
        }
        return String.join("\n", tableFields);
    }



}
