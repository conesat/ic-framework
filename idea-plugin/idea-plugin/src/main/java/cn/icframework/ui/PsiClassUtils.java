package cn.icframework.ui;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiNameValuePair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class PsiClassUtils {
    static Set<String> primitiveType = new HashSet<>();

    static {
        primitiveType.add("int");
        primitiveType.add("long");
        primitiveType.add("short");
        primitiveType.add("byte");
        primitiveType.add("float");
        primitiveType.add("double");
        primitiveType.add("char");
        primitiveType.add("boolean");
        primitiveType.add("string");
    }

    public static boolean isPrimitiveType(PsiField field) {
        return primitiveType.contains(field.getType().getPresentableText().toLowerCase());
    }

    /**
     * 获取指定注解的所有属性。
     *
     * @param annotation PsiAnnotation 实例
     * @return 包含所有属性名-值对的列表
     */
    public static PsiNameValuePair[] getAnnotationAttributes(PsiAnnotation annotation) {
        return annotation.getParameterList().getAttributes();
    }

    /**
     * 获取指定注解中某个属性的值。
     *
     * @param annotation    PsiAnnotation 实例
     * @param attributeName 要查找的属性名
     * @return 属性值对应的 PsiElement，如果没有找到则返回 null
     */
    public static String getAnnotationAttributeValueText(PsiAnnotation annotation, String attributeName) {
        for (PsiNameValuePair attribute : annotation.getParameterList().getAttributes()) {
            if (attributeName.equals(attribute.getName()) && attribute.getValue() != null) {
                String text = Objects.requireNonNull(attribute.getValue()).getText();
                if (text.length() >= 2) {
                    return text.substring(1, text.length() - 1);
                }
                return text;
                // 注意：这里返回的是 PsiElement，具体类型可能需要根据实际情况转换
            }
        }
        return null; // 没有找到指定属性的值
    }
}
