package cn.icframework.gen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Info {
    private TableInfo tableInfo;
    private String entityJavaPath;
    private String apiJavaPath;
    private String tableDefPath;
    private String pojoJavaPath;
    private String serviceJavaPath;
    private String daoJavaPath;
    private String wrapperBuilderJavaPath;
    private String apiVueSrcPath;
    private String pageVueSrcPath;
    private String vueRouterPath;
    private String routerInitJsonPath;
    private String packageName;
    private String moduleName;
    private String modelName;
    private String modelNameFistUp;
    private String modelNameFistDown;
    private String author = "create by ic gen";
    private String date;
    private Boolean over = false;

    public String getModuleName() {
        return moduleName == null ? "" : moduleName;
    }

    public Info() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        this.date = dtf.format(LocalDateTime.now());
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getEntityJavaPath() {
        return entityJavaPath;
    }

    public void setEntityJavaPath(String entityJavaPath) {
        this.entityJavaPath = entityJavaPath;
    }

    public String getApiJavaPath() {
        return apiJavaPath;
    }

    public void setApiJavaPath(String apiJavaPath) {
        this.apiJavaPath = apiJavaPath;
    }

    public String getPojoJavaPath() {
        return pojoJavaPath;
    }

    public void setPojoJavaPath(String pojoJavaPath) {
        this.pojoJavaPath = pojoJavaPath;
    }

    public String getServiceJavaPath() {
        return serviceJavaPath;
    }

    public void setServiceJavaPath(String serviceJavaPath) {
        this.serviceJavaPath = serviceJavaPath;
    }

    public String getDaoJavaPath() {
        return daoJavaPath;
    }

    public void setDaoJavaPath(String daoJavaPath) {
        this.daoJavaPath = daoJavaPath;
    }

    public String getWrapperBuilderJavaPath() {
        return wrapperBuilderJavaPath;
    }

    public void setWrapperBuilderJavaPath(String wrapperBuilderJavaPath) {
        this.wrapperBuilderJavaPath = wrapperBuilderJavaPath;
    }

    public String getApiVueSrcPath() {
        return apiVueSrcPath;
    }

    public void setApiVueSrcPath(String apiVueSrcPath) {
        this.apiVueSrcPath = apiVueSrcPath;
    }

    public String getPageVueSrcPath() {
        return pageVueSrcPath;
    }

    public void setPageVueSrcPath(String pageVueSrcPath) {
        this.pageVueSrcPath = pageVueSrcPath;
    }

    public String getVueRouterPath() {
        return vueRouterPath;
    }

    public void setVueRouterPath(String vueRouterPath) {
        this.vueRouterPath = vueRouterPath;
    }

    public String getRouterInitJsonPath() {
        return routerInitJsonPath;
    }

    public void setRouterInitJsonPath(String routerInitJsonPath) {
        this.routerInitJsonPath = routerInitJsonPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelNameFistUp() {
        return modelNameFistUp;
    }

    public void setModelNameFistUp(String modelNameFistUp) {
        this.modelNameFistUp = modelNameFistUp;
    }

    public String getModelNameFistDown() {
        return modelNameFistDown;
    }

    public void setModelNameFistDown(String modelNameFistDown) {
        this.modelNameFistDown = modelNameFistDown;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTableDefPath() {
        return tableDefPath;
    }

    public void setTableDefPath(String tableDefPath) {
        this.tableDefPath = tableDefPath;
    }

    public Boolean getOver() {
        return over;
    }

    public void setOver(Boolean over) {
        this.over = over;
    }
}
