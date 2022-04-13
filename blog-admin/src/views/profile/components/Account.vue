<template>
<div>
    <transition name="el-zoom-in-top">
        <div v-show="showPass">
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" >
                <el-form-item label="用户名" prop="name">
                    <el-input v-model="ruleForm.name"></el-input>
                </el-form-item>
                <el-form-item label="原密码" prop="oldPassword">
                    <el-input type="password" v-model="ruleForm.oldPassword" clearable show-password></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" v-model="ruleForm.newPassword" clearable show-password></el-input>
                </el-form-item>
                <el-form-item label="验证新密码" prop="reNewPassword">
                    <el-input type="password" v-model="ruleForm.reNewPassword" clearable show-password></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('ruleForm')">修改</el-button>
                    <el-button @click="resetForm('ruleForm')">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </transition>

    <transition name="el-zoom-in-top">
        <div v-show="!showPass" class="m-display-flex m-flex-justify-around">
            <ForgetPassword></ForgetPassword>
        </div>
    </transition>
    <el-button type="text" @click="showPass = !showPass" class="m-float-right">{{ showPass ? "忘记密码" : "返回" }}</el-button>
</div>
</template>

<script>
import {
    updatePass
} from "@/api/user";
import ForgetPassword from "@/views/profile/components/Forget";
import {
    mapGetters
} from "vuex";
export default {
    components: {
        ForgetPassword,
    },
    props: {
        user: {
            type: Object,
            default: () => {
                return {
                    name: "",
                    email: "",
                };
            },
        },
        labelWidth: {
            type: String,
            default: "100px",
        },
    },

    computed: {
        ...mapGetters(["name", "userId"]),
    },

    data() {
        const validateRePass = (rule, value, callback) => {
            if (value === "") {
                callback(new Error("请输入密码"));
            }
            if (this.ruleForm.newPassword !== value) {
                callback(new Error("两次密码输入不一致"));
            }
            callback();
        };

        return {
            showPass: true,
            ruleForm: {
                name: "",
                oldPassword: "",
                newPassword: "",
                reNewPassword: "",
            },
            rules: {
                name: [{
                        required: true,
                        message: "请输入用户名",
                        trigger: "blur",
                    },
                    {
                        min: 2,
                        max: 15,
                        message: "长度在 3 到 15 个字符",
                        trigger: "blur",
                    },
                ],

                oldPassword: [{
                        required: true,
                        message: "请输入旧密码",
                        trigger: "blur",
                    },
                    {
                        min: 3,
                        max: 20,
                        message: "长度在 4 到 20 个字符",
                        trigger: "blur",
                    },
                ],

                newPassword: [{
                        required: true,
                        message: "请输入新密码",
                        trigger: "blur",
                    },
                    {
                        min: 3,
                        max: 20,
                        message: "长度在 4 到 20 个字符",
                        trigger: "blur",
                    },
                ],
                reNewPassword: [{
                    validator: validateRePass,
                    trigger: "blur",
                }, ],
            },
        };
    },

    watch: {
        "pass.username"() {
            setTimeout(() => {
                this.showPassTip.show = true;
            }, 1000);
        },
    },

    created() {
        // 初始化用户名
        this.ruleForm.name = this.name;
    },
    methods: {
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.$confirm('确认修改密码?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        let data = {
                            username: this.ruleForm.name,
                            oldPassword: this.ruleForm.oldPassword,
                            newPassword: this.ruleForm.newPassword,
                        };
                        updatePass(data).then((res) => {
                            this.$message.success(res.result);
                            this.logOut()
                        });
                    }).catch(() => {
                        this.$message({
                            type: 'info',
                            message: '已取消修改'
                        });
                    });

                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },

        logOut() {
            setTimeout(() => {
                this.$confirm('密码已修改,需要重新登录！', '提示', {
                    confirmButtonText: '确定',
                    showCancelButton: false,
                    type: 'warning'
                }).then(() => {
                    this.$store.dispatch('user/resetToken')
                    this.$router.push('/login')
                }).catch(() => {
                    this.$store.dispatch('user/resetToken')
                    this.$router.push('/login')
                });
            }, 3000);

        }
    },
};
</script>
