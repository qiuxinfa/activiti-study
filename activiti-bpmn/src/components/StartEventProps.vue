<template>
    <div>
        <el-form-item label="编号">
            <el-input v-model="id"></el-input>
        </el-form-item>
        <el-form-item label="名称">
            <el-input v-model="name"></el-input>
        </el-form-item>
        <el-form-item label="发起人">
            <el-input v-model="initiator"></el-input>
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
            initiator: ''
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
		initiator (newVal, oldVal) {
			if(!this.bpmnModeler){
				return
			}
		    const modeling = this.bpmnModeler.get('modeling')
		    modeling.updateProperties(this.element,{
		        'activiti:initiator': newVal
		    })
		},
        element: {
            deep: true,
            immediate: true,
            handler(newVal, oldVal) {
                if (newVal != oldVal) {
                   this.id = newVal.businessObject.id || Date.now().toString()
				   this.name = newVal.businessObject.name || ''
				   this.initiator = newVal.businessObject.initiator || ''
                }
            },
            
        },
    }
}
</script>
<style>

</style>