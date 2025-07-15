package cn.icframework.mybatis.helper;

public class RelationHelper {
    private static final ThreadLocal<Boolean> threadLocalValue = ThreadLocal.withInitial(() -> true);

    public static void closeRelationQuery() {
        threadLocalValue.set(false);
    }

    public static void openRelationQuery() {
        threadLocalValue.set(true);
    }

    public static boolean getRelationQuery() {
        return threadLocalValue.get();
    }

    public static void remove() {
        threadLocalValue.remove();
    }
}
