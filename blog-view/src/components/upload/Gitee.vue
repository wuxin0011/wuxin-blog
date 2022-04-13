<template>
<div>
    <el-row>
        <el-col>
            <el-alert type="warning" title="上传图片至Gitee" description="图片内容只缓存到本地浏览器中，不会经过服务器缓存！"></el-alert>
        </el-col>
    </el-row>
    <el-row type="flex" justify="space-around">
        <el-col :span="3">
            <el-avatar :src="user.avatar" :size="40"></el-avatar>
        </el-col>
        <el-col :span="5">
            <el-tooltip content="点击前往仓库">
                <el-link target="_blank" :href="user.url" :underline="false">{{ user.username }}</el-link>
            </el-tooltip>
        </el-col>
        <el-col :span="6">
            <el-tooltip content="请选择仓库！" :disabled="repoName">
                <el-button size="small" type="primary" icon="el-icon-upload" :disabled="!repoName" @click="open = true">上传图片</el-button>
            </el-tooltip>
        </el-col>
        <el-col :span="6">
            <el-button size="small" type="primary" icon="el-icon-upload" @click="showLocal">本地图片</el-button>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="22">
            <el-select class="w-100-pre" v-model="repoName" placeholder="请选择要上传的仓库..." @change="resetRepos">
                <el-option v-for="(item, index) in repoList" :key="`${repoName}${index}`" :value="item.name" :label="item.name" size="small" />
            </el-select>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="20">
            <el-tooltip content="请选择仓库！" placement="top" :disabled="!repoName">
                <el-cascader size="small" class="w-100-pre" :key="showResource" :disabled="!repoName" :props="props" v-model="activePath" :options="root" placeholder="请选择要上传的文件夹路径..." />
            </el-tooltip>
        </el-col>
        <el-col :span="4">
            <el-tooltip content="点击前往仓库" :disabled="!repoName">
                <el-button size="small" type="primary" icon="el-icon-search" :disabled="!repoName" @click="searchRepoImg"></el-button>
            </el-tooltip>
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
        <el-col :span="10">
            <el-tooltip content="如果上传文件为图片，会将图片添加到本地" placement="top">
                <el-switch active-text="添加到本地壁纸" v-model="isAddLocal"></el-switch>
            </el-tooltip>
        </el-col>
        <el-col :span="10">
            <el-tooltip content="指定文件上传路径，如果路径不存在，创建并上传" placement="top">
                <el-switch active-text="自定义上传" v-model="isCustomPath" :disabled="!repoName"></el-switch>
            </el-tooltip>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="22">
            <el-input placeholder="自定义上传路径..." :disabled="!isCustomPath" v-model="customPath"></el-input>
        </el-col>
    </el-row>
    <sui-modal v-model="open" size="small">
        <div class="ui three top attached buttons">
            <button class="ui upload button" @click="showType=1">
                <i class="upload icon"></i>
                上传图片
            </button>
            <button class="ui picture button" @click="showType=2">
                <i class="picture icon"></i>
                本地图片
            </button>
            <button class="ui images button" @click="getUserImageList">
                <i class="images icon"></i>
                仓库图片
            </button>
        </div>

        <sui-modal-content scrolling align="center" v-if="showType===1">
            <div class="ui warning message">
                <i class="ui icon close"></i>
                文件上传路径
                <a class="ui icon" target="_blank" :href="`https://gitee.com/${user.username}/${repoName}/tree/${branch}/${activePath.join('/')}`">
                    <i class="folder open icon"></i>
                    {{ path === "/" ? "根目录" : path }}
                </a>
            </div>
            <el-upload style="width: 100%" ref="uploadRef" action="" :http-request="upload" drag multiple :file-list="uploadList" list-type="picture" :auto-upload="false">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
        </sui-modal-content>
        <sui-modal-content scrolling align="center" v-if="showType===2">
            <h4 class="ui horizontal divider header">
                本地缓存 共计<span style="color:skyblue;">{{imgList.length}}</span>张
            </h4>

            <sui-image-group size="medium">
                <a v-for="(img,index) in imgList" :key="index" :href="img" target="_blank">
                    <sui-image wrapped size="medium" :src="img" />
                </a>
            </sui-image-group>

        </sui-modal-content>
        <sui-modal-content scrolling align="center" v-if="showType===3">
            <h4 class="ui horizontal divider header">
                仓库图片,共计<span style="color:skyblue;">{{repoImgList.length}}</span>张
            </h4>
            <sui-image-group size="medium" v-if="repoImgList">
                <a v-for="(item,index) in repoImgList" :key="index" :href="item.download_url" target="_blank">
                    <sui-image wrapped size="medium" :src="item.download_url" />
                </a>
            </sui-image-group>
            <div class="ui header" v-else>
                空
            </div>

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
} from "@/api/gitee";

import {
    setSetStore,
    getStore,
    removeStore
} from "@/utils/session.js";

import {
    randomUUID,
    isImgExt
} from "@/utils/upload.js";
import {
    mapGetters
} from "vuex";

export default {
    name: "GithubUp",
    data() {
        return {
            user: {}, // 用户信息
            showType: 1, // 展示类型
            imgList: [], // 本地图片列表
            repoName: "", // 选择仓库
            branch: 'master', // 分支 默认master
            imageShow: false,
            repoList: [], // 用户仓库列表
            activePath: [""], // 选中文件夹
            showResource: 0,
            root: [{
                label: "根目录",
                value: "",
            }, ],
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
            isCustomPath: false, // 是否自定义路径
            isCustomUrl: false, // 自定义路径地址
            customPath: "",
            open: false,
            uploadList: [], //
            isAddLocal: true, // 是否添加到本地,
            repoImgList: []
        };
    },
    created() {
        console.log(isImgExt('hello.jpg'))
        this.init();
        console.log(getStore("imgList"));
    },
    computed: {
        // 真实路径
        path() {
            if (this.isCustomPath) {
                return `/${this.user.username}/${this.repoName}/${this.customPath}`;
            }
            return `/${this.user.username}/${this.repoName}${this.activePath.join( '/' )}`;
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
                    this.initImgList()
                });
            } else {
                this.$message.error("加载失败！获取不到用户配置信息，请初始化配置！");
                setTimeout(() => {
                    this.$emit("error");
                }, 3000);
            }
        },

        initImgList() {

            let arr = getStore("imgList");
            if (arr && arr.length !== 0) {
                this.imgList = arr;
            } else {
                this.imgList = [];
            }
        },

        resetRepos() {
            this.root = [{
                label: "根目录",
                value: "",
            }, ];
            this.activePath = [""];
            this.showResource++;
        },

        getUserRepos() {
            getUserRepos(this.user.token).then((res) => {
                this.repoList = res;
            });
        },

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

        submitUpload() {
            this.uploadList = this.$refs.uploadRef.uploadFiles;
            if (this.uploadList.length) {
                // element-ui upload 组件 触发 submit函数 获取上传图片信息
                this.$refs.uploadRef.submit();
            } else {
                this.$message.error("请先选择文件");
            }
        },

        upload(data) {
            let reader = new FileReader();
            reader.readAsDataURL(data.file);
            reader.onload = () => {
                let base64 = reader.result.split(",")[1];
                let fileName = data.file.name;
                fileName = randomUUID() + fileName.substr(fileName.lastIndexOf("."));
                let path = this.activePath.join("/");
                // 文件是否指定文件路径
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

                this.uploadGitee(data, fileName, base64, path);
            };
        },

        uploadGitee(data, fileName, base64, path) {
            // 上传私人仓库需要token
            let bodyData = {
                message: `上传文件${fileName}`,
                content: base64,
                access_token: this.user.token,
            };

            // 延迟上传
            setTimeout(() => {
                upload(
                    this.user.username,
                    this.repoName,
                    path,
                    fileName,
                    bodyData
                ).then((res) => {
                    this.$message.success("上传成功");
                    const url = res.content.download_url;
                    // 询问是否上传保存到本地相册 暂时不用上传到服务器
                    if (isAddLocal) {
                        setTimeout(() => {
                            this.saveImageLocal(url);
                        }, 500);
                    }
                    data.onSuccess();
                });
            }, 1000);
        },

        /**
         * 保存图片到本地
         */
        saveImageLocal(url) {
            this.$confirm("是否将图片保存到本地?", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning",
                })
                .then(() => {
                    // 本地是否缓存有图片
                    let imgList = getStore("imgList") && getStore("imgList").length !== 0 ? getStore("imgList") : [];
                    imgList.push(url);
                    // 将图片存储到本地
                    setSetStore("imgList", imgList);
                    this.$message({
                        type: "success",
                        message: "保存成功!",
                    });
                    // 重新加载图片
                    this.initImgList()
                })
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: "已取消保存！",
                    });
                });
        },

        /**
         * 获取用户仓库图片信息
         */
        getUserImageList() {
            this.showType = 3
            this.repoImgList = []
            getReposContents(this.user.username, this.repoName, this.activePath.join('/')).then(
                (res) => {
                    res.forEach((file) => {
                        // 获取仓库图片  
                        if (file.type === "file" && isImgExt(file.name)) {
                            this.repoImgList.push(file)
                        }
                    });
                }
            );
        },

        // 搜索仓库图片
        searchRepoImg() {
            this.open = true
            this.getUserImageList()
        },

        showLocal() {
            this.open = true
            this.showType = 2
            this.initImgList()
        }
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
