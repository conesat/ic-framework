# Service



```java
package cn.icframework.system.module.notice.service;

import cn.icframework.core.basic.service.BasicService;
import cn.icframework.core.utils.BeanUtils;
import cn.icframework.system.module.notice.Notice;
import cn.icframework.system.module.notice.dao.NoticeMapper;
import cn.icframework.system.module.notice.pojo.dto.NoticeDTO;
import cn.icframework.system.module.noticereceiver.service.NoticeReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务代码，对于没有扩展需求的service 作者推荐直接写class不写接口，太累赘了
 * 需要继承 BasicService<NoticeMapper, 实体>
 * @author ic
 * @date 2024/09/12
 */
@Service
@RequiredArgsConstructor
public class NoticeService extends BasicService<NoticeMapper, Notice> {
    
    /**
     * 当实体新增或者更前的时候会调用，重写后在数据写入数据库前可以执行对应业务操作，如数据校验，赋值，加密
     * 可以不重写
     * @param sqlCommandType 操作类型
     * @param entity         操作前实体
     */
    @Override
    public void before(SqlCommandType sqlCommandType, Notice entity) {
        super.before(sqlCommandType, entity);
    }

    /**
     * 当实体新增或者更后的时候会调用，重写后在数据写入数据库后可以执行对应业务操作
     * 可以不重写
     * @param sqlCommandType 操作类型
     * @param entity         操作后实体
     */
    @Override
    public void after(SqlCommandType sqlCommandType, Notice entity) {
        super.after(sqlCommandType, entity);
    }

}

```