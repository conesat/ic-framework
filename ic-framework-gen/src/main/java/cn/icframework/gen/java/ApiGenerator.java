package cn.icframework.gen.java;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Matcher;

import static cn.icframework.gen.template.java.ApiTemplate.*;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class ApiGenerator {

    public static void build(Generator.Info info) {
        String packageName = info.getTableClass() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getApiJavaPath()) ? info.getApiJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/api";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        String tableComment = "";
        if (info.getTableClass() != null) {
            Table table = info.getTableClass().getDeclaredAnnotation(Table.class);
            if (table != null) {
                tableComment = table.comment();
            }
        }
        String buildApiContent = buildApiContent(packageName, info.getModelName(), info.getModelNameFistUp(), info.getModelNameFistDown(), info.getAuthor(), info.getDate(), tableComment);
        String buildPublicContent = buildPublicContent(packageName, info.getModelName(), info.getModelNameFistUp(), info.getModelNameFistDown(), info.getAuthor(), info.getDate(), tableComment);
        String buildAppContent = buildAppContent(packageName, info.getModelName(), info.getModelNameFistUp(), info.getModelNameFistDown(), info.getAuthor(), info.getDate(), tableComment);
        GenUtils.createFile(javaPath, StringUtils.isEmpty(info.getApiJavaPath()), "ApiSys" + info.getModelNameFistUp() + ".java", buildApiContent);
        GenUtils.createFile(javaPath, StringUtils.isEmpty(info.getApiJavaPath()), "ApiPublic" + info.getModelNameFistUp() + ".java", buildPublicContent);
        GenUtils.createFile(javaPath, StringUtils.isEmpty(info.getApiJavaPath()), "ApiApp" + info.getModelNameFistUp() + ".java", buildAppContent);
    }


    public static String buildApiContent(String packageName,
                                         String modelName,
                                         String modelNameFistUp,
                                         String modelNameFistDown,
                                         String author,
                                         String date,
                                         String tableComment) {
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(modelName, "-");
        return API_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", modelNameFistUp)
                .replaceAll("#MODEL_NAME_FIST_DOWN", modelNameFistDown)
                .replaceAll("#AUTHOR", author)
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
                .replaceAll("#TABLE_COMMENT", tableComment)
                .replaceAll("#DATE", date);
    }

    public static String buildAppContent(String packageName,
                                         String modelName,
                                         String modelNameFistUp,
                                         String modelNameFistDown,
                                         String author,
                                         String date,
                                         String tableComment) {
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(modelName, "-");
        return APP_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", modelNameFistUp)
                .replaceAll("#MODEL_NAME_FIST_DOWN", modelNameFistDown)
                .replaceAll("#AUTHOR", author)
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
                .replaceAll("#TABLE_COMMENT", tableComment)
                .replaceAll("#DATE", date);
    }

    public static String buildPublicContent(String packageName,
                                            String modelName,
                                            String modelNameFistUp,
                                            String modelNameFistDown,
                                            String author,
                                            String date,
                                            String tableComment) {
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(modelName, "-");
        return PUBLIC_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", modelNameFistUp)
                .replaceAll("#MODEL_NAME_FIST_DOWN", modelNameFistDown)
                .replaceAll("#AUTHOR", author)
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
                .replaceAll("#TABLE_COMMENT", tableComment)
                .replaceAll("#DATE", date);
    }
}
