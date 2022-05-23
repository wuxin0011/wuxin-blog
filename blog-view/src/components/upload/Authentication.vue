<template>
<div>
    <el-row>
        <el-alert type="warning" title="输入仓库token获取配置"></el-alert>
    </el-row>
    <el-row>
        <el-input v-model="user.token" @keyup.enter.native="getUser">
            <el-select v-model="tokenType" slot="prepend" style="width: 100px;" @change="changeType">
                <el-option value="github" label="github"></el-option>
                <el-option value="gitee" label="gitee"></el-option>
            </el-select>
            <el-button slot="append" icon="el-icon-search" @click="getUser"></el-button>
        </el-input>
    </el-row>
    <el-row>
        <span class="middle m-margin-lr">当前用户</span>
        <el-avatar :src="user.avatar" class="middle m-margin-lr">user</el-avatar>
        <el-tooltip content="点击前往仓库" :disabled="!user.username">
            <el-link target="_blank" :href="user.url" :underline="false" :disabled="!user.username" class="middle m-margin-lr">{{ user.username ? user.username : '未配置' }}
            </el-link>
        </el-tooltip>
    </el-row>
    <el-row>
        <el-button type="info" size="small" icon="el-icon-close" @click="removeUser">清除配置</el-button>
        <el-button type="primary" size="small" icon="el-icon-check" :disabled="isDisabled" @click="saveUser">保存配置 </el-button>
    </el-row>
</div>
</template>

<script>
export default {
    name: "Authentication",
    data() {
        return {
            user: {
                token: '',
                username: '',
                url: '',
                avatar: '',
            },
            tokenType: "github",

        };
    },

    computed: {
        // 保存token类型
        type() {
            return this.$store.getters["upload/tokenType"];
        },

        loginUser() {
            return this.$store.getters["upload/user"];
        },

        isDisabled() {
            return !(this.user && this.user.username && this.user.token && this.user.url && this.user.avatar)
        }
    },

    mounted() {
        this.tokenType = this.type;

        this.changeType();

    },

    methods: {
        getUser() {
            const data = {
                tokenType: this.tokenType,
                token: this.user.token,
            };
            this.$store.dispatch("upload/getInfo", data).then((res) => {
                this.user = res;
                // this.saveUser()
            });
        },

        changeType() {
            this.$store.dispatch("upload/getUser", this.tokenType).then((res) => {
                this.user = res;
            });
        },

        saveUser() {
            this.$store.dispatch("upload/saveUser", {
                user: this.user,
                tokenType: this.tokenType,
            });
        },

        removeUser() {
            this.$store.dispatch("upload/removeUser", this.tokenType);
            this.user = {};
        },

    },
};
</script>

<style scoped>
.el-row {
    margin: 20px !important;
}

.middle {
    vertical-align: middle !important;
}
</style>
