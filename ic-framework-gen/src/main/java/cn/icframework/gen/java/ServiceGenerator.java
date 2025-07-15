package cn.icframework.gen.java;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.regex.Matcher;

import static cn.icframework.gen.template.java.ServiceTemplate.SERVICE_TEMPLATE;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class ServiceGenerator {
    private final Generator.Info info;

    public ServiceGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        String packageName = info.getTableClass() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getServiceJavaPath()) ? info.getServiceJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/service";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        String idType = "String";
        if (info.getTableClass() != null) {
            Field[] fields = info.getTableClass().getDeclaredFields();
            for (Field field : fields) {
                Id id = field.getDeclaredAnnotation(Id.class);
                if (id != null) {
                    idType = field.getType().getSimpleName();
                }
            }
        }
        GenUtils.createFile(javaPath, StringUtils.isEmpty(info.getServiceJavaPath()), info.getModelNameFistUp() + "Service.java", SERVICE_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#ID_EMPTY", idType.equals("String") ? "StringUtils.hasLength(dto.getId())" : "dto.getId() != null")
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate()));
    }

}
