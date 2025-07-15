import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMapTransfer {
    @Test
    public void testMap() {
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<String, Object> a = new HashMap<>();
        a.put("a.a", "a1");
        a.put("bs.b", "b1");
        a.put("bs.b.c", "c1");
        Map<String, Object> b = new HashMap<>();
        a.put("a.a", "a1");
        a.put("bs.b", "b2");
        a.put("bs.b.c", "c2");
        maps.add(a);
        maps.add(b);
//        List<A> as = MapTransfer.handlerResultMap(A.class, maps);
//        System.out.println(as);
    }

    @Getter
    @Setter
    static class A {
        private String a;
        private List<B> bs;
    }

    @Getter
    @Setter
    static class B {
        private String b;
        private C c;
    }

    @Getter
    @Setter
    static class C {
        private String c;
    }
}
