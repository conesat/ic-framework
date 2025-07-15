package cn.icframework.gen.java;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;

import static cn.icframework.gen.template.java.WrapperBuilderTemplate.SQL_WRAPPER_TEMPLATE;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class WrapperBuilderGenerator {
    private final Generator.Info info;

    public WrapperBuilderGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        String packageName = info.getTableClass() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getWrapperBuilderJavaPath()) ? info.getWrapperBuilderJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/wrapperbuilder";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        GenUtils.createFile(javaPath, StringUtils.isEmpty(info.getWrapperBuilderJavaPath()), info.getModelNameFistUp() + "WrapperBuilder.java", SQL_WRAPPER_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate()));
    }

}
