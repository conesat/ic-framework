package cn.icframework.gen.vue;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import static cn.icframework.gen.template.vue.VueApiTemplate.VUE_API_TEMPLATE;

/**
 * @author iceFire
 * @since 2023/6/6
 */
public class VueApiGenerator {
    private final Generator.Info info;

    public VueApiGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableClass() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getApiVueSrcPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getApiVueSrcPath();
            savePath = pathPrefix + "/api/" + info.getModuleName();
        }
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        GenUtils.createFile(savePath, StringUtils.isEmpty(info.getApiVueSrcPath()), "Api" + info.getModelNameFistUp() + ".ts", VUE_API_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
        );
    }

}
