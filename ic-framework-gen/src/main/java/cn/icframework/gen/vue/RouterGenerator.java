package cn.icframework.gen.vue;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import static cn.icframework.gen.template.vue.VueRouterJsonTemplate.VUE_ROUTER_JSON_TEMPLATE;
import static cn.icframework.gen.template.vue.VueRouterTemplate.VUE_ROUTER_TEMPLATE;

/**
 * @author iceFire
 * @since 2023/6/6
 */
public class RouterGenerator {
    private final Generator.Info info;

    public RouterGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableClass() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        String moduleName = info.getModuleName() == null ? "" : info.getModuleName();
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getRouterVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getRouterVueSrcPath();
            savePath = pathPrefix + "/router/modules";
        }
        Table table = info.getTableClass().getDeclaredAnnotation(Table.class);
        String name = table == null ? info.getModelName() : table.comment();
        GenUtils.createFile(savePath, StringUtils.isEmpty(info.getRouterVueSrcPath()), info.getModelNameFistDown() + ".ts", VUE_ROUTER_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODULE", StringUtils.isEmpty(moduleName) ? "" : "/" + moduleName)
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#MODEL_SPLIT_NAME", luCaseToCharSplit)
                .replaceAll("#TABLE_NAME", name)
        );
        GenUtils.createFile(savePath, StringUtils.isEmpty(info.getRouterVueSrcPath()), info.getModelNameFistDown() + ".json", VUE_ROUTER_JSON_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODULE", StringUtils.isEmpty(moduleName) ? "" : "/" + moduleName)
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#MODEL_SPLIT_NAME", luCaseToCharSplit)
                .replaceAll("#TABLE_NAME", name)
        );
    }
}
