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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


/**
 * @author hzl
 * @since 2024/9/19
 */
public class GenDefDialog extends DialogWrapper {
    private final AnActionEvent actionEvent;
    private final Project project;
    TextFieldWithBrowseButton tableDefPathTextField;
    PluginSettingsState state;

    public GenDefDialog(AnActionEvent e) {
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
        if (virtualFile != null) {
            String path = virtualFile.getPresentableUrl().replace("\\", "/");
            int index = path.lastIndexOf("src/main/java");
            if (StringUtils.isEmpty(tableDefPath)) {
                tableDefPath = path.substring(0, index) + "target/generated-sources/annotations/gen/java";
            }
        }

        tableDefPathTextField.setText(tableDefPath);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        // 创建表单面板
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new VerticalFlowLayout());

        JPanel tableDefPathPanel = new JPanel();
        tableDefPathPanel.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        tableDefPathPanel.add(new JLabel("TableDefPath:"));
        tableDefPathTextField = new TextFieldWithBrowseButton();
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

        dialogPanel.add(formPanel, BorderLayout.CENTER);

        initPath();
        return dialogPanel;
    }

    public String getTableDefPath() {
        return tableDefPathTextField.getText();
    }

}
