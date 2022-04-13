<template>
<div class="app-container">
    <el-row :gutter="20">
        <el-col :span="4">
            <el-select v-model="type" class="m-margin-left-small" @change="updateUploadType">
                <el-option label="github" :value="1" />
                <el-option label="gitee" :value="2" />
            </el-select>
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="12" :xs="24">
            <MyCard title="系统文件上传图床配置">
                <!-- <el-alert class="m-margin-tb-mini" type="warning" description="用户名为仓库账号名,仓库为仓库名称，文件夹为该仓库下的文件夹,token用于访问 仓库, " :closable="false" show-icon /> -->
                <el-form ref="dataForm" size="small" style="width:500px;" :model="repo" class="m-margin-tb-small">
                    <el-form-item label="用户名">
                        <el-input v-model="repo.username" prefix-icon="el-icon-s-custom" placeholder="仓库用户名 ..." />
                    </el-form-item>
                    <el-form-item label="仓库">
                        <el-input v-model="repo.repository" prefix-icon="el-icon-s-shop" placeholder="仓库名 ..." />
                    </el-form-item>
                    <el-form-item label="分支">
                        <el-input v-model="repo.master" prefix-icon="el-icon-share" placeholder="提交的分支..." />
                    </el-form-item>
                    <el-form-item label="文件夹">
                        <el-input v-model="repo.folder" prefix-icon="el-icon-folder-opened" placeholder="提交的文件夹..." />
                    </el-form-item>
                    <el-form-item label="token">
                        <el-input v-model="repo.accessToken" prefix-icon="el-icon-s-check" placeholder="ghp_xxxxxxxxxxxxx" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="updateSetting">确认</el-button>
                    </el-form-item>
                </el-form>
                <el-link type="primary" :underline="false" :href="type===1?'https://docs.github.com/en/rest/reference/repos':'https://gitee.com/api/v5/swagger#/getV5ReposOwnerRepohttps://gitee.com/api/v5/swagger#/getV5ReposOwnerRepo'" target="_blank" class="m-float-right">api文档
                </el-link>
            </MyCard>
        </el-col>
        <el-col :span="12" :xs=24>
            <MyCard title="操作">
                <el-alert class="m-margin-tb-mini" type="warning" description=" 该功能用于测试使用 删除文件需要谨慎操作！" :closable="false" show-icon />
                <el-form ref="githubOperation" size="small" class="m-margin-tb-small" :model="github" :rules="rules">
                    <el-form-item label="地址" required prop="url">
                        <el-input v-model="info.url" placeholder="地址..." />
                    </el-form-item>
                    <el-form-item label="sha" required prop="sha">
                        <el-input v-model="info.sha" placeholder="sha..." />
                    </el-form-item>
                    <el-form-item label="提交消息" required prop="message">
                        <el-input v-model="info.message" placeholder="写点什么..." />
                    </el-form-item>
                    <el-form-item>
                        <el-button size="small" type="danger" plain @click.prevent="handleDelete">删除</el-button>
                    </el-form-item>
                </el-form>
            </MyCard>
        </el-col>
    </el-row>
</div>
</template>

<script>
import {
    getUploadType,
    updateUploadType,
    updateRepoSetting,
    getRepoSettingInfo,
    deleteGithubRecords
} from '@/api/system'
import MyCard from '@/components/MyComponents/MyCard'
import {
    validateUrl
} from '@/utils/validate'

export default {
    components: {
        MyCard
    },
    data() {
        return {
            type: 1,
            repo: {
                username: '',
                repository: '',
                master: '',
                folder: '',
                accessToken: '',
                type: 1
            },
            info: {
                url: '',
                sha: '',
                message: ''
            },
            rules: {
                url: [{
                    validator: validateUrl,
                    trigger: 'blur'
                }],
                sha: [{
                    required: true,
                    message: 'sha不能为空！',
                    trigger: 'blur'
                }],
                message: [{
                    required: true,
                    message: '提交消息不能为空！',
                    trigger: 'blur'
                }]
            },
            response: {}
        }
    },
    created() {
        this.getUploadType()
    },
    methods: {
        getUploadType() {
            getUploadType().then(res => {
                if (res.code === 200) {
                    this.type = res.result
                    this.getData()
                }
            })
        },
        updateUploadType() {
            updateUploadType(this.type).then(res => {
                if (res.code === 200) {
                    this.$message.success('修改成功！')
                    this.getData()
                }
            })
        },

        updateSetting() {
            updateRepoSetting(this.repo).then(res => {
                if (res.code === 200) {
                    this.$message.success('修改成功！')
                }
            })
        },

        getData() {
            getRepoSettingInfo(this.type).then(res => {
                if (res.code === 200) {
                    this.repo = res.result
                }
            })
        },
        handleDelete() {
            this.$refs['githubOperation'].validate(valid => {
                if (valid) {
                    this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        deleteGithubRecords(this.info).then(res => {
                            if (res.code === 200) {
                                this.response = res.result
                            }
                        })
                    }).catch(() => {
                        this.$message({
                            type: 'info',
                            message: '已取消删除'
                        })
                    })
                }
            })
        }

    }
}
</script>

<style scoped>
.el-row {
    margin: 10px !important;
}
</style>
