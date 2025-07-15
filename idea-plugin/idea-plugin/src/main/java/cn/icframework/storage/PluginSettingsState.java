package cn.icframework.storage;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;


@Service
@State(name = "Settings", storages = @Storage("Settings.xml"))
public final class PluginSettingsState implements PersistentStateComponent<PluginSettingsState> {

    private String tabelDefPath = "";
    private String javaPath = "";
    private String vuePath = "";
    private String menuInitJsonPath = "";
    private String vueRouterParent = "";


    @Override
    public PluginSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getVuePath() {
        return vuePath;
    }

    public void setVuePath(String vuePath) {
        this.vuePath = vuePath;
    }

    public String getMenuInitJsonPath() {
        return menuInitJsonPath;
    }

    public void setMenuInitJsonPath(String menuInitJsonPath) {
        this.menuInitJsonPath = menuInitJsonPath;
    }

    public String getTabelDefPath() {
        return tabelDefPath;
    }

    public void setTabelDefPath(String tabelDefPath) {
        this.tabelDefPath = tabelDefPath;
    }

    public String getVueRouterParent() {
        return vueRouterParent;
    }

    public void setVueRouterParent(String vueRouterParent) {
        this.vueRouterParent = vueRouterParent;
    }
}
