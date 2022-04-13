<template>
<div>
    <el-row class="step_container m-margin-tb-large">
        <el-col>
            <el-steps finish-status="success">
                <el-step title="步骤 1" :description="description.first" :status="step.first" icon="el-icon-message" />
                <el-step title="步骤 2" :description="description.second" :status="step.second" icon="el-icon-s-check" />
                <el-step title="步骤 3" :description="description.third" :status="step.third" icon="el-icon-lock" />
            </el-steps>
            <el-button style="margin-top: 12px" :disabled="isDisabled" @click="next">下一步</el-button>
        </el-col>
    </el-row>

    <el-form ref="passDataForm" class="container_el_form el-form" size="small" :inline="true" :model="dataForm" :rules="dataRules">
        <el-row>
            <el-col>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="dataForm.email" placeholder="请输入邮箱" clearable />
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                    <el-input v-model="dataForm.code" placeholder="请输入验证码" clearable :disabled="disableCode" />
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col>
                <el-form-item label="新密码" prop="newPass">
                    <el-input v-model="dataForm.newPass" placeholder="请输入密码" clearable type="password" :disabled="disableCode" />
                </el-form-item>
                <el-form-item label="重新输入" prop="reNewPass">
                    <el-input v-model="dataForm.reNewPass" placeholder="请重新输入密码" clearable type="password" :disabled="disableCode" />
                </el-form-item>
            </el-col>
        </el-row>

    </el-form>

</div>
</template>

<script>
import {
    updatePassByEmail,
    validUserEmail
} from "@/api/user";
import {
    validEmail
} from "@/utils/validate";
export default {
    name: "ForgetPassword",
    data() {
        const validateEmail = (rule, value, callback) => {
            if (!validEmail(value)) {
                callback(new Error("邮箱格式输入错误"));
            }
            callback();
        };

        const validateCode = (rule, value, callback) => {
            if (value.length !== 6) {
                callback(new Error("验证码是6为数字哦！"));
            }
            callback();
        };

        const validatePass = (rule, value, callback) => {
            if (value !== this.newPass) {
                callback(new Error("两次密码输入不一致哦"));
            }
            this.active = 5;
            callback();
        };
        return {
            dataForm: {
                email: "",
                code: "",
                password: "",
                rePassword: "",
            },
            dataRules: {
                email: [{
                    required: true,
                    validate: validateEmail,
                    trigger: "blur",
                }, ],
                code: [{
                    required: true,
                    validate: validateCode,
                    trigger: "blur",
                }, ],
                newPass: [{
                        required: true,
                        message: "请输入密码",
                        trigger: "blur",
                    },
                    {
                        min: 4,
                        max: 15,
                        message: "长度在 4 到 15 个字符",
                        trigger: "blur",
                    },
                ],
                reNewPass: [{
                    required: true,
                    validate: validatePass,
                    trigger: "blur",
                }, ],
            },

            active: 0,
            isDisabled: true,
            disableCode: true,

            step: {
                first: "process",
                second: "wait",
                third: "wait",
            },

            description: {
                first: "请输入邮箱",
                second: "校验验证码",
                third: "密码修改",
            },
        };
    },

    watch: {
        // 检查邮箱格式是否正确
        "dataForm.email"(newValue) {
            if (validEmail(newValue)) {
                this.changeStep('process', 'wait', 'wait', 1, false, false)
                this.changeDescription('邮箱格式正确，请获取验证码...', '校验验证码', '密码修改')
            } else {
                this.changeStep('error', 'wait', 'wait', 0, true, true)
                this.changeDescription('邮箱校验不通过,请重新检查邮箱！', '校验验证码', '密码修改')
            }
        },
    },

    methods: {
        next() {
            if (this.active === 0) {
                this.$message.error('邮箱校验不通过')
                return;
            }
            if (this.active === 1) {
                this.getEmailCode();
                return;
            }
            if (this.active === 2) {
                this.submit();
                return;
            }

        },

        // 1、第1步， 获取邮箱验证码
        getEmailCode() {
            this.description.second = "验证码获取中...";
            validUserEmail(this.dataForm.email).then((res) => {
                this.$message.success("验证码成功发送到你的邮箱！");
                this.changeStep('success', 'process', 'wait', 2, false, false)
                this.changeDescription('验证码成功发送到你的邮箱！有效时间为10分钟', '填写验证码', '校验验证码，修改密码')
            }).catch(() => {
                this.changeStep('error', 'wait', 'wait', 1, false, true)
                this.changeDescription('邮箱验证失败！请重新填写邮箱获取验证码！', '填写验证码', '校验验证码，修改密码')
            });
        },

        //第2步， 输入验证码，密码，修改密码
        submit() {
            this.$refs.passDataForm.validate((valid) => {
                if (valid) {
                    updatePassByEmail({
                        email: this.dataForm.email,
                        code: this.dataForm.code,
                        password: this.dataForm.reNewPass,
                    }).then((res) => {
                        this.$message.success(res.result)
                        this.changeStep('success', 'success', 'success', 2, false, false)
                        this.changeDescription('验证码成功发送到你的邮箱！', '验证码校验通过', '密码修改成功')
                        this.logOut()
                    }).catch(() => {
                        this.changeStep('success', 'error', 'error', 1, false, true)
                        this.changeDescription('获取验证码！', '验证码校验失败，重新获取验证码！', '密码修改失败')
                    })
                }
            });
        },

        // 修改步骤信息
        changeStep(first, second, third, active, isDisabled, disableCode) {
            this.step.first = first;
            this.step.second = second;
            this.step.third = third;
            this.active = active;
            this.isDisabled = isDisabled;
            this.disableCode = disableCode
        },

        // 修改提示信息
        changeDescription(d1, d2, d3) {
            this.description.first = d1
            this.description.second = d2
            this.description.third = d3
        },

        // 密码修改后需要重新登录
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

<style scoped>
.step_container {
    width: 600px;
}

.container_el_form {
    position: relative;
    margin-top: 50px;
}
</style>
