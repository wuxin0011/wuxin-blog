<template>
<div class="app-container">
    <el-button class="m-fixed-button" type="primary" icon="el-icon-plus" @click="handleCreate"> 发布 </el-button>
    <el-row :gutter="20">
        <el-col :span="20" :xs="24">
            <el-switch v-model="blog.publish" active-text="发布" />
            <el-switch v-model="blog.commentEnabled" active-text="评论" class="m-margin-left-small" />
            <el-switch v-model="blog.appreciation" active-text="赞赏" class="m-margin-left-small" />
            <el-switch v-model="blog.top" active-text="置顶" :active-value="1" :inactive-value="0" />
            <el-switch v-model="blog.secrecy" active-text="保密" />
            <el-switch v-model="isCache" active-text="开启本地缓存" @change="updateCahce" />
            <template v-if="blog.secrecy">
                <el-input v-model="blog.password" style="width:300px;" placeholder="请输入密码" show-word-limit />
            </template>

        </el-col>
    </el-row>

    <el-row :gutter="20">
        <el-col :span="5" :xs="20">
            <el-select v-model="blog.cid" style="width: 100%" size="small" placeholder="请选择类型，输入可创建" filterable allow-create>
                <el-option v-for="item in categoryList" :key="item.cid" :label="item.name" :value="item.cid" />
            </el-select>
        </el-col>
        <el-col :span="8" :xs="20">
            <el-select v-model="blog.tagIds" style="width: 100%" multiple filterable allow-create default-first-option placeholder="请选择文章标签">
                <el-option v-for="item in tagList" :key="item.tagId" :label="item.name" :value="item.tagId">
                </el-option>
            </el-select>
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="12" :xs="24">
            <el-input v-model="blog.title" :maxlength="100" placeholder="请输入标题" clearable show-word-limit />
        </el-col>
        <el-col :span="12" :xs="24">
            <el-input v-model="blog.imgUrl" placeholder="封面..." clearable />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col>
            <div id="vditor-description" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col>
            <div id="vditor-content" />
        </el-col>
    </el-row>
</div>
</template>

<script>
import {
    createBlog
} from "@/api/blog";
import {
    blogMinix
} from "../minix";
export default {
    name: "BlogAdd",
    mixins: [blogMinix],
    methods: {

        // 验证数据
        handleCreate() {

            if (this.blog.tagIds.length === 0 || this.blog.tagIds.length > 4) {
                this.$message.error('标签数量在1-4个')
                return
            }

            if (!this.blog.cid) {
                this.$message.error('分类不能为空！')
                return
            }

            if (this.blog.secrecy && this.blog.password.length < 4 || this.blog.password.length > 15) {
                this.$message.error('添加失败,密码长度在3-15')
                return
            }

            // 检查tag是否存在，不存在自动创建
            this.blog.tagIds.filter((id) => {
                this.validTag(id)
            });

            // 检查category是否存在，不存在自动创建
            this.validCategory(this.blog.cid)

            // 获取markdown文本内容
            this.blog.description = this.descriptionVditor.getHTML()
            this.blog.content = this.contentVditor.getHTML()
            if (!this.blog.description) {
                this.$message.error('文章简介不能为空！')
                return
            }
            if (!this.blog.content) {
                this.$message.error('文章内容不能为空！')
                return
            }

            setTimeout(() => {
                this.blog.tagIds = this.ids

                // 这里要延时执行，否则创建标签可能没有添加进去
                setTimeout(() => {
                    createBlog(this.blog).then(res => {
                        this.$router.push('/blog/list')
                    })
                }, 500);
            }, 1000);
        },

    },
};
</script>

<style scoped>
.el-switch {
    margin: 0 4px !important;
}

.el-row {
    margin: 10px 0 !important;
}
</style>
