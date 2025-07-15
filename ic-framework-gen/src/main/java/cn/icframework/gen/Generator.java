package cn.icframework.gen;

import cn.icframework.gen.java.*;
import cn.icframework.gen.vue.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class Generator {
    private final Info info = new Info();

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
        this.info.setRouterVueSrcPath(vueSrcPath);
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
     * 保存到指定目录下面
     *
     * @return
     */
    public Generator vuePath(String vueSrcPath) {
        this.info.setApiVueSrcPath(vueSrcPath);
        this.info.setPageVueSrcPath(vueSrcPath);
        this.info.setRouterVueSrcPath(vueSrcPath);
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
    public Generator routerVueSrcPath(String path) {
        this.info.setRouterVueSrcPath(path);
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
     * @param tableClass
     * @return
     */
    public Generator tableClass(Class<?> tableClass) {
        this.info.setTableClass(tableClass);
        modelName(tableClass.getSimpleName());
        packageName(tableClass.getPackageName());
        return this;
    }

    public void build() {
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

    @Getter
    @Setter
    public class Info {
        private Class<?> tableClass;
        private String entityJavaPath;
        private String apiJavaPath;
        private String pojoJavaPath;
        private String serviceJavaPath;
        private String daoJavaPath;
        private String wrapperBuilderJavaPath;
        private String apiVueSrcPath;
        private String pageVueSrcPath;
        private String routerVueSrcPath;
        private String packageName;
        private String moduleName;
        private String modelName;
        private String modelNameFistUp;
        private String modelNameFistDown;
        private String author = "create by ic gen";
        private String date;

        public String getModuleName() {
            return moduleName == null ? "" : moduleName;
        }

        public Info() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            this.date = dtf.format(LocalDateTime.now());
        }
    }
}
