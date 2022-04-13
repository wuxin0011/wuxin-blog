import Vue from "vue";
import MyPagination from "@/components/common/Pagination";
import store from "@/store";
/**
 * 注册全局分页组件
 */
Vue.component(MyPagination.name, MyPagination)


/**
 * 参考elementui回到顶部组件
 */

const cubic = value => Math.pow(value, 3);
const easeInOutCubic = value => value < 0.5 ? cubic(value * 2) / 2 : 1 - cubic((1 - value) * 2) / 2;
Vue.prototype.scrollToTop = function () {
	const el = document.documentElement
	const beginTime = Date.now()
	const beginValue = el.scrollTop
	const rAF = window.requestAnimationFrame || (func => setTimeout(func, 16))
	const frameFunc = () => {
		const progress = (Date.now() - beginTime) / 500;
		if (progress < 1) {
			el.scrollTop = beginValue * (1 - easeInOutCubic(progress))
			rAF(frameFunc)
		} else {
			el.scrollTop = 0
		}
	}
	rAF(frameFunc)
}


/**
 * 背景色配置
 */
Vue.mixin({
    computed: {
        bgColor() {
            if (store.state.setting.nightMode) {
                return {
                    'backgroundColor': 'rgb(40, 40, 35)',
                    'color': 'rgb(212, 212, 212)',
                    'border': 0
                }
            } else {
                return {
                    'backgroundColor': 'rgb(255, 255, 255)',
                    'color': '#303133'
                }
            }
        }
    },
})
