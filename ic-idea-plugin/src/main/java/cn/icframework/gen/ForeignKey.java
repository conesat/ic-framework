package cn.icframework.gen;

import com.intellij.psi.PsiClass;

public class ForeignKey {
    private String name;
    private PsiClass references;
    private String referencesColumn;
    private String onDelete;
    private String onUpdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PsiClass getReferences() {
        return references;
    }

    public void setReferences(PsiClass references) {
        this.references = references;
    }

    public String getReferencesColumn() {
        return referencesColumn;
    }

    public void setReferencesColumn(String referencesColumn) {
        this.referencesColumn = referencesColumn;
    }

    public String getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(String onDelete) {
        this.onDelete = onDelete;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
    }
}
