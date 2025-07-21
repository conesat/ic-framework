package cn.icframework.gen.vue;

import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Info;
import cn.icframework.gen.TableInfo;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

import static cn.icframework.temp.vue.VueRouterJsonTemplate.VUE_ROUTER_JSON_TEMPLATE;


/**
 * @author iceFire
 * @since 2023/6/6
 */
public class RouterGenerator {
    private final Info info;

    public RouterGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        if (info.getTableInfo() == null) {
            return;
        }
        String packageName = info.getPackageName();
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(info.getModelName(), "-");
        String moduleName = info.getModuleName() == null ? "" : info.getModuleName();
        moduleName = StringUtils.isEmpty(moduleName) ? "" : "/" + moduleName;
        String pathPrefix;
        String savePath;
        if (StringUtils.isEmpty(info.getVueRouterPath())) {
            pathPrefix = System.getProperty("user.dir") + "/target/gen/";
            savePath = pathPrefix + packageName.replace(".", "/") + "/vue";
        } else {
            pathPrefix = info.getVueRouterPath();
            savePath = pathPrefix + "/router/modules";
        }
        TableInfo table = info.getTableInfo();
        String name = table == null ? info.getModelName() : table.getComment();
//        GenUtils.createFile(savePath, info.getOver(), info.getModelNameFistDown() + ".ts", VUE_ROUTER_TEMPLATE
//                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
//                .replaceAll("#MODULE", moduleName)
//                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
//                .replaceAll("#MODEL_SPLIT_NAME", luCaseToCharSplit)
//                .replaceAll("#TABLE_NAME", name)
//        );

        String content = VUE_ROUTER_JSON_TEMPLATE
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODULE", moduleName)
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#MODEL_SPLIT_NAME", luCaseToCharSplit)
                .replaceAll("#TABLE_NAME", name);

        JSONObject contentJSON = JSONObject.parseObject(content);
        String string = GenUtils.readFile(info.getRouterInitJsonPath());
        JSONArray jsonArray = JSONArray.parseArray(StringUtils.isEmpty(string) ? "[]" : string);
        boolean has = false;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String path = jsonObject.getString("path");
            if (path.equals(info.getModuleName()) || path.equals("/" + info.getModuleName())) {
                if (info.getOver()) {
                    jsonArray.remove(i);
                    break;
                }
                JSONArray children = jsonObject.getJSONArray("children");
                boolean hasIndex = false;
                boolean hasEdit = false;
                if (children == null) {
                    children = new JSONArray();
                    jsonObject.put("children", children);
                } else {
                    for (int i1 = 0; i1 < children.size(); i1++) {
                        JSONObject child = children.getJSONObject(i1);
                        String childPath = child.getString("path");
                        if (childPath.equals(luCaseToCharSplit + "-index")) {
                            hasIndex = true;
                        }
                        if (childPath.equals(luCaseToCharSplit + "-edit")) {
                            hasEdit = true;
                        }
                    }
                }
                if (!hasIndex && !hasEdit) {
                    children.addAll(contentJSON.getJSONArray("children"));
                } else if (!hasIndex) {
                    children.add(contentJSON.getJSONArray("children").get(0));
                }else if (!hasEdit) {
                    children.add(contentJSON.getJSONArray("children").get(1));
                }
                has = true;
                break;
            }
        }
        if (!has) {
            jsonArray.add(contentJSON);
        }
        GenUtils.writeFile(new File(info.getRouterInitJsonPath()),
                JSON.toJSONString(jsonArray, JSONWriter.Feature.PrettyFormat));
    }

}
