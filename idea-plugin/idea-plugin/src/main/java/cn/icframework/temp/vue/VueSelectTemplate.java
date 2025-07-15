package cn.icframework.temp.vue;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class VueSelectTemplate {
    public final static String VUE_SELECT_TEMPLATE = """
            <template>
              <div>
                <t-card class="list-card-container" :bordered="false">
                  <t-row justify="space-between">
                    <t-input auto-width v-model="queryForm.searchKey" placeholder="请输入你需要搜索的内容" clearable @enter="getData(true)">
                      <template #suffix-icon>
                        <search-icon size="16px" @click.stop="getData(true)"/>
                      </template>
                    </t-input>
                  </t-row>
                        
                  <t-table
                    :data="data"
                    :columns="columns"
                    row-key="id"
                    vertical-align="top"
                    :hover="true"
                    :pagination="queryForm"
                    :selected-row-keys="selectedRowKeys"
                    :loading="dataLoading"
                    :header-affixed-top="headerAffixedTop"
                    :sort="sort"
                    :multipleSort="true"
                    @change="reHandleChange"
                    @select-change="reHandleSelectChange"
                  >
                   <template #empty>
                      <t-space direction="vertical" style="text-align: center">
                        <span>暂无数据</span>
                        <t-button theme="default" @click="handleClickEdit">点击新建</t-button>
                      </t-space>
                   </template>
                  </t-table>
                  <t-row justify="end">
                    <t-button theme="default" variant="base" @click="cancel">取消</t-button>
                    <t-button theme="primary" @click="selected" :disabled="selectedRowKeys.length === 0">确认</t-button>
                  </t-row>
                </t-card>
              </div>
            </template>
                        
            <script lang="ts">
            export default {
              name: '#MODEL_NAME_FIST_DOWNSelect',
            };
            </script>
                        
            <script setup lang="ts">
            import {SearchIcon} from 'tdesign-icons-vue-next';
            import {PrimaryTableCol} from 'tdesign-vue-next';
            import {computed, onMounted, ref} from 'vue';
                        
            import Api#MODEL_NAME_FIST_UP from '@/api#MODULE/Api#MODEL_NAME_FIST_UP';
            import {prefix} from '@/config/global';
            import {useSettingStore} from '@/store';
            import query from "@/api/common/query";
                        
            const props = defineProps({
              mutilate: {
                type: Boolean,
                default: false
              }
            });
                        
            // 定义变量 start -------------------
            const emit = defineEmits(['selectSuccess', 'cancel']);
            // 读取设置内容
            const store = useSettingStore();
            // 排序
            const sort = ref([{
              // 按照 sort 字段进行排序
              sortBy: '',
              // 是否按照降序进行排序
              descending: false,
            }]);
            // 列表数据
            const data = ref([]);
            // 查询表单,包括分页
            const queryForm = ref({
               ...query,
            });
            // 数据是否加载中
            const dataLoading = ref(false);
            // 被选中的key
            const selectedRowKeys = ref([]);
            // 被选中的数据
            const selectedRowData = ref([]);
            // 表头
            const columns: PrimaryTableCol[] = [
              {colKey: 'row-select', type: props.mutilate ? 'multiple' : 'single', width: 64, fixed: 'left'},
            #CONTENT
            ];
            // 定义变量 end -------------------
                        
            // 定义方法 start -------------------
            // 获取列表数据
            const getData = async (reload ?: boolean) => {
              if (reload) {
                queryForm.value.current = 1;
                data.value = [];
              }
              dataLoading.value = true;
              Api#MODEL_NAME_FIST_UP.page(queryForm.value, (res: any) => {
                data.value = [...data.value, ...res.data];
                queryForm.value.total = res.total;
                dataLoading.value = false;
              });
              if (event){
                event.e.preventDefault();
              }
            };
            // 表单选中数据变化
            const reHandleSelectChange = (val: number[], e: any) => {
              selectedRowKeys.value = val;
              selectedRowData.value = e.selectedRowData;
            };
            // 排序变化
            const sortChange = (sortInfo: any) => {
              // 对于受控属性而言，这里的赋值很重要，不可缺少
              sort.value = sortInfo;
              queryForm.value.orders = JSON.stringify(sort.value)
            };
            // 表单参数变化 包括过滤、分页
            const reHandleChange = (changeParams: any, triggerAndData: unknown) => {
              queryForm.value = {
                ...queryForm.value,
                ...changeParams.pagination
              }
              getData();
            };
            // 取消选择
            const cancel = () => {
              emit('cancel');
            }
            // 确认选择
            const selected = () => {
              emit('selectSuccess', props.mutilate ? selectedRowData.value : selectedRowData.value[0]);
            }
            // 处理表单顶部距离
            const headerAffixedTop = computed(
              () =>
                ({
                  offsetTop: store.isUseTabsRouter ? 48 : 0,
                  container: `.${prefix}-layout`,
                } as any),
            );
            // 新建数据
            const handleClickEdit = () => {
              window.open(`#MODULE/#MODEL_SPLIT_NAME-edit?autoClose=true`);
            };
            // 定义方法 end -------------------
                        
            // vue生命周期
            onMounted(() => {
              getData();
            });
            </script>
                        
            <style lang="less" scoped>
                        
            </style>
            """;
}
