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
public class EntityVO {
    private String name;
    private Integer status;
    private String statusText;
    private Integer sex;
    private String sexText;

    public String getStatusText() {
        return Status.instanceOf(status).text();
    }

    public String getSexText() {
        return Sex.instanceOf(sex).text();
    }
}
