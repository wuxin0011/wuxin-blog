<template>
<div class="app-container">
    <el-row :gutter="20">
        <el-col :span="12" :xs="24">
            <!-- <BasicSetting :system-info="systemInfo" /> -->
            <MyCard title="站点设置">
                <el-form :model="systemInfo" label-width="100px" size="small">
                    <el-form-item label="图标" prop="adminIcon">
                        <CustomUploadAvatar :image="systemInfo.adminIcon" @updateImage="updateAdminIcon" />
                        <el-input v-model="systemInfo.adminIcon" prefix-icon="el-icon-s-opportunity" />
                    </el-form-item>
                    <el-form-item label="二维码图片">
                        <CustomUploadAvatar :image="systemInfo.erCode" @updateImage="updateErCodeImage" />
                        <el-input v-model="systemInfo.erCode" prefix-icon="el-icon-s-home" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="small" @click.prevent="updateData">确认</el-button>
                    </el-form-item>
                </el-form>
            </MyCard>
        </el-col>
        <el-col :span="12" :xs="24">
            <!-- <OtherSetting :system-info="systemInfo" /> -->
            <MyCard title="其他设置">
                <el-form :model="systemInfo" label-width="100px" size="small">
                    <el-form-item label="名称">
                        <el-input v-model="systemInfo.webName" prefix-icon="el-icon-s-opportunity" />
                    </el-form-item>
                    <el-form-item label="网址">
                        <el-input v-model="systemInfo.webUrl" prefix-icon="el-icon-s-home" />
                    </el-form-item>
                    <el-form-item label="登录地址">
                        <el-input v-model="systemInfo.loginUrl" prefix-icon="el-icon-s-platform" />
                    </el-form-item>
                    <el-form-item label="评论标签名">
                        <el-input v-model="systemInfo.commentLabelName" prefix-icon="el-icon-s-opportunity" style="width: 200px;" />
                        <el-select v-model="systemInfo.commentLabelColor" placeholder="评论标签颜色" style="width: 100px;" class="m-margin-left-small">
                            <el-option v-for="(color,index) in colors" :key="index" :value="color" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="域名备案">
                        <el-input v-model="systemInfo.webRecord" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="small" @click.prevent="updateData">确认</el-button>
                    </el-form-item>
                </el-form>
            </MyCard>
        </el-col>
    </el-row>
    <WebFooter :footer-label="label" />
</div>
</template>

<script>
import {
    colors
} from '@/utils/color'
import WebFooter from '@/views/system/web/footer/index.vue'
import {
    updateMySystemInfo
} from "@/api/system";
import CustomUploadAvatar from '@/components/MyComponents/CustomUploadAvatar'
import MyCard from "@/components/MyComponents/MyCard";
import {
    getMySystemInfo
} from '@/api/system'

export default {
    name: 'MyWeb',
    components: {
        CustomUploadAvatar,
        WebFooter,
        MyCard
    },
    data() {
        return {
            systemInfo: {},
            label: [],
            colors: colors,
            rules: {
                typeName: [{
                    message: '请填写类型名',
                    trigger: 'blur'
                }],
                typeColor: [{
                    message: '请填类型颜色',
                    trigger: 'blur'
                }],
                name: [{
                    message: '请填写名称',
                    trigger: 'blur'
                }],
                color: [{
                    message: '颜色不能为空！',
                    trigger: 'blur'
                }],

                url: [{
                    message: '请地址不能为空！',
                    trigger: 'blur'
                }],
                title: [{
                    message: '介绍不能为空',
                    trigger: 'blur'
                }],

            },
        }
    },
    mounted() {
        this.getData()
    },
    methods: {

        getData() {
            getMySystemInfo().then(res => {
                if (res.code === 200) {
                    const {
                        label,
                        system
                    } = res
                    this.label = label
                    this.systemInfo = system
                }
            })
        },

        updateErCodeImage(url) {
            this.systemInfo.erCode = url
            this.updateData()
        },
        updateAdminIcon(url) {
            this.systemInfo.adminIcon = url
            this.updateData()
        },
        updateData() {
            updateMySystemInfo(this.systemInfo).then(res => {
                if (res.code === 200) {
                    this.$message.success('修改成功！')
                }
            })
        },

        deleteItem(id) {
            this.$emit('deleteItem', id)
        },

        updateItem(data) {
            this.$emit('updateItem', data)
        },

        cancel() {
            this.$notify.warning('取消操作了哦')
        },

        submit(data) {
            this.$refs.dataForm.validate(valid => {

                if (valid) {

                    if (data.typeName.trim() !== "" &&
                        data.typeColor !== "" &&
                        data.name.trim() !== "" &&
                        data.color.trim() !== "" &&
                        data.url.trim() !== "" &&
                        data.title.trim() !== ""
                    ) {

                    } else {
                        this.$message.error("添加失败,标签内容不能为空！")
                    }
                    this.updateItem(data)
                }
            })
        }
    }
}
</script>

<style scoped>

</style>
