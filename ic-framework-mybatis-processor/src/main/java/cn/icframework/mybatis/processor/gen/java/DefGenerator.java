package cn.icframework.mybatis.processor.gen.java;

import cn.icframework.mybatis.processor.gen.FileUtils;
import cn.icframework.mybatis.processor.gen.GenUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static cn.icframework.gen.template.java.DefTemplate.DEF_TEMPLATE;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class DefGenerator {

    private static final String separator = "/";

    public static void genFile(
            Filer filer,
            String genFilePath,
            String packageName,
            String modelName,
            String modelNameFistUp,
            String modelNameFistDown,
            String date,
            String content,
            Element... elements) {
        Writer writer = null;
        try {
            JavaFileObject sourceFile = filer.createSourceFile(packageName.replace("/", ".") + ".def." + modelName + "Def", elements);
            if (genFilePath == null || genFilePath.isEmpty()) {
                writer = new OutputStreamWriter(sourceFile.openOutputStream(), StandardCharsets.UTF_8);
                writer.write(buildContent(packageName,
                        modelName,
                        modelNameFistUp,
                        modelNameFistDown,
                        date,
                        content
                ));
                writer.flush();
                return;
            }
            String defaultGenPath = sourceFile.toUri().getPath();
            // 全部替换为 /
            genFilePath = genFilePath.replace("\\", "/");
            defaultGenPath = defaultGenPath.replace("\\", "/");

            // 最终存放代码的目录
            String javaPath;
            if (genFilePath.endsWith("/")) {
                genFilePath = genFilePath.substring(0, genFilePath.length() - 1);
            }
            if (FileUtils.isAbsolutePath(genFilePath)) {
                javaPath = getJavaPath(genFilePath, packageName, defaultGenPath);
            } else if (StringUtils.isNotEmpty(genFilePath)) {
                javaPath = genFilePath + separator + packageName.replace(".", separator) + ".def";
            } else {
                javaPath = defaultGenPath.substring(0, defaultGenPath.lastIndexOf("/"));
            }
            File defFile = new File(javaPath + separator + modelName + "Def.java");
            String buildContent = buildContent(
                    packageName,
                    modelName,
                    modelNameFistUp,
                    modelNameFistDown,
                    date,
                    content
            );
            FileUtils.writerFile(defFile, buildContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // do nothing here.
                }
            }
        }

    }

    private static String getJavaPath(String genFilePath, String packageName, String defaultGenPath) {
        return defaultGenPath.substring(0, defaultGenPath.indexOf("/target/generated-sources/")) +
                separator +
                "src" +
                genFilePath +
                separator +
                packageName.replace(".", separator) +
                separator +
                "def";
    }

    private static String buildContent(
            String packageName,
            String modelName,
            String modelNameFistUp,
            String modelNameFistDown,
            String date,
            String content) {
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(modelName, "-");
        return DEF_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", modelNameFistUp)
                .replaceAll("#MODEL_NAME_FIST_DOWN", modelNameFistDown)
                .replaceAll("#AUTHOR", "ic generator")
                .replaceAll("#CONTENT", content)
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
                .replaceAll("#DATE", date);
    }
}
