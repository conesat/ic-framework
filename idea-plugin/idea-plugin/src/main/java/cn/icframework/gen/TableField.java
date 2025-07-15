package cn.icframework.gen;

public class TableField {
    private String name;
    private String tableColumnName;
    private String comment;
    private String typeSimpleName;
    private String typeName;
    /**
     * 是否是基础类型
     */
    private boolean primitive;
    private boolean id;
    private boolean notNull;
    private Integer length;

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTypeSimpleName() {
        return typeSimpleName;
    }

    public void setTypeSimpleName(String typeSimpleName) {
        this.typeSimpleName = typeSimpleName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getTableColumnName() {
        return tableColumnName;
    }

    public void setTableColumnName(String tableColumnName) {
        this.tableColumnName = tableColumnName;
    }
}
