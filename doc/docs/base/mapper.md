# Mapper

```java
package cn.icframework.system.module.notice.dao;

import cn.icframework.mybatis.mapper.BasicMapper;
import cn.icframework.system.module.notice.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
* 需要继承 BasicMapper<实体>
* @author ic
* @date 2024/09/12
*/
@Mapper
public interface NoticeMapper extends BasicMapper<Notice> {
}

```