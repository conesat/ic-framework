package cn.icframework.actions;

import cn.icframework.common.Icons;
import cn.icframework.Temp;
import cn.icframework.ui.GenDialog;
import cn.icframework.utils.FieldUtils;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.charset.StandardCharsets;

public class GenAction extends AnAction implements DumbAware {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    boolean isDarcula = UIManager.getLookAndFeel().getName().toLowerCase().contains("darcula");

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Icon icon;
        if (isDarcula) {
            // 深色主题
            icon = Icons.GENERATED_DARK;
        } else {
            // 浅色主题
            icon = Icons.GENERATED;
        }
        Presentation presentation = e.getPresentation();
        presentation.setIcon(icon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || virtualFile == null) {
            JOptionPane.showMessageDialog(null, "Project or file context is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        GenDialog genDialog = new GenDialog();
        genDialog.show();
        if (genDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            String className = genDialog.getClassName();
            String classNameCN = genDialog.getClassNameCN();
            if (className == null || className.trim().isEmpty()) {
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    VirtualFile newFile = virtualFile.createChildData(null, className + ".java");
                    String packageName = "";
                    String path = virtualFile.getPresentableUrl().replace("\\", "/");
                    int index = path.lastIndexOf("src/main/java");
                    if (index != -1) {
                        path = path.substring(index + "src/main/java".length() + 1);
                        packageName = path.replace('/', '.');
                    }
                    newFile.setBinaryContent(
                            Temp.TABLE.replace("#PACKAGE", packageName)
                                    .replace("#TABLE_NAME_CN", classNameCN)
                                    .replace("#CLASS_NAME", className)
                                    .replace("#TABLE_NAME", FieldUtils.luCaseToUnderLine(className)).getBytes(StandardCharsets.UTF_8));

                    NotificationGroupManager.getInstance()
                            .getNotificationGroup("IcFrameworkWorkNotificationGroup")
                            .createNotification("Ic gen finish", NotificationType.INFORMATION)
                            .notify(project);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to create class file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

}