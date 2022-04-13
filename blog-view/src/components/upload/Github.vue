<template>
<div>
    <el-row>
        <el-col>
            <el-alert type="warning"  description="该功能目前仅支持上传图片保存到本地，不会上传至服务器"></el-alert>
        </el-col>
    </el-row>
    <el-row type="flex" justify="space-around">
        <el-col :span="4">
            <el-avatar :src="user.avatar" :size="40"></el-avatar>
        </el-col>
        <el-col :span="4">
            <el-tooltip content="点击前往仓库">
                <el-link target="_blank" :href="user.url" :underline="false">{{ user.username }}</el-link>
            </el-tooltip>
        </el-col>
        <el-col :span="4">
            <el-tooltip content="请指定仓库路径" :disabled="repoName">
                <el-button @click="open = true" :disabled="!repoName" plain icon="el-icon-upload" size="small">开始上传</el-button>
            </el-tooltip>
        </el-col>
        <el-col :span="4">
            <el-button type="primary" size="small">本地图片</el-button>
        </el-col>
    </el-row>
    <el-row type="flex" justify="space-between">
        <el-col :span="12">
            <el-select class="w-100-pre" v-model="repoName" placeholder="请选择要上传的仓库..." @change="resetRepos">
                <el-option v-for="(item, index) in repoList" :key="`${repoName}${index}`" :value="item.name" :label="item.name" />
            </el-select>
        </el-col>
        <el-col :span="8">
            <el-tooltip content="默认分支为main,如果需要上传到其他分支,请指定！" placement="top">
                <el-input v-model="branch" placeholder="分支"></el-input>
            </el-tooltip>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="18">
            <el-tooltip content="请选择仓库！" placement="top" :disabled="!repoName">
                <el-cascader class="w-100-pre" :key="showResource" :disabled="!repoName" :props="props" v-model="activePath" :options="root" placeholder="请选择要上传的文件夹路径..." />
            </el-tooltip>
        </el-col>
        <el-col :span="4">
            <el-button type="prmary" icon="el-icon-search"></el-button>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="22">
            <span v-if="repoName">当前仓库 :
                <el-tag type="info" size="small" class="m-margin-lr">{{
            repoName
          }}</el-tag></span>
            <span v-if="path">当前路径:
                <el-tag type="info" size="small" class="m-margin-lr">{{
            path === "/" ? "根目录" : path
          }}</el-tag></span>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">
            <el-tooltip content="如果上传文件为图片，会将图片作为壁纸添加到本地" placement="top">
                <el-switch active-text="添加到本地壁纸" disabled v-model="isAddLocal"></el-switch>
            </el-tooltip>
        </el-col>
        <el-col :span="8">
            <el-tooltip content="指定文件上传路径，如果路径不存在，则创建并上传" placement="top">
                <el-switch active-text="自定义上传" v-model="isCustomPath"></el-switch>
            </el-tooltip>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="22">
            <el-input placeholder="自定义上传路径..." :disabled="!isCustomPath" v-model="customPath"></el-input>
        </el-col>
    </el-row>
    <el-row>
    </el-row>

    <sui-modal v-model="open" size="small" style="min-height: 500px">
        <sui-modal-content scrolling align="center">
            <div class="ui warning message">
                <i class="ui icon close"></i>
                <p>
                    文件会上传到仓库
                    <a class="ui label">
                        <i class="folder open icon"></i>
                        {{ repoName }}{{ path === "/" ? "根目录" : path }}
                    </a>
                </p>
            </div>
            <el-upload style="width: 100%" ref="uploadRef" action="" :http-request="upload" drag multiple :file-list="uploadList" list-type="picture" :auto-upload="false">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
        </sui-modal-content>
        <sui-modal-actions>
            <el-button size="small" type="primary" icon="el-icon-upload" @click="submitUpload">确定上传</el-button>
            <sui-button @click.native="open = false"> 关闭 </sui-button>
        </sui-modal-actions>
    </sui-modal>
</div>
</template>

<script>
import {
    getReposContents,
    getUserRepos,
    upload
} from "@/api/github";

import {
    randomUUID
} from "@/utils/upload.js";
import {
    mapGetters
} from "vuex";

export default {
    name: "GithubUp",
    data() {
        return {
            user: {},
            branch: 'main',
            repoName: "",
            repoList: [],
            activePath: [""],
            showResource: 0,
            root: [{
                label: "根目录",
                value: "",
            }, ],
            activeName: [],
            props: {
                checkStrictly: true,
                lazy: true,
                lazyLoad: async (node, resolve) => {
                    const list = [];
                    const path = node.path.join("/");
                    await this.getReposContents(list, path);
                    resolve(list);
                },
            },
            isCustomPath: false,
            isCustomUrl: false,
            customPath: "",
            open: false,
            uploadList: [],
            isAddLocal: true, // 是否添加到本地
        };
    },
    created() {
        this.init();
    },
    computed: {
        // 真实路径
        path() {
            if (this.isCustomPath) {
                return `/${this.user.username}/${this.repoName}/${this.customPath}`;
            }
            return `/${this.user.username}/${this.repoName}${this.activePath.join( "/" )}`;
        },

        ...mapGetters("upload", ["loginUser"]),
    },
    methods: {
        init() {
            this.user = this.loginUser;
            if (
                this.user &&
                this.user.username &&
                this.user.token &&
                this.user.avatar
            ) {
                this.$nextTick(() => {
                    this.getUserRepos();
                });
            } else {
                this.$message.error("加载失败！获取不到用户配置信息，请初始化配置！");
                setTimeout(() => {
                    this.$emit("error");
                }, 3000);
            }
        },

        resetRepos() {
            this.root = [{
                label: "根目录",
                value: ""
            }, ];
            this.activePath = [""];
            this.showResource++;
        },

        // 获取仓库列表
        getUserRepos() {
            getUserRepos(this.user.username).then((res) => {

                this.repoList = res;
            });
        },

        // 获取仓库路径文件信息
        async getReposContents(list, path) {
            await getReposContents(this.user.username, this.repoName, path).then(
                (res) => {
                    res.forEach((file) => {
                        // 加载目录
                        if (file.type === "dir") {
                            list.push({
                                label: file.name,
                                value: file.name,
                                leaf: false,
                            });
                        }
                    });
                }
            );
        },

        // 图片使用jsdelivr=>CDN加速访问
        accessUrl(filePath) {
            return `https://cdn.jsdelivr.net/gh/${this.user.username}/${this.repoName}${filePath}`;
        },

        submitUpload() {
            this.uploadList = this.$refs.uploadRef.uploadFiles;
            if (this.uploadList.length) {
                this.$refs.uploadRef.submit();
            } else {
                this.$message.error("请先选择文件");
            }
        },

        upload(data) {
            let fileReader = new FileReader();
            fileReader.readAsDataURL(data.file);
            fileReader.onload = () => {
                let base64 = fileReader.result.split(",")[1];
                let fileName = data.file.name;
                fileName = randomUUID() + fileName.substr(fileName.lastIndexOf("."));
                this.uploadGithub(data, fileName, base64);
            };
        },

        // 文件上传
        uploadGithub(data, fileName, base64) {
            // 文件内容
            let formData = {
                message: `上传文件${fileName}`,
                content: base64,
                branch: this.branch
            };
            let path = this.activePath.join('/')
            console.log(this.user.username, this.repoName, path, fileName)

            // if (this.isCustomPath) {
            //     if (this.customPath === '/') {
            //         path = ''
            //     } else {
            //         path = this.customPath
            //         if (path.charAt(0) !== '/') {
            //             path = '/' + path
            //         }
            //         if (path.charAt(path.length - 1) === '/') {
            //             path = path.substring(0, path.length - 1)
            //         }
            //     }
            // }

            // 延迟上传
            setTimeout(() => {
                upload(this.user.username, this.repoName, path, fileName, formData).then(() => {
                    this.$message.success("上传成功");
                    const imgUrl = path + "/" + fileName;
                    const accessUrl = this.accessUrl(imgUrl);

                    // todo 这里处理文件成功上传之后 判断是否将图片文件添加到本地
                    //  上传成功回调
                    data.onSuccess();
                });
            }, 1000);
        },
    },
};
</script>

<style scoped>
.el-row {
    margin: 20px !important;
}

.w-100-pre {
    width: 100% !important;
}
</style>
