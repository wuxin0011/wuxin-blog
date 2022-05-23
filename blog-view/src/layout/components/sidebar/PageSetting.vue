<template>
<div class="setting">
    <el-button size="mini" type="primary" icon="el-icon-setting" @click.prevent="drawer = !drawer">
    </el-button>
    <el-drawer title="页面配置" :visible.sync="drawer" :modal="false" :with-header="false" size="40%" style="min-width:400px;">
        <el-card>
            <h3 slot="header">页面配置</h3>
            <h4 class="ui horizontal divider header">
                <i class="trademark icon"></i>
                导航栏
            </h4>
            <el-row>
                <el-col :span="8" :xs="24">
                    <el-switch size="small" v-model="settingState.inverted" class="m-margin-small" active-text="反转" />
                </el-col>
                <el-col :span="12" :xs="24">
                    <el-select size="small" v-model="settingState.menuColor" placeholder="请选择" class="m-margin-small">
                        <el-option v-for="(item, index) in colors" :key="`${item}${index}`" :label="item" :value="item"> <span style="float: left">{{ item !== "default" ? item : "默认" }}</span>
                            <span style="float: right; width: 60px; height: 24px" :style="{ background: item !== 'default' ? item : '' }"></span>
                        </el-option>
                    </el-select>
                </el-col>
            </el-row>
            <h4 class="ui horizontal divider header">
                <i class="paint small brush icon"></i>
                模式
            </h4>
            <el-row>
                <el-col :span="8" :xs="24">
                    <el-tooltip content="开启夜间模式前请关闭背景图🙂" :disabled="!settingState.background.isShowImage">
                        <i class="m-setting-icon" :class=" settingState.nightMode ? 'el-icon-sunny' : 'el-icon-moon' " @click="updateNightMode" style="cursor: pointer; font-size: 30px"></i>
                    </el-tooltip>
                </el-col>
                <el-col :span="6" :xs="24">
                    <el-tooltip content="请先关闭 夜间模式 和 背景图 自定义背景才会生效" :disabled=" !settingState.background.isShowImage && !settingState.nightMode ">
                        <el-color-picker size="small" v-model="settingState.background.color" show-alpha />
                    </el-tooltip>
                </el-col>
                <el-col :span="10" :xs="24" class="m-mobile-hide">
                    <el-switch size="small" v-model="settingState.focusMode" active-text="侧边栏" />
                </el-col>
            </el-row>
            <h4 class="ui horizontal divider header">
                <i class="images icon"></i>
                背景
            </h4>
            <el-row>
                <el-col :span="8" :xs="24">
                    <el-switch size="small" v-model="settingState.background.isShowImage" active-text="开启" class="m-margin-small" />
                </el-col>
                <el-col :span="12" :xs="24">
                    <el-tooltip content="请打开背景图开关" :disabled="settingState.background.isShowImage">
                        <el-select size="small" v-model="settingState.background.image" placeholder="请选择背景图" class="m-margin-small">
                            <el-option v-for="(item, index) in backgroundImageList" :key="`image${index}`" :label="`${ item === 'default' ? '无(默认)' : '背景图' + index }`" :value="item"> </el-option>
                        </el-select>
                    </el-tooltip>
                </el-col>
                <el-col :span="3" :xs="24">
                    <el-button size="small" type="text" @click="dialogVisible = true">自定义背景</el-button>
                </el-col>
            </el-row>
        </el-card>
    </el-drawer>

    <el-drawer :visible.sync="dialogVisible" :modal="false" size="40%">
        <component :is="componentName" @error="componentName = 'Authentication'"></component>
        <el-button type="text" @click="loadCompontentName" class="m-float-r m-margin-lr">查看仓库</el-button>
    </el-drawer>
</div>
</template>

<script>
import {
    colors,
    backgroundImageList
} from "@/utils/setting";
import {
    setSetStore,
    getStore,
    removeStore
} from "@/utils/session";
import {
    mapGetters
} from "vuex";

import {
    TOKEN_GITHUB_KEY
} from "@/store/mutations-type.js";
import Authentication from "@/components/upload/Authentication.vue";
import Github from "@/components/upload/Github.vue";
import Gitee from "@/components/upload/Gitee.vue";

export default {
    name: "PageSetting",
    data() {
        return {
            drawer: false,
            dialogVisible: false,
            colors: colors,
            backgroundImageList: backgroundImageList,
            visible: true,
            user: {
                token: "",
                username: "",
                avatar: "",
            },
            isSave: false,
            componentName: "Authentication",
        };
    },
    components: {
        Authentication,
        Gitee,
        Github,
    },
    computed: {
        ...mapGetters(["settingState"]),
        ...mapGetters('upload', ['tokenType']),
    },
    created() {
        const user = getStore(TOKEN_GITHUB_KEY);
        if (user && user.token && user.username && user.avatar) {
            this.user = user;
        } else {
            this.isSave = true;
        }
    },
    methods: {
        updateNightMode() {
            let nightMode = this.settingState.nightMode;
            this.settingState.nightMode = !nightMode;
            if (this.settingState.nightMode) {
                // 如果开启了夜间模式，导航栏需要开启夜间颜色反转
                this.settingState.inverted = true;
            } else {
                this.settingState.inverted = false;
            }
        },

        loadCompontentName() {
            if (this.componentName === "Authentication") {
                if (this.tokenType === 'github') {
                    this.componentName = "Github";
                } else {
                    this.componentName = "Gitee";
                }
            } else {
                this.componentName = "Authentication";
            }
        },

        // handleClose() {
        //     this.$confirm('确认关闭？')
        //         .then(_ => {
        //             this.timer = setTimeout(() => {
        //                 done();
        //                 // 动画关闭需要一定的时间
        //                 setTimeout(() => {}, 400);
        //             }, 2000);
        //         })
        //         .catch(_ => {});
        // },
    },
};
</script>

<style scoped>
.setting {
    position: fixed;
    top: 300px;
    right: 0;
    z-index: 1000;
}

/* .el-drawer {
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
    text-align: center;
} */

.el-row {
    margin-top: 20px !important;
    margin-left: 20px !important;
}

.el-form {
    width: 100% !important;
    height: 100% !important;
    /*border: skyblue 10px solid;*/
}

.m-setting-icon {
    cursor: pointer !important;
    font-size: 30px !important;
}

/* .el-drawer .rtl{
    width:40% !important;
    min-width: 400px !important;
} */
</style>
