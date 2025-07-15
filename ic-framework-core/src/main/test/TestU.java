import cn.icframework.common.enums.Sex;
import cn.icframework.common.enums.Status;
import convert.Convert;
import convert.Entity;
import convert.EntityVO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestU {

    @Test
    public void  t(){
        Convert convert = new Convert();
        List<Entity> entities = new ArrayList<>();
        Entity entity = new Entity();
        entity.setName("123");
        entity.setStatus(Status.DISABLE);
        entity.setSex(Sex.MEN);
        entities.add(entity);
        Entity entity2 = new Entity();
        entity2.setName("333");
        entity2.setStatus(Status.ENABLE);
        entities.add(entity2);
        List<EntityVO> convert1 = convert.convert(entities);
        System.out.println(convert1.toString());
    }
}
