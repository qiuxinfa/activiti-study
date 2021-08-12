<template>
    <div>
        <el-form-item label="编号" required>
            <el-input v-model= "id"></el-input>
        </el-form-item>
        <el-form-item label="名称" required>
            <el-input v-model= "name"></el-input>
        </el-form-item>
        <el-form-item label="文档" v-show="false">
            <el-input type="textarea" v-model="documentation"></el-input>
        </el-form-item>
        <el-form-item label="多实例类型" v-show="false">
            <el-select v-model= "multiinstance_type"> <!--相当于bpmn2.0标准中的isSequential-->
                <el-option label="非多实例" value="None"></el-option>
                <el-option label="同时进行" value="Parallel"></el-option>
                <el-option label="顺序进行" value="Sequential"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="基数 (多实例)" v-show="false"><!--后端暂时没用到-->
            <el-input v-model= "multiinstance_cardinality" type="number"></el-input>
        </el-form-item>
        <el-form-item label="元素的变量(多实例)" v-show="false"><!--后端暂时没用到-->
            <el-input v-model= "multiinstance_variable" ></el-input>
        </el-form-item>
        <el-form-item label="通过权重[1-100]" v-show="false">
            <el-input v-model= "multiinstance_condition" type="number"></el-input>
        </el-form-item>
        <el-form-item label="表单标识">
            <el-input v-model= "formKey"></el-input>
        </el-form-item>
        <el-form-item label="任务监听器">
            <el-input v-model= "taskListener"></el-input>
        </el-form-item>
        <el-form-item label="任务派遣">
            <el-select v-model= "candidateGroups" multiple filterable placeholder="请选择">
                <el-option
                v-for=" p in positions"
                :key="p.id"
                :label="p.name"
                :value="p.id">
                </el-option>
            </el-select>
        </el-form-item>
    </div>
</template>
<script>

// 用户任务属性组件
export default {
    props:['bpmnModeler','element'],
    data(){
        return {
            modeling: null,
            id: this.element.id || '',
            name: '',
            formKey:'',
            documentation: '',
            multiinstance_type: '',
            multiinstance_collection: '',
            multiinstance_condition: '',
            multiinstance_cardinality: null,
            multiinstance_variable: null,
            taskListener: '',
            // 候选组
            positions:[
              {
                id: 1,
                name: '部门经理'
              },
              {
                id: 2,
                name: '人事主管'
              }],
            priority:'',
            candidateGroups: '没有受让人'
        }
    },
    methods:{

    },
    mounted() {
        this.modeling = this.bpmnModeler.get('modeling');
    },
    watch:{
        name: {
            handler(newVal, oldVal) {
                this.modeling.updateProperties(this.element,{
                    name: newVal
                });
            }
        },

        //监视元素变化
        element:{
            deep: true,
            immediate: true,
             handler(newVal,oldVal){
                 if(newVal.type == 'bpmn:UserTask') {
                     const modeling = this.bpmnModeler.get('modeling');
                     const businessObject = newVal.businessObject;
                     this.name = businessObject.name;
                     this.formKey = businessObject.get('formKey');
                     // 候选组
					 // debugger
                     const candidateGroupsTemp = businessObject.get('candidateGroups');
                     // 解决后端反显和切换节点反显candidateGroupsTemp类型不一致问题
                    if(candidateGroupsTemp && candidateGroupsTemp.length > 0) {
                        if(Array.isArray(candidateGroupsTemp)) {
                            //切换节点反显
                            this.candidateGroups = businessObject.get('candidateGroups');
                        } else {
                            //后端反显
                            this.candidateGroups = businessObject.get('candidateGroups').split(',');
                        }

                    }
                    // 存在拓展元素，提取任务监听器
                    if(businessObject.extensionElements && businessObject.extensionElements.values){
                      const tasks = businessObject.extensionElements.values
                      tasks.forEach(item => {
                        if(item.$type === 'activiti:TaskListener'){
                          this.taskListener = item.delegateExpression || item.$attrs.delegateExpression
                        }
                      })
                    }
                    // this.multiinstance_type = businessObject.get('multiinstance_type') || 'None';
                    // this.multiinstance_condition = businessObject.get('multiinstance_condition') || '';
                    // modeling.updateProperties(newVal,{'multiinstance_type':this.multiinstance_type});
                    // modeling.updateProperties(newVal,{'multiinstance_condition':this.multiinstance_condition});
                 }
             }
        },
        formKey:{
            handler(newVal,oldVal){
                this.modeling.updateProperties(this.element,{'formKey':newVal});
            }
        },
        multiinstance_type: {
            handler(newVal, oldVal) {
                this.modeling.updateProperties(this.element,{'multiinstance_type':newVal});
            }
        },
        multiinstance_collection: {
            handler(newVal, oldVal) {
                this.modeling.updateProperties(this.element,{'multiinstance_collection':newVal});
            }
        },
        multiinstance_condition: {
            handler(newVal, oldVal) {
                this.modeling.updateProperties(this.element,{'multiinstance_condition':newVal});
            }
        },
        priority: {
            handler(newVal, oldVal) {
                this.modeling.updateProperties(this.element,{'priority':newVal});
            }
        },
        candidateGroups: {
            handler(newVal,oldVal){
                this.modeling.updateProperties(this.element,{'candidateGroups':newVal});

            }
        },
        taskListener(newVal,oldVal){
          if(newVal) {
			// debugger
            const bpmnFactory = this.bpmnModeler.get('bpmnFactory');
            let extensionElements = this.element.businessObject.get('extensionElements');
            if(!extensionElements) {
              extensionElements = bpmnFactory.create('bpmn:ExtensionElements',{values: []});
            }
            const length = extensionElements.get('values').length;
            for (let i = 0; i < length; i++) {
              // 清除旧值
              extensionElements.get('values').pop();
            }
            const taskListener = bpmnFactory.create('activiti:TaskListener',{});
            taskListener.$attrs['event'] = 'all';
            taskListener.$attrs['delegateExpression'] = newVal;
            extensionElements.get('values').push(taskListener)
            const modeling = this.bpmnModeler.get('modeling');
            modeling.updateProperties(this.element, {
              extensionElements: extensionElements
            })
          }
        }
    }

}
</script>
<style>

</style>
