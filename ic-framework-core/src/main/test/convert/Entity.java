package convert;

import cn.icframework.common.enums.Sex;
import cn.icframework.common.enums.Status;
import lombok.Data;

/**
 *
 * @author hzl
 * @since 2024/8/21
 */
@Data
public class Entity {
    private String name;
    private Status status;
    private Sex sex;
}
