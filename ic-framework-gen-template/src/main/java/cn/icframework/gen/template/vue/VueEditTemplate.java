package cn.icframework.gen.template.vue;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class VueEditTemplate {
    public final static String VUE_EDIT_TEMPLATE = """
            <template>
              <t-form
                ref="form"
                class="base-form"
                :data="formData"
                :rules="FORM_RULES"
                label-align="top"
                :label-width="100"
                @keydown.enter.prevent
                @reset="onReset"
                @submit="onSubmit"
              >
                <div class="form-basic-container">
                  <div class="form-basic-item">
                    <div class="form-basic-container-title">编辑#TABLE_NAME</div>
                    <!-- 表单内容 -->
                    <t-row class="row-gap" :gutter="[32, 24]">
            #FORM_ITEMS
                    </t-row>
                  </div>
                </div>
                        
                <div class="form-submit-container">
                  <div class="form-submit-sub">
                    <div class="form-submit-left">
                      <t-button theme="primary" class="form-submit-confirm" type="submit"> 提交</t-button>
                      <t-button type="reset" class="form-submit-cancel" theme="default" variant="base"> 重置</t-button>
                    </div>
                  </div>
                </div>
              </t-form>
            </template>
                        
            <script lang="ts">
                        
            export default {
              name: '#MODEL_NAME_FIST_DOWNEdit',
            };
            </script>
                        
            <script setup lang="ts">
            import type { SubmitContext } from 'tdesign-vue-next';
            import { FormRule, MessagePlugin } from 'tdesign-vue-next';
            import { onMounted, ref } from 'vue';
            import { useRoute } from 'vue-router';
            import { closeOrBack } from "@/utils/url-utils";
            import Api#MODEL_NAME_FIST_UP from '@/api#MODULE/Api#MODEL_NAME_FIST_UP';
            import router from '@/router';
                        
            // 自定义校验 start -------------------
            // 自定义校验 end -------------------
                        
            // 定义变量 start -------------------
            // 路由
            const route = useRoute();
            // 表单
            const formData = ref({
            #FORM_CONTENT
            });
            // 表单校验
            const FORM_RULES: Record<string, FormRule[]> = {
            #FORM_RULES
            };
            // 定义变量 end -------------------
                        
            // 定义方法 start -------------------
            // 重置表单
            const onReset = () => {};
            // 提交表单
            const onSubmit = (ctx: SubmitContext) => {
              if (ctx.validateResult === true) {
                Api#MODEL_NAME_FIST_UP.edit({
                    data: formData.value,
                    success: (res: any) => {
                        MessagePlugin.success('已完成');
                        closeOrBack(route, router)
                    }
                });
              }
            };
            // 定义方法 end -------------------
                        
            // vue生命周期
            onMounted(() => {
              const { id } = route.query;
              if (id) {
                Api#MODEL_NAME_FIST_UP.detail(id, {
                    success: (res: any) => {
                        formData.value = res;
                    }
                });
              }
            });
            </script>
                        
            <style lang="less" scoped></style>
            """;
}
