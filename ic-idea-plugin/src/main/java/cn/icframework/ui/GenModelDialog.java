package cn.icframework.ui;

import cn.icframework.storage.PluginSettingsState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


/**
 * @author hzl
 * @since 2024/9/19
 */
public class GenModelDialog extends DialogWrapper {
    private final AnActionEvent actionEvent;
    private final Project project;
    JBCheckBox overCheckBox;
    TextFieldWithBrowseButton javaPathTextField;
    TextFieldWithBrowseButton tableDefPathTextField;
    TextFieldWithBrowseButton vuePathTextField;
    JBTextField vueRouterParentTextField;
    TextFieldWithBrowseButton menuJsonPathTextField;
    PluginSettingsState state;

    public GenModelDialog(AnActionEvent e) {
        super(true); // use current window as parent
        this.actionEvent = e;
        this.project = e.getProject();
        state = Objects.requireNonNull(e.getProject()).getService(PluginSettingsState.class);
        setTitle("Ic Model Gen");
        init();
        setSize(500, -1);
    }

    private void initPath() {
        VirtualFile virtualFile = actionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        String tableDefPath = state.getTabelDefPath();
        String javaPath = state.getJavaPath();
        String menuInitJsonPath = state.getMenuInitJsonPath();
        String vuePath = state.getVuePath();
        if (virtualFile != null) {
            String path = virtualFile.getPresentableUrl().replace("\\", "/");
            int index = path.lastIndexOf("src/main/java");
            if (StringUtils.isEmpty(tableDefPath)) {
                tableDefPath = path.substring(0, index) + "target/generated-sources/annotations/gen/java";
            }
            if (StringUtils.isEmpty(javaPath)) {
                javaPath = path.substring(0, index) + "src/main/java";
            }
            if (StringUtils.isEmpty(menuInitJsonPath)) {
                menuInitJsonPath = path.substring(0, index) + "src/main/resources/init/menu/manager-menu.json";
            }
            if (StringUtils.isEmpty(vuePath)) {
                vuePath = project.getBasePath() + "/_web/admin/src";
            }
        }

        tableDefPathTextField.setText(tableDefPath);
        javaPathTextField.setText(javaPath);
        menuJsonPathTextField.setText(menuInitJsonPath);
        vuePathTextField.setText(vuePath);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        // 创建表单面板
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new VerticalFlowLayout());

        // 添加表单组件
        overCheckBox = new JBCheckBox("是否覆盖已有文件");
        formPanel.add(overCheckBox);

        JPanel javaPathPanel = new JPanel();
        javaPathPanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        javaPathPanel.add(new JLabel("JavaPath:"));
        JBTextField javaPathJBTF = new JBTextField();
        javaPathJBTF.getEmptyText().setText("Java文件路径，一般是java项目src/main/java");
        javaPathTextField = new TextFieldWithBrowseButton();
        javaPathTextField.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
            if (file != null) {
                state.setJavaPath(file.getPath());
                javaPathTextField.setText(file.getPath());
            }
        });
        javaPathPanel.add(javaPathTextField);
        formPanel.add(javaPathPanel);


        JPanel vuePathPanel = new JPanel();
        vuePathPanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        vuePathPanel.add(new JLabel("VuePath:"));
        JBTextField vuePathJBTF = new JBTextField();
        vuePathJBTF.getEmptyText().setText("Vue页面文件路径，一般是vue项目src");
        vuePathTextField = new TextFieldWithBrowseButton(vuePathJBTF);
        vuePathTextField.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
            if (file != null) {
                state.setVuePath(file.getPath());
                vuePathTextField.setText(file.getPath());
            }
        });
        vuePathPanel.add(vuePathTextField);
        formPanel.add(vuePathPanel);


        JPanel tableDefPathPanel = new JPanel();
        tableDefPathPanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        tableDefPathPanel.add(new JLabel("TableDefPath:"));
        JBTextField tableDefJBTF = new JBTextField();
        tableDefJBTF.getEmptyText().setText("java映射数据库Def类生成路径");
        tableDefPathTextField = new TextFieldWithBrowseButton(tableDefJBTF);
        tableDefPathTextField.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
            if (file != null) {
                state.setTabelDefPath(file.getPath());
                tableDefPathTextField.setText(file.getPath());
            }
        });
        tableDefPathPanel.add(tableDefPathTextField);
        formPanel.add(tableDefPathPanel);

        JPanel vueModulePanel = new JPanel();
        vueModulePanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        vueModulePanel.add(new JLabel("VueRouterParent:"));
        vueRouterParentTextField = new JBTextField();
        vueRouterParentTextField.getEmptyText().setText("设置一级菜单路由名称如：sys");
        vueRouterParentTextField.setText(state.getVueRouterParent());
        vueModulePanel.add(vueRouterParentTextField);
        formPanel.add(vueModulePanel);

        JPanel menuInitJsonPanel = new JPanel();
        menuInitJsonPanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        menuInitJsonPanel.add(new JLabel("MenuInitJson:"));
        JBTextField menuInitJsonJBTF = new JBTextField();
        menuInitJsonJBTF.getEmptyText().setText("菜单初始化文件，一般是java项目resources/init/menu/manager-menu.json");
        menuJsonPathTextField = new TextFieldWithBrowseButton(menuInitJsonJBTF);
        menuJsonPathTextField.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
            if (file != null) {
                state.setMenuInitJsonPath(file.getPath());
                menuJsonPathTextField.setText(file.getPath());
            }
        });
        menuInitJsonPanel.add(menuJsonPathTextField);
        formPanel.add(menuInitJsonPanel);

        dialogPanel.add(formPanel, BorderLayout.CENTER);

        initPath();
        return dialogPanel;
    }

    public String getJavaPath() {
        return javaPathTextField.getText();
    }

    public String getVuePath() {
        return vuePathTextField.getText();
    }

    public String getMenuInitJsonPath() {
        return menuJsonPathTextField.getText();
    }
    public String getTableDefPath() {
        return tableDefPathTextField.getText();
    }
    public String getVueRouterParent() {
        state.setVueRouterParent(vueRouterParentTextField.getText());
        return vueRouterParentTextField.getText();
    }

    public Boolean getOver() {
        return overCheckBox.isSelected();
    }

}
