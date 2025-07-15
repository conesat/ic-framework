package cn.icframework.gen.vue;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static cn.icframework.gen.template.vue.VueIndexTemplate.VUE_INDEX_TEMPLATE;

/**
 * @author iceFire
 * @since 2023/6/6
 */
public class IndexGenerator {
    private final Generator.Info info;

    public IndexGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableClass() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        String moduleName = info.getModuleName() == null ? "" : "/" + info.getModuleName();
        String content = getTableFieldsContent(info.getTableClass());
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getPageVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getPageVueSrcPath();
            savePath = pathPrefix + "/pages/" + info.getModuleName() + "/" + luCaseToCharSplit;
        }
        GenUtils.createFile(savePath, StringUtils.isEmpty(info.getPageVueSrcPath()), "index.vue", VUE_INDEX_TEMPLATE
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
    private String getTableFieldsContent(Class<?> entityClass) {
        if (info.getTableClass() == null) {
            return "";
        }
        List<String> tableFields = new ArrayList<>();
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = declaredField.getDeclaredAnnotation(TableField.class);
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                if (tableField == null || id != null) {
                    continue;
                }
                tableFields.add(String.format("  {title: '%s', colKey: '%s'},", StringUtils.isNotEmpty(tableField.comment()) ? tableField.comment() : declaredField.getName(), declaredField.getName()));
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);
        return String.join("\n", tableFields);
    }



}
