package cn.icframework;

/**
 * @author hzl
 * @since 2024/9/19
 */
public interface Temp {
    String TABLE = """
            package #PACKAGE;

            import cn.icframework.mybatis.annotation.Id;
            import cn.icframework.mybatis.annotation.Table;
            import cn.icframework.mybatis.annotation.TableField;
            import cn.icframework.mybatis.consts.IdType;
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
                @TableField(comment = "名称")
                private String name;
    
                /**
                * 创建时间
                */
                @TableField(notNull = true, comment = "创建时间", onInsertValue = "now()")
                private LocalDateTime createTime;
    
                /**
                * 更新时间
                */
                @TableField(comment = "更新时间", onUpdateValue = "now()")
                private LocalDateTime updateTime;
            }
            """;
}
