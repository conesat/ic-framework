package cn.icframework.temp.vue;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class VueApiTemplate {
    public final static String VUE_API_TEMPLATE =  """
            import BaseService from '@/api/common/baseService';
                        
            const path = '/sys/#LU_CASE_TO_CHAR_SPLIT';
                        
            class Api#MODEL_NAME_FIST_UP extends BaseService {}
                        
            export default new Api#MODEL_NAME_FIST_UP(path);
            """;
}
