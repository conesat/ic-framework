package cn.icframework.gen.java;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Info;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class ModelGenerator {

    private final Info info;

    public ModelGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableInfo() != null) {
            return;
        }
        StringBuilder contentBuilder = new StringBuilder();

        String packageName = info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase();
        String savePathPrefix = StringUtils.isNotEmpty(info.getEntityJavaPath()) ? info.getEntityJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/");
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        GenUtils.createFile(javaPath, info.getOver(), info.getModelNameFistUp() + ".java", MODEL_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#TABLE", GenUtils.luCaseToUnderLine(info.getModelName()))
                .replaceAll("#DATE", info.getDate())
                .replaceAll("#CONTENT", contentBuilder.toString())
        );
    }


    final String MODEL_TEMPLATE = """
            package #PACKAGE_NAME;

            import annotation.cn.icframework.mybatis.Id;
            import annotation.cn.icframework.mybatis.Table;
            import annotation.cn.icframework.mybatis.TableField;
            import consts.cn.icframework.mybatis.IdType;
            import lombok.Getter;
            import lombok.Setter;

            import java.time.LocalDateTime;

            /**
             * @author #AUTHOR
             * @since #DATE
             */
            @Getter
            @Setter
            @Table("#TABLE")
            public class #MODEL_NAME_FIST_UP {
                #CONTENT
            }
            """;
}
