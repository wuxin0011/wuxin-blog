<template>
<div class="header-container m-animation-welcome" :style="headerBackgroundImage" ref="header">
</div>
</template>

<script>
import {
    tvList
} from '@/utils/setting'
import {
    mapGetters
} from "vuex";
const changeUrl = (url) => {
    return 'url(' + `${url}` + ')'
}
export default {
    name: "HeaderIndex",
    props: {
        webName: {
            type: String,
            default: 'wuxin`blog',
        },
    },
    data() {
        return {
            loaded: false,
            headerBackgroundImage: {
                'backgroundImage': changeUrl('https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main//blog-resource/img/bg1.jpg')
            },
            container: null,
            clickCount: 1,
        }
    },

    computed: {
        ...mapGetters(['clientSize'])
    },
    mounted() {
        this.init()
        this.setHeaderHeight()
    },

    methods: {

        init() {
            this.container = this.$refs.header
        },

        //根据可视窗口高度，动态改变首图大小
        setHeaderHeight() {
            this.$refs.header.style.height = this.clientSize.clientHeight + 'px'
        },
        // 动至正文部分
        scrollToMain() {
            window.scrollTo({
                top: this.clientSize.clientHeight,
                behavior: 'smooth'
            })
        }

    },
}
</script>

<style lang="scss" scoped>
* {
    margin: 0;
    padding: 0;
}

.header-container {
    position: relative;
    width: 100%;
    min-height: 100vh;
    background-repeat: no-repeat;
    background-size: cover;
    background-attachment: fixed;

}

@keyframes welcomeAnimation {
    0% {
        opacity: 0;
        filter: alpha(opacity=0);
        transform: translateY(-50px);
    }

    100% {
        opacity: 1;
        -webkit-filter: none;
        filter: none;
        transform: translateY(0);
    }
}

@-webkit-keyframes welcomeAnimation {
    0% {
        opacity: 0;
        filter: alpha(opacity=0);
        transform: translateY(-50px);
    }

    100% {
        opacity: 1;
        -webkit-filter: none;
        filter: none;
        transform: translateY(0);
    }
}

.m-animation-welcome {
    animation-duration: 1s;
    animation-timing-function: ease;
    animation-iteration-count: 1;
    animation-direction: normal;
    animation-fill-mode: none;
    animation-play-state: running;
    animation-name: welcomeAnimation;
}
</style>
