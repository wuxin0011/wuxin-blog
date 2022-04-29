<template>
<div class="app-container">
    <el-button class="m-fixed-button" type="primary" icon="el-icon-plus" @click="createData()">
        发布
    </el-button>
    <el-switch v-model="isCache" active-text="开启本地缓存" @change="updateCahce" />
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
import {
    createVditor
} from '@/plugins/CreateVditor'
import {
    createMoment
} from '@/api/moment'
export default {
    name: 'MomentAdd',
    data() {
        return {
            contentVditor: '',
            isCache: false,
            msg: '发布动态',
            moment: {
                content: ''
            }
        }
    },
    mounted() {
        let isCache = window.localStorage.getItem('wuxin_moment_iscache')
        if (isCache === true || isCache === 'true') {
            this.isCache = true
        }
        // 加载编辑器
        this.initVditor()
    },

    methods: {

        updateCahce() {
            window.localStorage.setItem('wuxin_moment_iscache', this.isCache)
            if (this.isCache) {
                this.$message.success('缓存开启成功！')
                return;
            }
            this.$message.success('缓存关闭成功！')

        },
        initVditor() {
            let that = this
            this.contentVditor = createVditor('vditor-content', 500, that.isCache)
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
