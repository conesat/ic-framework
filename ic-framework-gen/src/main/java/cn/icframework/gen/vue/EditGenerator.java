package cn.icframework.gen.vue;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import cn.icframework.mybatis.consts.MysqlType;
import cn.icframework.mybatis.consts.MysqlTypeMap;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.icframework.gen.template.vue.VueEditTemplate.VUE_EDIT_TEMPLATE;

/**
 * @author iceFire
 * @since 2023/6/6
 */
public class EditGenerator {
    private final Generator.Info info;

    public EditGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableClass() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String moduleName = info.getModuleName() == null ? "" : "/" + info.getModuleName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        List<String> formContent = new ArrayList<>();
        List<String> formRules = new ArrayList<>();
        List<String> formItems = new ArrayList<>();
        getTableFieldsContent(info.getTableClass(), formContent, formRules, formItems);
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getPageVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getPageVueSrcPath();
            savePath = pathPrefix + "/pages/" + info.getModuleName() + "/" + luCaseToCharSplit;
        }
        Table table = info.getTableClass().getDeclaredAnnotation(Table.class);
        String name = table == null ? info.getModelName() : table.comment();
        GenUtils.createFile(savePath, StringUtils.isEmpty(info.getPageVueSrcPath()), "edit.vue", VUE_EDIT_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#MODEL_NAME_UNDER_LINE", luCaseToCharSplit)
                .replaceAll("#FORM_CONTENT", String.join("\n", formContent))
                .replaceAll("#FORM_RULES", String.join("\n", formRules))
                .replaceAll("#FORM_ITEMS", String.join("\n", formItems))
                .replaceAll("#MODULE", moduleName)
                .replaceAll("#TABLE_NAME", name)
        );
    }

    /**
     * 遍历全部字段
     * 包括继承的
     *
     * @param entityClass
     * @param formRules
     * @param formItems
     * @return
     */
    private void getTableFieldsContent(Class<?> entityClass, List<String> formContent, List<String> formRules, List<String> formItems) {
        if (info.getTableClass() == null) {
            return;
        }
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = declaredField.getDeclaredAnnotation(TableField.class);
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                if (tableField == null && id == null) {
                    continue;
                }
                String fieldName = declaredField.getName();
                String comment = tableField != null && StringUtils.isNotEmpty(tableField.comment()) ? tableField.comment() : fieldName;

                String initVal = "''";
                if (id == null) {
                    MysqlType mysqlType = MysqlTypeMap.getType(declaredField.getType().toString().replace("class ", ""));
                    if (mysqlType != null) {
                        initVal = mysqlType.getDefaultVueValue();
                    }
                    formContent.add(String.format("  %s: %s,", fieldName, initVal));
                } else {
                    formContent.add(String.format("  %s: %s,", fieldName, "''"));
                }

                boolean notNull = declaredField.getDeclaredAnnotation(NotEmpty.class) != null;
                if (!notNull) {
                    notNull = declaredField.getDeclaredAnnotation(NotNull.class) != null;
                }
                if (notNull) {
                    formRules.add(String.format("  %s: [{required: true, message: '%s不能为空', type: 'error'}],", fieldName, comment));
                }

                if (id == null) {
                    Size size = declaredField.getDeclaredAnnotation(Size.class);
                    int maxLength = size != null ? size.max() : 255;
                    String item = """
                                      <t-col :span="6">
                                        <t-form-item label="%s" name="%s">
                                          <t-input v-model="formData.%s" placeholder="请输入%s" #MAX_LENGTH/>
                                        </t-form-item>
                                      </t-col>
                            """.replace("#MAX_LENGTH", Objects.equals(initVal, "''") ? ":maxlength=\"%d\"" : "");
                    formItems.add(String.format(item, comment, fieldName, fieldName, comment, maxLength));
                }
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);
    }


}
