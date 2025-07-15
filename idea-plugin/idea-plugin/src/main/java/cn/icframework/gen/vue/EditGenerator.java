package cn.icframework.gen.vue;

import cn.icframework.common.MysqlType;
import cn.icframework.common.MysqlTypeMap;
import cn.icframework.gen.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.icframework.temp.vue.VueEditTemplate.VUE_EDIT_TEMPLATE;


/**
 * @author iceFire
 * @since 2023/6/6
 */
public class EditGenerator {
    private final Info info;

    public EditGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableInfo() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String moduleName = info.getModuleName() == null ? "" : "/" + info.getModuleName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        List<String> formContent = new ArrayList<>();
        List<String> formRules = new ArrayList<>();
        List<String> formItems = new ArrayList<>();
        getTableFieldsContent(info.getTableInfo(), formContent, formRules, formItems);
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getPageVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getPageVueSrcPath();
            savePath = pathPrefix + "/pages/" + info.getModuleName() + "/" + luCaseToCharSplit;
        }
        TableInfo table = info.getTableInfo();
        String name = table == null ? info.getModelName() : table.getComment();
        GenUtils.createFile(savePath, info.getOver(), "edit.vue", VUE_EDIT_TEMPLATE
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
     * @param tableInfo
     * @param formRules
     * @param formItems
     * @return
     */
    private void getTableFieldsContent(TableInfo tableInfo, List<String> formContent, List<String> formRules, List<String> formItems) {
        if (tableInfo == null) {
            return;
        }
        List<TableField> fields = tableInfo.getFields();
        for (TableField field : fields) {
            String fieldName = field.getName();
            String comment = StringUtils.isNotEmpty(field.getComment()) ? field.getComment() : fieldName;

            String initVal = "''";
            if (field.isId()) {
                MysqlType mysqlType = MysqlTypeMap.getType(field.getTypeName());
                if (mysqlType != null) {
                    initVal = mysqlType.getDefaultVueValue();
                }
                formContent.add(String.format("  %s: null,", fieldName));
            } else {
                formContent.add(String.format("  %s: %s,", fieldName, "''"));
            }

            if (field.isNotNull()) {
                formRules.add(String.format("  %s: [{required: true, message: '%s不能为空', type: 'error'}],", fieldName, comment));
            }

            if (!field.isId()) {
                int maxLength = field.getLength() != null ? field.getLength() : 255;
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
    }


}
