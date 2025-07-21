package cn.icframework.gen.java;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Info;
import cn.icframework.gen.TableField;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import static cn.icframework.temp.java.ServiceTemplate.SERVICE_TEMPLATE;


/**
 * @author hzl
 * @since 2023/6/5
 */
public class ServiceGenerator {
    private final Info info;

    public ServiceGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        String packageName = info.getTableInfo() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getServiceJavaPath()) ? info.getServiceJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/service";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        String idType = "String";
        if (info.getTableInfo() != null) {
            List<TableField> fields = info.getTableInfo().getFields();
            for (TableField field : fields) {
                if (field.isId()) {
                    idType = field.getTypeSimpleName();
                }
            }
        }
        GenUtils.createFile(javaPath, info.getOver(), info.getModelNameFistUp() + "Service.java", SERVICE_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#ID_EMPTY", idType.equals("String") ? "StringUtils.hasLength(dto.getId())" : "dto.getId() != null")
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate()));
    }

}
