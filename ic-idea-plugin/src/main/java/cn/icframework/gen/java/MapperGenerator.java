package cn.icframework.gen.java;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Info;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;

import static cn.icframework.temp.java.DaoTemplate.DAO_TEMPLATE;


/**
 * @author hzl
 * @since 2023/6/5
 */
public class MapperGenerator {
    private final Info info;

    public MapperGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        String packageName = info.getTableInfo() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getDaoJavaPath()) ? info.getDaoJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/dao";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        GenUtils.createFile(javaPath, info.getOver(), info.getModelNameFistUp() + "Mapper.java", DAO_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate()));
    }
}
