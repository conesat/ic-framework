package cn.icframework.gen;

import cn.icframework.gen.java.*;
import cn.icframework.gen.vue.*;
import cn.icframework.ui.PsiClassUtils;
import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class Generator {
    private final Info info = new Info();
    private PsiClass psiClass;

    public Generator moduleName(String moduleName) {
        this.info.setModuleName(moduleName);
        return this;
    }

    public Generator packageName(String packageName) {
        this.info.setPackageName(packageName);
        return this;
    }

    public Generator modelName(String modelName) {
        String last = modelName.substring(1);
        this.info.setModelNameFistUp(modelName.substring(0, 1).toUpperCase() + last);
        this.info.setModelNameFistDown(modelName.substring(0, 1).toLowerCase() + last);
        this.info.setModelName(modelName);
        return this;
    }

    public Generator author(String author) {
        this.info.setAuthor(author);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator savePath(String javaPath, String vueSrcPath) {
        this.info.setApiJavaPath(javaPath);
        this.info.setDaoJavaPath(javaPath);
        this.info.setPojoJavaPath(javaPath);
        this.info.setServiceJavaPath(javaPath);
        this.info.setWrapperBuilderJavaPath(javaPath);
        this.info.setApiVueSrcPath(vueSrcPath);
        this.info.setPageVueSrcPath(vueSrcPath);
        this.info.setVueRouterPath(vueSrcPath);
        return this;
    }

    /**
     * 保存java到指定目录下面
     *
     * @return
     */
    public Generator javaPath(String javaPath) {
        this.info.setApiJavaPath(javaPath);
        this.info.setDaoJavaPath(javaPath);
        this.info.setPojoJavaPath(javaPath);
        this.info.setServiceJavaPath(javaPath);
        this.info.setWrapperBuilderJavaPath(javaPath);
        this.info.setEntityJavaPath(javaPath);
        return this;
    }

    /**
     * 保存java def到指定目录下面
     *
     * @return
     */
    public Generator tableDefPath(String tableDefPath) {
        this.info.setTableDefPath(tableDefPath);
        return this;
    }


    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator vuePath(String vueSrcPath) {
        this.info.setApiVueSrcPath(vueSrcPath);
        this.info.setPageVueSrcPath(vueSrcPath);
        this.info.setVueRouterPath(vueSrcPath);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator entityJavaPath(String path) {
        this.info.setEntityJavaPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator javaApiPath(String path) {
        this.info.setApiJavaPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator serviceJavaPath(String path) {
        this.info.setServiceJavaPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator pojoJavaPath(String path) {
        this.info.setPojoJavaPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator daoJavaPath(String path) {
        this.info.setDaoJavaPath(path);
        return this;
    }


    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator wrapperBuilderJavaPath(String path) {
        this.info.setWrapperBuilderJavaPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator vueRouterPath(String path) {
        this.info.setVueRouterPath(path);
        return this;
    }
    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator routerInitJsonPath(String path) {
        this.info.setRouterInitJsonPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator apiVueSrcPath(String path) {
        this.info.setApiVueSrcPath(path);
        return this;
    }

    /**
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator pageVueSrcPath(String path) {
        this.info.setPageVueSrcPath(path);
        return this;
    }

    /**
     * 通过java实体构建 不需要modelName、packageName了
     *
     * @param psiClass
     * @return
     */
    public Generator tablePsiClass(@NotNull PsiClass psiClass) {
        this.psiClass = psiClass;
        modelName(Objects.requireNonNull(psiClass.getName()));
        String qualifiedName = psiClass.getQualifiedName();
        assert qualifiedName != null;
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            packageName(qualifiedName.substring(0, lastDotIndex));
        }
        return this;
    }

    public void build() {
        fitTableInfo();
        DefGenerator.build(info);
        ApiGenerator.build(info);
        new OGenerator(info).build();
        new ModelGenerator(info).build();
        new ServiceGenerator(info).build();
        new MapperGenerator(info).build();
        new VueApiGenerator(info).build();
        new IndexGenerator(info).build();
        new EditGenerator(info).build();
        new RouterGenerator(info).build();
        new SelectGenerator(info).build();
        new WrapperBuilderGenerator(info).build();
    }
    public void buildDef() {
        fitTableInfo();
        DefGenerator.build(info);
    }

    private void fitTableInfo() {
        if (psiClass == null) {
            return;
        }

        TableInfo tableInfo = new TableInfo();
        PsiAnnotation table = psiClass.getAnnotation("cn.icframework.mybatis.annotation.Table");
        if (table != null) {
            tableInfo.setComment(PsiClassUtils.getAnnotationAttributeValueText(table, "comment"));
        }
        info.setTableInfo(tableInfo);

        do {
            PsiField[] fields = psiClass.getFields();
            for (PsiField field : fields) {
                PsiAnnotation tableFieldAnnotation = field.getAnnotation("cn.icframework.mybatis.annotation.TableField");
                PsiAnnotation id = field.getAnnotation("cn.icframework.mybatis.annotation.Id");
                if (tableFieldAnnotation == null && id == null) {
                    continue;
                }
                if (field.hasModifierProperty(PsiModifier.STATIC)) {
                    continue;
                }
                TableField tableField = new TableField();
                tableField.setPrimitive(PsiClassUtils.isPrimitiveType(field));
                tableField.setName(field.getName());
                tableField.setId(id != null);
                tableField.setTypeSimpleName(field.getType().getPresentableText());
                tableField.setTypeName(field.getType().getCanonicalText());
                if (tableFieldAnnotation != null) {
                    tableField.setComment(PsiClassUtils.getAnnotationAttributeValueText(tableFieldAnnotation, "comment"));
                    tableField.setNotNull(Objects.equals(PsiClassUtils.getAnnotationAttributeValueText(tableFieldAnnotation, "notNull"), "true"));
                    String value = PsiClassUtils.getAnnotationAttributeValueText(tableFieldAnnotation, "value");
                    tableField.setTableColumnName(StringUtils.isEmpty(value) ? tableField.getName() : value);
                    String lengthStr = PsiClassUtils.getAnnotationAttributeValueText(tableFieldAnnotation, "length");
                    if (lengthStr != null && !lengthStr.equals("-1")) {
                        try {
                            int length = Integer.parseInt(lengthStr);
                            tableField.setLength(length);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(field.getName() + "huo");
                        }
                    }
                } else {
                    tableField.setTableColumnName(tableField.getName());
                }
                tableInfo.getFields().add(tableField);
            }
            psiClass = psiClass.getSuperClass();
        } while (psiClass != null && !Objects.equals(psiClass.getQualifiedName(), "java.lang.Object"));
    }

    public void over(Boolean over) {
        this.info.setOver(over);
    }
}
