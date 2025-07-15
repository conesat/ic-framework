package cn.icframework.actions;

import cn.icframework.gen.Generator;
import cn.icframework.ui.GenModelDialog;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class GenModelAction extends AnAction {
    PsiClass psiClass = null;

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Presentation presentation = e.getPresentation();

        // 将 PSI 操作移出 EDT
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
            psiClass = null;
            if (psiFile != null) {
                psiClass = ReadAction.compute(() -> {
                    for (PsiElement child : psiFile.getChildren()) {
                        if (child instanceof PsiClass pc) {
                            return pc;
                        }
                    }
                    return null;
                });
            }
            // 更新 UI 需要在 EDT 上执行
            ApplicationManager.getApplication().invokeLater(() -> {
                if (psiClass == null) {
                    presentation.setVisible(false);
                    presentation.setEnabled(false);
                    return;
                }
                boolean hasTableAnnotation = hasTableAnnotation(psiClass);
                presentation.setVisible(hasTableAnnotation);
                presentation.setEnabled(hasTableAnnotation);
            });
        });
    }


    private boolean hasTableAnnotation(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if ("cn.icframework.mybatis.annotation.Table".equals(annotation.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || virtualFile == null) {
            JOptionPane.showMessageDialog(null, "Project or file context is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        GenModelDialog genDialog = new GenModelDialog(e);
        genDialog.show();
        if (genDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Generator generator = new Generator();
                generator.author("ic");
                generator.over(genDialog.getOver());
                generator.moduleName(genDialog.getVueRouterParent());
                generator.tablePsiClass(psiClass);
                generator.javaPath(genDialog.getJavaPath());
                generator.tableDefPath(genDialog.getTableDefPath());
                generator.apiVueSrcPath(genDialog.getVuePath());
                generator.vuePath(genDialog.getVuePath());
                generator.vueRouterPath(genDialog.getVuePath());
                generator.routerInitJsonPath(genDialog.getMenuInitJsonPath());
                generator.build();

                NotificationGroupManager.getInstance()
                        .getNotificationGroup("IcFrameworkWorkNotificationGroup")
                        .createNotification("Ic gen finish", NotificationType.INFORMATION)
                        .notify(project);
            });
        }
    }
}
