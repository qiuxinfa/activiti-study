<template>
	<el-container style="height: 100%;">
	  <el-header height="10%">  
        <ul class="buttons">
		   <li>
				<el-upload action="" :before-upload="openBpmn" style="margin-right: 10px; display:inline-block;">
				  <el-tooltip effect="dark" content="加载xml" placement="bottom">
				    <el-button size="mini" icon="el-icon-folder-opened" />
				  </el-tooltip>
				</el-upload>
		   </li>	
           <li>
                <a ref="saveDiagram" href="javascript:" title="保存为bpmn">保存为bpmn</a>
            </li>
            <li>
                <a ref="saveSvg" href="javascript:" title="保存为svg">保存为svg</a>
            </li>
			<li>
			    <el-button size="mini" @click="saveToDb">保存到数据库</el-button>
			</li>
        </ul>
	  </el-header>
	  <el-container style="height: 680px;">
		<el-aside width="80%" style="padding: 0;">
			 <div ref="canvas" style="width: 100%;height: 100%"></div>
		</el-aside>
		<el-main v-if="bpmnModeler && currentElement" style="background-color: #f0f2f5">
			<el-form label-width="120px" size="mini" label-position="left">
				<component :is="currentComponet" :bpmnModeler="bpmnModeler" :element="currentElement" :key="currentElement.id"></component>
			</el-form>
		</el-main>
	  </el-container>
	</el-container>
</template>

<script>
// 引入相关的依赖
import BpmnModeler from 'bpmn-js/lib/Modeler'
import { xmlStr } from '../mock/xmlStr'
import activitiDescriptor from '../config/activiti.json'
import customTranslate from '../js/customTranslate/customTranslate'
import customControlsModule from '../js/customControls'
import ProcessProps from './ProcessProps.vue'
import StartEventProps from './StartEventProps.vue'
import UserTaskProps from './UserTaskProps.vue'
import SequenceFlowProps from './SequenceFlowProps.vue'
import EndEventProps from './EndEventProps.vue'
import {saveModelXml,getEditorXml} from '../api/process.js'

export default {
  name: 'ProcessDesigner',
  components: {
	  ProcessProps,
	  StartEventProps,
      UserTaskProps,
	  SequenceFlowProps,
	  EndEventProps
  },
  // 生命周期 - 创建完成（可以访问当前this实例）
  created() {
	  this.modelId = this.$route.query.modelId
  },
  // 生命周期 - 载入后, Vue 实例挂载到实际的 DOM 操作完成，一般在该过程进行 Ajax 交互
  mounted() {
    this.init()
  },
  data() {
    return {
	  modelId: '',
      // bpmn建模器
      bpmnModeler: null,
	  currentComponet: 'UserTaskProps',
	  currentElement: null,
      container: null,
      canvas: null,
      myProps:{
		'bpmn:Process': 'ProcessProps',
		'bpmn:StartEvent': 'StartEventProps',
		'bpmn:UserTask': 'UserTaskProps',
		'bpmn:SequenceFlow': 'SequenceFlowProps',
		'bpmn:EndEvent': 'EndEventProps'
	  }
    }
  },
  // 方法集合
  methods: {
	openBpmn(file) {
	  const reader = new FileReader()
	  reader.readAsText(file, 'utf-8')
	  reader.onload = () => {
		this.bpmnModeler.importXML(reader.result, err => {
		  if (err) {
		    console.error('加载xml出错了')
		  } else {
		    // 这里是成功之后的回调, 可以在这里做一系列事情
		    this.success()
		  }
		})
	  }
	  return false
	},  
    init() {
      // 获取到属性ref为“canvas”的dom节点
      const canvas = this.$refs.canvas
	  var customTranslateModule = {
	      translate: [ 'value', customTranslate ]
	  };
      // 建模
      this.bpmnModeler = new BpmnModeler({
        container: canvas,
        additionalModules: [customTranslateModule,customControlsModule],
        moddleExtensions: {
          activiti: activitiDescriptor
        }
      })
	  if(this.modelId){
		  this.loadXml()
	  }else{
		  this.createDefaultDiagram()
	  }
      
    },
	// 从后台加载xml
	loadXml(){
		getEditorXml(this.modelId).then(res => {
			debugger
			// 这里可以请求后台查询流程信息，转换为流程图显示
			this.bpmnModeler.importXML(res.data, err => {
				debugger
			  if (err) {
			    this.createDefaultDiagram()
			  } else {
			    // 这里是成功之后的回调, 可以在这里做一系列事情
			    this.success()
			  }
			})
		}).catch(e => {
			// 使用默认的xml
			debugger
			this.createDefaultDiagram()
		})
	},
	// 使用默认的xml
    createDefaultDiagram() { 
	  // 初始化画布
	  this.bpmnModeler.createDiagram()
	  // 绑定监听
	  this.success()
    },
    success() {
      this.addBpmnListener()
      this.addModelerListener()
      this.addEventBusListener()
    },
    addModelerListener() {
      // 监听 modeler
      const bpmnjs = this.bpmnModeler
      const that = this
      const events = ['shape.added', 'shape.move.end', 'shape.removed']
      events.forEach(function(event) {
        that.bpmnModeler.on(event, e => {
          var elementRegistry = bpmnjs.get('elementRegistry')
          var shape = e.element ? elementRegistry.get(e.element.id) : e.shape
          if (event === 'shape.added') {
            console.log('新增了shape')
			if(that.myProps[e.element.type]){
			  that.currentComponet = that.myProps[e.element.type]
			  that.currentElement = e.element
			}
          } else if (event === 'shape.move.end') {
            console.log('移动了shape')
			if(that.myProps[e.element.type]){
			  that.currentComponet = that.myProps[e.element.type]
			  that.currentElement = e.element
			}
          } else if (event === 'shape.removed') {
            console.log('删除了shape')
			// 显示流程属性
			that.currentComponet = 'ProcessProps'
			// 流程的element
			const allElement = elementRegistry._elements || {}
			for(let k in allElement){
			  if(allElement[k].element.type == 'bpmn:Process'){
			    that.currentElement = allElement[k].element
			  }
			}
          }
        })
      })
    },
    addEventBusListener() {
      // 监听 element
      let that = this
      const eventBus = this.bpmnModeler.get('eventBus')
      const modeling = this.bpmnModeler.get('modeling')
      const elementRegistry = this.bpmnModeler.get('elementRegistry')
      const eventTypes = ['element.click', 'element.changed', 'selection.changed']
      eventTypes.forEach(function(eventType) {
        eventBus.on(eventType, function(e) {
		  // debugger
          console.log(eventType)
          if (!e || !e.element) {
            console.log('无效的e', e)
            return
          }
          if (eventType === 'element.changed') {
			console.log('改变了element', e)
            if(that.myProps[e.element.type]){
              that.currentComponet = that.myProps[e.element.type]
              that.currentElement = e.element
            }
          } else if (eventType === 'element.click') {
            console.log('点击了element', e)
			if(that.myProps[e.element.type]){
			  that.currentComponet = that.myProps[e.element.type]
			  that.currentElement = e.element
			}
          }
        })
      })
    },
	elementChanged(e) {
	  debugger
	  if(e.element.type == "bpmn:UserTask"){
		  this.currentElement = e.element
	  }else{
		  this.currentElement = null;
	  }
	},
	// 添加绑定事件
	addBpmnListener () {
	  const that = this
	  // 获取a标签dom节点
	  const downloadLink = this.$refs.saveDiagram
	  const downloadSvgLink = this.$refs.saveSvg
	    // 给图绑定事件，当图有发生改变就会触发这个事件
	  this.bpmnModeler.on('commandStack.changed', function () {
	    that.saveSVG(function(err, svg) {
	        that.setEncoded(downloadSvgLink, 'diagram.svg', err ? null : svg)
	    })
	    that.saveDiagram(function(err, xml) {
	        that.setEncoded(downloadLink, 'diagram.bpmn', err ? null : xml)
	    })
	  })
	},
	// 保存到数据库
	saveToDb(){
		this.bpmnModeler.saveXML({format:true},(err,xml) => {
		    if(err){
		      console.error('流程数据生成失败')
		      return
		    }
		    this.bpmnModeler.saveSVG((err, svg) => {
		      if(err){
		        console.error('流程数据生成失败！')
		        return
		      }
			  const data = {
				  modelId: this.modelId,
		          bpmnXml: xml,
		          svgXml: svg
		        }
			  saveModelXml(data)
		      .then(res => {
		       console.log('保存流程图成功')
		      })
		      .catch(e => {
		        console.log('保存流程图失败')
		      })
		    })
		})
	},
	// 下载为SVG格式,done是个函数，调用的时候传入的
	saveSVG(done) {
	  // 把传入的done再传给bpmn原型的saveSVG函数调用
	  this.bpmnModeler.saveSVG(done)
	},
	// 下载为bpmn格式,done是个函数，调用的时候传入的
	saveDiagram(done) {
		this.bpmnModeler.saveXML({format:true},(err,xml) => {
			done(err, xml)
		})
	},
	 // 当图发生改变的时候会调用这个函数，这个data就是图的xml
	setEncoded(link, name, data) {
	  // 把xml转换为URI，下载要用到的
	  const encodedData = encodeURIComponent(data)
	  // 下载图的具体操作,改变a的属性，className令a标签可点击，href令能下载，download是下载的文件的名字
	  // console.log(link, name, data)
	  let xmlFile = new File([data], 'test.bpmn')
	  // console.log(xmlFile)
	  if (link && data) {
	    link.className = 'active'
	    link.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData
	    link.download = name
	  }
	}
	
  },
  // 计算属性
  computed: {}
}
</script>

<style scoped>
.buttons {
    position: absolute;
    left: 20px;
    bottom: 20px;
}
.buttons li {
    display: inline-block;
    margin: 5px;
}
.buttons li a {
    color: #999;
    background: #eee;
    cursor: not-allowed;
    padding: 8px;
    border: 1px solid #ccc;
    text-decoration: none;
}
.buttons li a.active {
    color: #333;
    background: #fff;
    cursor: pointer;
}
</style>
