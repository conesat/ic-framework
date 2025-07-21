package cn.icframework.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


/**
 *
 * @author hzl
 * @since 2024/9/19
 */
public class GenDialog extends DialogWrapper {
    JBTextField classNameTextField;
    JBTextField classNameCnTextField;

    public GenDialog() {
        super(true); // use current window as parent
        setTitle("Ic Class Template Gen");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        // 创建表单面板
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new VerticalFlowLayout());

        JPanel linePanel = new JPanel();
        linePanel.setLayout(new GridLayout(2, 2, 5, 5));

        // 添加表单组件
        linePanel.add(new JLabel("ClassName:"));
        classNameTextField = new JBTextField(20);
        classNameTextField.getEmptyText().setText("请输入Class实体名称");
        linePanel.add(classNameTextField);

        JPanel linePanel2 = new JPanel();
        linePanel2.setLayout(new GridLayout(2, 2, 5, 5));
        // 添加表单组件
        linePanel2.add(new JLabel("Comment:"));
        classNameCnTextField = new JBTextField(20);
        classNameCnTextField.getEmptyText().setText("请输入中文注释");
        linePanel2.add(classNameCnTextField);

        formPanel.add(linePanel);
        formPanel.add(linePanel2);
        dialogPanel.add(formPanel, BorderLayout.CENTER);

        return dialogPanel;
    }

    public String getClassName() {
        return classNameTextField.getText();
    }

    public String getClassNameCN() {
        return classNameCnTextField.getText();
    }
}
