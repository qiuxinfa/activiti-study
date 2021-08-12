<template>
    <div>
        <el-form-item label="编号">
            <el-input v-model="id"></el-input>
        </el-form-item>
        <el-form-item label="名称">
            <el-input v-model="name"></el-input>
        </el-form-item>
        <el-form-item label="跳过条件">
            <el-input v-model="conditionExpression"></el-input>
        </el-form-item>
    </div>
</template>
<script>

export default {
    props: ['bpmnModeler','element'],
    data(){
        return {
            id : '',
            name: '',
            conditionExpression: ''
        }
    },

    watch: {
        id (newVal, oldVal) {
            if(!this.bpmnModeler){
				return
			}
            const modeling = this.bpmnModeler.get('modeling')
            modeling.updateProperties(this.element,{
                id: newVal
            })
        },
        name (newVal, oldVal) {
			if(!this.bpmnModeler){
				return
			}
            const modeling = this.bpmnModeler.get('modeling')
            modeling.updateProperties(this.element,{
                name: newVal
            })
        },
		conditionExpression (newVal, oldVal){
		  if(newVal) {
            const newCondition = this.bpmnModeler.get('moddle').create('bpmn:FormalExpression', { body: newVal })
			debugger
			const modeling = this.bpmnModeler.get('modeling');
			modeling.updateProperties(this.element, {
			  conditionExpression: newCondition
			})
		  }
		},
        element: {
            deep: true,
            immediate: true,
            handler(newVal, oldVal) {
                if (newVal != oldVal) {
                   this.id = newVal.businessObject.id || Date.now().toString()
				   this.name = newVal.businessObject.name || ''
				   // debugger
				   if(newVal.businessObject.conditionExpression && newVal.businessObject.conditionExpression.body){
					   this.conditionExpression = newVal.businessObject.conditionExpression.body.body || ''
				   }
                }
            },
            
        },
    }
}
</script>
<style>

</style>