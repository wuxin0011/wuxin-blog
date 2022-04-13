import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./plugins/index"; // 插件 注册 全局组件注册
import "./assets/css/index.css"; // 样式
import 'animate.css'; // 动画样式

/**
 * 关闭生产消息提示
 */
Vue.config.productionTip = false;

/**
 * 捕获错误信息
 */
Vue.config.errorHandler = function (err, vm, info) {
    console.log(err)
    console.log(vm)
    console.log(info)
}


/**
 * 捕获警告信息
 */
Vue.config.warnHandler = function (msg, vm, trace) {
    console.log(msg)
    console.log(vm)
    console.log(trace)
}








console.log("\n\n %c gitee v2.0.1 %c https://gitee.com/wuxin0011 \n","color: #fadfa3; background: rgb(199, 29, 35); padding:5px 0;","background: #fadfa3; padding:5px 0;")
console.log("\n %c github v2.0.1 %c https://github.com/wuxin0011 \n","color:#000; background: rgb(223, 245, 255); padding:5px 0;","background: #fadfa3; padding:5px 0;")

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount("#app");
