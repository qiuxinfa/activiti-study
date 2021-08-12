<template>
  <div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="常规" name="first">
        <el-form-item label="编号" v-show="true">
          <el-input v-model="id" readonly></el-input>
        </el-form-item>
        <el-form-item label="流程名称" required>
          <el-input v-model="name"></el-input>
        </el-form-item>
        <el-form-item label="目标命名空间" v-show="false">
          <el-input v-model="process_namespace"></el-input>
        </el-form-item>
        <el-form-item label="候选开始组">
          <el-input v-model="candidateStarterGroups"></el-input>
        </el-form-item>
        <el-form-item label="候选开始用户">
          <el-input v-model="candidateStarterUsers"></el-input>
        </el-form-item>
        <el-form-item label="文档" v-show="false">
          <el-input type="textarea" v-model="documentation"></el-input>  <!-- documentation   ? -->
        </el-form-item>
      </el-tab-pane>
      <el-tab-pane label="监听" name="second">
        <el-form-item label="事件类型" >
          <el-select v-model="eventType" placeholder="请选择" multiple>
            <el-option label="开始" value="start"></el-option>
            <el-option label="结束" value="end"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="监听器类型" v-show="isShow">
          <el-select v-model="listenerType" placeholder="请选择">
            <el-option label="java类" value="class"></el-option>
            <el-option label="表达式" value="expression"></el-option>
            <el-option label="代理表达式" value="delegateExpression"></el-option>
            <el-option label="脚本" value="script"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-bind:label="listenerValueLabel" v-show="isShow">
          <el-input v-model="listenerValue"></el-input>
        </el-form-item>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>
<script>
// import bpmnHelper from '../js/helper/BpmnHelper';
// import elementHelper from '../js/helper/ElementHelper';
// import { commonParse, userTaskParse } from '../js/util/parseElement'

export default {
  //依赖注入
  props: ['bpmnModeler','element'],
  data () {
    return {
      id: '',
      name:  '',
      process_namespace:  '',
      documentation: '',
      activeName: 'first',
      candidateStarterGroups:'',
      candidateStarterUsers: '',
      listenerType: '',
      eventType: '',
      listenerValue: '',
      isShow: false,
      listenerValueLabel: 'Java类'

    }
  },
  created() {
    // 给表单属性赋值
    this.initData()
  },
  methods: {
    // 给表单属性赋值
    initData(){
      if(!this.element){
        return
      }
      let obj = this.element.businessObject
      if(obj.candidateStarterUsers){
        this.candidateStarterUsers = obj.candidateStarterUsers
      }
      if(obj.candidateStarterGroups){
        this.candidateStarterGroups = obj.candidateStarterGroups
      }
      // debugger
      if(!obj.extensionElements){
        return
      }
      let allExtensionElements = obj.extensionElements.values || []
      this.eventType = []
      allExtensionElements.forEach(item => {
        // 这里只提取执行监听器
        if(item.$type == 'activiti:ExecutionListener'){
          // debugger
          this.eventType.push(item.event)
          if(item.class){
            this.listenerType = 'class'
            this.listenerValueLabel = 'java类'
            this.listenerValue = item.class
          }else if(item.expression){
            this.listenerType = 'expression'
            this.listenerValueLabel = '表达式'
            this.listenerValue = item.expression
          }else if(item.delegateExpression){
            this.listenerType = 'delegateExpression'
            this.listenerValueLabel = '代理表达式'
            this.listenerValue = item.delegateExpression
          }else if(item.script){
            this.listenerType = 'script'
            this.listenerValueLabel = '脚本'
            this.listenerValue = item.script
          }
        }
      })
    },
    handleClick(tab, event) {
      console.log(tab, event);
    }
  },
  watch: {
    id (newVal, oldVal) {
      const modeling = this.bpmnModeler.get('modeling');
      modeling.updateProperties(this.element,{'id':newVal});
    },
    name(newVal, oldVal){
      const modeling = this.bpmnModeler.get('modeling');
      modeling.updateProperties(this.element,{'name':newVal});
    },
    candidateStarterUsers(newVal,oldVal){
      const modeling = this.bpmnModeler.get('modeling');
      modeling.updateProperties(this.element,{'activiti:candidateStarterUsers':newVal});
    },
    candidateStarterGroups(newVal,oldVal){
      const modeling = this.bpmnModeler.get('modeling');
      modeling.updateProperties(this.element,{'activiti:candidateStarterGroups':newVal});
    },
    // 监控element值，当发生改变时获取响应的属性
    element: {
      deep: true,
      immediate: true,
      handler(newVal, oldVal) {
        if(newVal) {
          this.id = newVal.businessObject.get('id');
          this.name = newVal.businessObject.get('name');
          // 初始化赋值
          const modeling = this.bpmnModeler.get('modeling');
          modeling.updateProperties(this.element,{'name':this.name});
          modeling.updateProperties(this.element,{'process_namespace':this.process_namespace});
          modeling.updateProperties(this.element,{'process_id':this.id});
        }
      }
    },
    eventType(newVal, oldVal){
      if(newVal) {
        this.isShow = true
      }
    },
    listenerType(newVal, oldVal){
      if(newVal === 'class') {
        this.listenerValueLabel = 'java类'
      } else if(newVal === 'expression') {
        this.listenerValueLabel = '表达式'
      } else if(newVal === 'delegateExpression') {
        this.listenerValueLabel = '代理表达式'
      }else if(newVal === 'script'){
        this.listenerValueLabel = '脚本'
      }
    },
    listenerValue(newVal, oldVal){
      if(newVal) {
        const bpmnFactory = this.bpmnModeler.get('bpmnFactory');
		// const ElementFactory = this.bpmnModeler.get("elementFactory")
		debugger
        let extensionElements = this.element.businessObject.get('extensionElements');
        if(!extensionElements) {
          extensionElements = bpmnFactory.create('bpmn:ExtensionElements', {values : []});
        }
        const length = extensionElements.get('values').length;
        for (let i = 0; i < length; i++) {
          // 清除旧值
          extensionElements.get('values').pop();
        }
        this.eventType.forEach(evt => {
          const executionListener = bpmnFactory.create('activiti:ExecutionListener',{});
          executionListener.$attrs['event'] = evt;
          executionListener.$attrs[this.listenerType] = newVal;
          extensionElements.get('values').push(executionListener)

        });
        const modeling = this.bpmnModeler.get('modeling');
        modeling.updateProperties(this.element, {
          extensionElements: extensionElements
        })
      }
    },
  }
}

</script>
