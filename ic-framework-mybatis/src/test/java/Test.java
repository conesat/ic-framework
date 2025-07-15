import cn.icframework.mybatis.annotation.Id;
import cn.icframework.common.lambda.LambdaGetter;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author hzl
 * @since 2024/8/30
 */
public class Test {

    public static <T> String getFieldName(T lambda) {
        try {
            // 获取 Lambda 表达式的 SerializedLambda 对象
            Method writeReplaceMethod = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda)writeReplaceMethod.invoke(lambda);

            // 解析出方法名
            String methodName = serializedLambda.getImplMethodName();
            String prefix = methodName.startsWith("get") ? "get" : methodName.startsWith("is") ? "is" : "set";
            String fieldName = methodName.substring(prefix.length());
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

            Class<?> aClass = Class.forName(serializedLambda.getImplClass());
            // 使用反射获取字段
            Field field = aClass.getDeclaredField(fieldName);
            // 获取字段上的注解
            Annotation[] annotations = field.getDeclaredAnnotations();

            // 打印注解信息
            for (Annotation annotation : annotations) {
                System.out.println(annotation.annotationType().getName());
            }

            return fieldName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, R> String getAnnotationValue(LambdaGetter<T> getter) {
        String fieldName = getFieldName(getter);
        return null;
    }

    @Getter
    @Setter
    public class ExampleClass {
        @Id
        private String exampleField;

        // 其他字段和方法...
    }

    public static void main(String[] args) {
        String annotationValue = getAnnotationValue(ExampleClass::getExampleField);
        System.out.println("Annotation Value: " + annotationValue);
    }
}
