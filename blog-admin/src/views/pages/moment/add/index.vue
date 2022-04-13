<template>
  <div class="app-container">
    <el-button
      class="m-fixed-button"
      type="primary"
      icon="el-icon-plus"
      @click="createData()"
    >
      发布
    </el-button>
    <!-- 内容 -->
    <el-form ref="dataForm" :model="moment" class="form-container">
      <el-col :span="24">
        <el-form-item>
          <div id="vditor-content" />
        </el-form-item>
      </el-col>
    </el-form>

  </div>
</template>

<script>
import { createVditor } from '@/plugins/CreateVditor'
import { createMoment } from '@/api/moment'
export default {
  name: 'MomentAdd',
  data() {
    return {
      contentVditor: '',
      msg: '发布动态',
      moment: {
        content: ''
      }
    }
  },
  mounted() {
    // 加载编辑器
    this.initVditor()
  },

  methods: {
    initVditor() {
      this.contentVditor = createVditor('vditor-content', 500, false)
    },
    createData() {
      this.moment.content = this.contentVditor.getHTML()
      
      if (this.moment.content.length === 0) {
        this.$message.error('发布失败，不能发布一个空动态哦!')
        return
      }
      createMoment(this.moment).then(res => {
        if (res.code === 200) {
          this.$notify.success('发布成功！')
          this.contentVditor.setValue('')
        }
      })
    }
  }
}
</script>

<style scoped>

.add-blog {
  display: flex;
  position: fixed;
  right: 0;
  z-index: 2000;
  margin-right: 20px;
}
</style>
