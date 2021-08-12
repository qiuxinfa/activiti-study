<template>
  <div id="app">
    <h1>流程管理</h1>
    <el-button class="addBtn" icon="el-icon-plus" @click="addProcessModel">新增</el-button>
    <el-table
      :data="tableData"
      style="width: 100%;">
      <el-table-column
        label="流程标识"
        prop="id"
        width="180">
      </el-table-column>
      <el-table-column
        label="流程名称"
        prop="name"
        width="180">
      </el-table-column>
      <el-table-column
        label="上次更新时间"
        prop="lastUpdateTime"
        width="180">
      </el-table-column>
      <el-table-column
        label="版本"
        prop="version"
        width="180">
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button
            size="mini"
            @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          <el-button
            size="mini"
            @click="handleDeploy(scope.$index, scope.row)">部署</el-button>  
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="新增模型"
      :visible.sync="modelVisible"
      width="50%"
      :before-close="handleModelClose">
      <el-form>
        <el-row :gutter="20">

          <el-col :span="6">
            <el-form-item label="标识">
              <el-input v-model="key"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="名称">
              <el-input v-model="name"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="类别">
              <el-input v-model="category"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleModelClose">取 消</el-button>
        <el-button type="primary" @click="handleModelOk">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import {addModel,getModels,deleteModel,deployModel} from '@/api/process.js'

export default {
  name: 'ModelList',
  data () {
    return {
      tableData: [],
      modelVisible: false,
      key: '',
      name: '',
      category: '',
      descp: ''
    }
  },

  mounted() {
	  this.getModelList()
  }, 

  methods: {
	getModelList(){
		getModels().then(res => {
			this.tableData = res.data || []
		}).catch(e => {
			this.$message("查询失败")
		})
	},
    addProcessModel() {
      this.modelVisible = true
    },
    handleModelClose() {
      this.modelVisible = false
    },
    handleModelOk() {
      const that = this
	  const data = {
	    key: that.key,
	    name: that.name,
	    category: that.category
	  }
      that.modelVisible = false
      addModel(data)
      .then(function(res) {
        that.key = ''
        that.name = ''
        that.category = ''
        that.descp = ''
        that.$message("新增成功")
		that.getModelList()
      })
      .catch(function(err) {
        that.$message("新增失败")
      });
    },
    async handleEdit (index, row) {
		this.$router.push({
			path:'/designer',
			query:{
				modelId: row.id
			}
		})
    },
	
    handleDelete(index, row) {
      const that = this
      deleteModel(row.id)
      .then(function(res) {
        that.$message("删除成功")
		that.getModelList()
      })
      .catch(function(err) {
        that.$message("删除失败")
      })
    },

    handleDeploy(index, row) {
      const that = this;
      deployModel(row.id)
      .then(function(res) {
        if(res.data === 'success') {
          that.$message("部署成功")
        } else {
          that.$message(res.data)
        }
        
      })
      .catch(function(err) {
        that.$message("部署失败")
      })
    },

    handleVisiable(visiable) {
      this.dialogVisible = visiable
    },
    handleViewModelVisiable(visiable) {
      this.dialogViewModelVisible = visiable
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.addBtn {
  display: flex;
  text-align:left;
  margin-left: 50px;
}
</style>
