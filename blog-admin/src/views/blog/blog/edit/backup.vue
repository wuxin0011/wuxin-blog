<template>
<div class="app-container">

    <!-- 发布 -->
    <el-button class="m-fixed-button" type="primary" icon="el-icon-edit" size="small" @click="handleUpdate">
        修改
    </el-button>
    <el-form ref="updateDataForm" :rules="rules" :model="blog" label-position="left" label-width="60px">
        <el-row>
            <el-col :span="12" :xs="24">
                <el-form-item label="权限">
                    <el-switch v-model="blog.publish" active-text="发布" class="m-margin-left-small" />
                    <el-switch v-model="blog.commentEnabled" active-text="评论" class="m-margin-left-small" />
                    <el-switch v-model="blog.top" active-text="置顶" :active-value="1" :inactive-value="0" class="m-margin-left-small" />
                    <el-switch v-model="blog.appreciation" active-text="赞赏" class="m-margin-left-small" />
                    <el-switch v-model="blog.secrecy" active-text="密码" class="m-margin-left-small" />
                </el-form-item>
            </el-col>
            <el-col v-if="blog.secrecy" :span="8" :xs="24">
                <el-form-item label="密码">
                    <el-input v-model="blog.password" :type="isShowPassword?'text':'password'" placeholder="请输入密码" :maxlength="15" style="width: 80%;" show-word-limit />
                    <el-button icon="el-icon-view" type="text" @click="showPassword(blog.userId)" />
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8" :xs="24">
                <el-form-item label="分类" required>
                    <el-select v-model="blog.cid" style="width: 80%;" placeholder="请选择...">
                        <el-option v-for="item in categoryList" :key="item.cid" :label="item.name" :value="item.cid" />
                    </el-select>
                    <el-button type="primary" size="small" class="m-margin-left-small" icon="el-icon-plus" @click="addCategory" />
                </el-form-item>
            </el-col>
            <el-col :span="14" :xs="24">
                <el-form-item label="标签" prop="tagIds" required>
                    <el-drag-select style="width: 80%;" v-model="blog.tagIds" filterable multiple placeholder="请选择，输入关键字搜索">
                        <el-option v-for="item in tagList" :key="item.tagId" :label="item.name" :value="item.tagId" />
                    </el-drag-select>
                    <el-button type="primary" size="small" class="m-margin-left-small" icon="el-icon-plus" @click="addTag" />
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12" :xs="24">
                <el-form-item label="标题" required>
                    <el-input v-model="blog.title" :maxlength="30" show-word-limit clearable />
                </el-form-item>
            </el-col>
            <el-col :span="12" :xs="24">
                <el-form-item label="封面">
                    <el-input show-word-limit clearable />
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <div id="vditor-description" />
        </el-row>
        <el-row>
            <div id="vditor-content" />
        </el-row>

    </el-form>
    <el-dialog title="添加分类" :visible.sync="addCategoryDialogFormVisible" width="30%">
        <CustomLabel @cancel="addCategoryDialogFormVisible=false" @addCategorySuccess="getLabelList" />
    </el-dialog>

    <el-dialog title="添加标签" :visible.sync="addTagDialogFormVisible" width="30%">
        <CustomLabel label-name="tag" @cancel="addTagDialogFormVisible=false" @addTagSuccess="getLabelList" />
    </el-dialog>
</div>
</template>

<script>
import {
    blogDetail,
    updateBlog
} from '@/api/blog'
import {
    blogMinix
} from '../minix'
import CustomLabel from '@/components/MyComponents/CustomLabel'

export default {
    name: 'BlogEdit',
    components: {
        CustomLabel
    },
    mixins: [blogMinix],
    data() {
        return {
            blog: {
                tagIds: []
            }
        }
    },
    mounted() {
        this.getData()
        this.$message.success('' + this.$route.params)
    },
    methods: {
        handleUpdate() {
            // 第一次验证
            this.$refs['updateDataForm'].validate(valid => {
                if (valid) {
                    if (this.blog.secrecy && this.blog.password.length > 4 && this.blog.password.length < 15) {
                        this.$message.error('密码长度 4-15')
                        return
                    }
                    this.blog.description = this.descriptionVditor.getHTML()
                    this.blog.content = this.contentVditor.getHTML()
                    // 去重处理
                    this.blog.tagIds = this.uniqueList(this.blog.tagIds)
                    updateBlog(this.blog).then(res => {
                        this.$notify.success('修改成功！')
                    })
                }
            })
        },
        // 获取tag
        getData() {
            blogDetail(this.$route.params.blogId).then(res => {
                if (res.code === 200) {
                    const {
                        description,
                        content,
                        tags,
                        tagIds
                    } = res.result
                    this.blog = res.result

                    // 遍历获取文章标签
                    for (let i = 0; i < this.blog.tags.length; i++) {
                        this.blog.tagIds.push(tags[i].tagId)
                    }
                    // 去重处理
                    this.blog.tagIds = this.uniqueList(this.blog.tagIds)

                    // 将html格式语言转换markdown
                    this.blog.description = this.descriptionVditor.html2md(description)
                    this.blog.content = this.contentVditor.html2md(content)

                    // markdown格式文件渲染到编辑器中
                    this.descriptionVditor.setValue(this.blog.description)
                    this.contentVditor.setValue(this.blog.content)
                }
            })
        },

        // list去重
        uniqueList(arr) {
            var newArr = []
            for (var i = 0; i < arr.length; i++) {
                if (newArr.indexOf(arr[i]) === -1) {
                    newArr.push(arr[i])
                }
            }
            return newArr
        }

    }
}
</script>
