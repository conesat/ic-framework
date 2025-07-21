package cn.icframework;

/**
 * @author hzl
 * @since 2024/9/19
 */
public interface Temp {
    String TABLE = """
            package #PACKAGE;

            import annotation.cn.icframework.mybatis.Id;
            import annotation.cn.icframework.mybatis.Table;
            import annotation.cn.icframework.mybatis.TableField;
            import consts.cn.icframework.mybatis.IdType;
            import lombok.Getter;
            import lombok.Setter;

            import java.time.LocalDateTime;

            @Getter
            @Setter
            @Table(value = "#TABLE_NAME", comment = "#TABLE_NAME_CN")
            public class #CLASS_NAME {
    
                @Id(idType = IdType.SNOWFLAKE)
                private Long id;
    
                /**
                * 名称
                */
                @TableField(value = "name", comment = "名称")
                private String name;
    
                /**
                * 创建时间
                */
                @TableField(value = "create_time", notNull = true, comment = "创建时间", onInsertValue = "now()")
                private LocalDateTime createTime;
    
                /**
                * 更新时间
                */
                @TableField(value = "update_time", comment = "更新时间", onUpdateValue = "now()")
                private LocalDateTime updateTime;
            }
            """;
}
