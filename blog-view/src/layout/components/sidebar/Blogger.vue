<template>
<div>
    <div class="ui segments m-box">
        <div class="ui card">
            <div class="image">
                <img :src="info.avatar">
            </div>
            <div class="content" align="center">
                <div class="ui header">{{ info.nickname }}</div>
                <div id="rollText" class="m-margin-top" v-if="info.motto.length!=0">{{info.motto}}</div>
            </div>
            <div class="extra content" align="center">
                <!-- gitee -->
                <a :href="chatUrl.gitee" target="_blank" v-if="chatUrl.gitee">
                    <svg t="1638696494899" class="m-user-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4875">
                        <path d="M512 1024C230.4 1024 0 793.6 0 512S230.4 0 512 0s512 230.4 512 512-230.4 512-512 512z m259.2-569.6H480c-12.8 0-25.6 12.8-25.6 25.6v64c0 12.8 12.8 25.6 25.6 25.6h176c12.8 0 25.6 12.8 25.6 25.6v12.8c0 41.6-35.2 76.8-76.8 76.8h-240c-12.8 0-25.6-12.8-25.6-25.6V416c0-41.6 35.2-76.8 76.8-76.8h355.2c12.8 0 25.6-12.8 25.6-25.6v-64c0-12.8-12.8-25.6-25.6-25.6H416c-105.6 0-188.8 86.4-188.8 188.8V768c0 12.8 12.8 25.6 25.6 25.6h374.4c92.8 0 169.6-76.8 169.6-169.6v-144c0-12.8-12.8-25.6-25.6-25.6z" fill="#d81e06" p-id="4876"></path>
                    </svg>
                </a>

                <!-- github -->
                <a :href="chatUrl.github" target="_blank" v-if="chatUrl.github">
                    <svg t="1638696528738" class="m-user-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5780">
                        <path d="M511.6 76.3C264.3 76.2 64 276.4 64 523.5 64 718.9 189.3 885 363.8 946c23.5 5.9 19.9-10.8 19.9-22.2v-77.5c-135.7 15.9-141.2-73.9-150.3-88.9C215 726 171.5 718 184.5 703c30.9-15.9 62.4 4 98.9 57.9 26.4 39.1 77.9 32.5 104 26 5.7-23.5 17.9-44.5 34.7-60.8-140.6-25.2-199.2-111-199.2-213 0-49.5 16.3-95 48.3-131.7-20.4-60.5 1.9-112.3 4.9-120 58.1-5.2 118.5 41.6 123.2 45.3 33-8.9 70.7-13.6 112.9-13.6 42.4 0 80.2 4.9 113.5 13.9 11.3-8.6 67.3-48.8 121.3-43.9 2.9 7.7 24.7 58.3 5.5 118 32.4 36.8 48.9 82.7 48.9 132.3 0 102.2-59 188.1-200 212.9 23.5 23.2 38.1 55.4 38.1 91v112.5c0.8 9 0 17.9 15 17.9 177.1-59.7 304.6-227 304.6-424.1 0-247.2-200.4-447.3-447.5-447.3z" p-id="5781"></path>
                    </svg>
                </a>

                <!-- bilibili -->
                <a :href="chatUrl.bilibili" target="_blank">
                    <svg t="1638696900932" class="m-user-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10564">
                        <path d="M306.005333 117.632L444.330667 256h135.296l138.368-138.325333a42.666667 42.666667 0 0 1 60.373333 60.373333L700.330667 256H789.333333A149.333333 149.333333 0 0 1 938.666667 405.333333v341.333334a149.333333 149.333333 0 0 1-149.333334 149.333333h-554.666666A149.333333 149.333333 0 0 1 85.333333 746.666667v-341.333334A149.333333 149.333333 0 0 1 234.666667 256h88.96L245.632 177.962667a42.666667 42.666667 0 0 1 60.373333-60.373334zM789.333333 341.333333h-554.666666a64 64 0 0 0-63.701334 57.856L170.666667 405.333333v341.333334a64 64 0 0 0 57.856 63.701333L234.666667 810.666667h554.666666a64 64 0 0 0 63.701334-57.856L853.333333 746.666667v-341.333334A64 64 0 0 0 789.333333 341.333333zM341.333333 469.333333a42.666667 42.666667 0 0 1 42.666667 42.666667v85.333333a42.666667 42.666667 0 0 1-85.333333 0v-85.333333a42.666667 42.666667 0 0 1 42.666666-42.666667z m341.333334 0a42.666667 42.666667 0 0 1 42.666666 42.666667v85.333333a42.666667 42.666667 0 0 1-85.333333 0v-85.333333a42.666667 42.666667 0 0 1 42.666667-42.666667z" p-id="10565" fill="#d4237a"></path>
                    </svg>
                </a>

                <!-- qq -->
                <el-popover placement="bottom" width="180" trigger="hover" style="margin: 2em auto">
                    <div class="ui blue basic label" style="width: 100%">
                        <div class="image">
                            <img :src="chatUrl.qq" class="ui rounded bordered image" style="width: 100%" alt="">
                            <div style="font-size: 12px;text-align: center;margin-top: 5px;"> 扫一扫加我qq</div>
                        </div>
                    </div>
                    <svg t="1638696749097" slot="reference" class="m-user-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9906">
                        <path d="M824.8 613.2c-16-51.4-34.4-94.6-62.7-165.3C766.5 262.2 689.3 112 511.5 112 331.7 112 256.2 265.2 261 447.9c-28.4 70.8-46.7 113.7-62.7 165.3-34 109.5-23 154.8-14.6 155.8 18 2.2 70.1-82.4 70.1-82.4 0 49 25.2 112.9 79.8 159-26.4 8.1-85.7 29.9-71.6 53.8 11.4 19.3 196.2 12.3 249.5 6.3 53.3 6 238.1 13 249.5-6.3 14.1-23.8-45.3-45.7-71.6-53.8 54.6-46.2 79.8-110.1 79.8-159 0 0 52.1 84.6 70.1 82.4 8.5-1.1 19.5-46.4-14.5-155.8z" p-id="9907" fill="#1296db"></path>
                    </svg>
                </el-popover>

                <!-- wechat -->
                <el-popover placement="bottom" width="180" trigger="hover" style="margin: 2em auto">
                    <div class="ui olive basic label" style="width: 100%">
                        <div class="image">
                            <img :src="chatUrl.wechat" class="ui rounded bordered image" style="width: 100%" alt="">
                            <div style="font-size: 12px;text-align: center;margin-top: 5px;">扫一扫加我微信</div>
                        </div>
                    </div>
                    <svg slot="reference" t="1638696674178" class="m-user-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8966">
                        <path d="M695.296 346.112c11.776 0 23.552 1.024 34.816 2.048-31.232-146.432-187.904-254.976-366.592-254.976-199.68 0-363.52 136.192-363.52 308.736 0 99.84 54.272 181.76 145.408 245.248l-36.352 109.056L236.032 692.736c45.568 9.216 81.92 18.432 127.488 18.432 11.264 0 22.528-0.512 33.792-1.536-7.168-24.064-11.264-49.664-11.264-76.288 0.512-158.208 136.704-287.232 309.248-287.232zM497.664 240.64c31.232 0 56.32 25.088 56.32 56.32s-25.088 56.32-56.32 56.32-56.32-25.088-56.32-56.32 25.088-56.32 56.32-56.32zM243.2 353.792c-31.232 0-56.32-25.088-56.32-56.32s25.088-56.32 56.32-56.32 56.32 25.088 56.32 56.32-25.088 56.32-56.32 56.32zM1024.512 630.784c0-145.408-145.408-263.68-308.736-263.68-173.056 0-309.248 118.272-309.248 263.68s136.192 263.68 309.248 263.68c36.352 0 72.704-9.216 109.056-18.432l99.84 54.784-27.136-90.624c72.704-54.784 126.976-127.488 126.976-209.408z m-403.456-40.96c-22.016 0-39.936-17.92-39.936-39.936s17.92-39.936 39.936-39.936 39.936 17.92 39.936 39.936-17.92 39.936-39.936 39.936z m199.68 2.56c-22.016 0-39.936-17.92-39.936-39.936s17.92-39.936 39.936-39.936 39.936 17.92 39.936 39.936-17.92 39.936-39.936 39.936z" fill="#69BB64" p-id="8967"></path>
                    </svg>
                </el-popover>
            </div>
            <div>
                <el-collapse accordion>
                    <el-collapse-item :title="item.title" :name="index" v-for="(item,index) in hobby" :key="index">
                        <div>{{ item.content }}</div>
                    </el-collapse-item>
                </el-collapse>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import {
    getAdminUserInfo
} from "@/api/home.js";
export default {
    name: "Blogger",
    data() {
        return {
            chatUrl: {},
            info: {},
            hobby: [],
        };
    },
    created() {
        this.getAdminUserInfo();
    },
    watch: {
        'info.motto'() {
            if (this.info.motto.length != 0) {
                //等待 id="rollText"的div加载完毕
                this.$nextTick(() => {
                    this.rollText()
                })
            }
        }
    },
    methods: {
        getAdminUserInfo() {
            getAdminUserInfo().then((res) => {
                if (res.code === 200) {
                    
                    const {
                        info,
                        chatUrl,
                        hobby
                    } = res;
                    this.chatUrl = chatUrl;
                    this.info = info;
                    this.hobby = hobby;
                    // this.$store.commit('index/' + SET_ADMIN_USER, res)
                }
            });
        },

        rollText() {
            // let r = document.getElementById('rollText')
            // let l = ""
            // const motto = this.info.motto
            // 
            
            // let o = motto.map(function (r) {
            //     return r + ""
            // })
            // let a = 2
            // let g = 1
            // let s = 5
            // let d = 75
            // let b = ["rgb(110,64,170)", "rgb(150,61,179)", "rgb(191,60,175)", "rgb(228,65,157)", "rgb(254,75,131)", "rgb(255,94,99)", "rgb(255,120,71)", "rgb(251,150,51)", "rgb(226,183,47)", "rgb(198,214,60)", "rgb(175,240,91)", "rgb(127,246,88)", "rgb(82,246,103)", "rgb(48,239,130)", "rgb(29,223,163)", "rgb(26,199,194)", "rgb(35,171,216)", "rgb(54,140,225)", "rgb(76,110,219)", "rgb(96,84,200)"]
            // let c = {
            //     text: "",
            //     prefixP: -s,
            //     skillI: 0,
            //     skillP: 0,
            //     direction: "forward",
            //     delay: a,
            //     step: g
            // }

            // function t() {
            //     return b[Math.floor(Math.random() * b.length)]
            // }

            // function e() {
            //     return String.fromCharCode(94 * Math.random() + 33)
            // }

            // function n(r) {
            //     for (var n = document.createDocumentFragment(), i = 0; r > i; i++) {
            //         let l = document.createElement("span")
            //         l.textContent = e(), l.style.color = t(), n.appendChild(l)
            //     }
            //     return n
            // }

            // function i() {
            //     let t = o[c.skillI]
            //     c.step ? c.step-- : (c.step = g, c.prefixP < l.length ? (c.prefixP >= 0 && (c.text += l[c.prefixP]), c.prefixP++) : "forward" === c.direction ? c.skillP < t.length ? (c.text += t[c.skillP], c.skillP++) : c.delay ? c.delay-- : (c.direction = "backward", c.delay = a) : c.skillP > 0 ? (c.text = c.text.slice(0, -1), c.skillP--) : (c.skillI = (c.skillI + 1) % o.length, c.direction = "forward")), r.textContent = c.text, r.appendChild(n(c.prefixP < l.length ? Math.min(s, s + c.prefixP) : Math.min(s, t.length - c.skillP))), setTimeout(i, d)
            // }
            // i()
        }
    }
}
</script>

<style scoped>
.ui.circular.icon.button {
    width: 38px;
}

#rollText {
    font-size: 15px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}

.el_tooltip_content {
    text-align: center;
    font-size: 16px;
}

.m-right-font-small,
#rollText {
    padding-left: 20px !important;
}

.chat-image {
    width: 150px !important;
    height: 150px !important;
}

.user-bilibili-link {
    position: relative;
    margin-top: 100px !important;
    padding-top: 100px !important;
}

.hobby {
    margin-top: 15px !important;
    text-align: left !important;
}

.m-user-icon {
    width: 28px !important;
    height: 28px !important;
    margin: auto 2px !important;
}

.el-collapse-item {
    padding-left: 15px;
    padding-right: 15px;
}

.el-collapse-item .el-collapse-item__content {
    padding-bottom: 10px;
}
</style>
