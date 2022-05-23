<template>
<div class="ui bottom" style="text-align: center">
    <el-pagination @current-change="handleCurrentChange" :current-page="pageNum" :page-count="totalPage" layout="prev, pager, next" background hide-on-single-page />
</div>
</template>

<script>
const cubic = value => Math.pow(value, 4);
const easeInOutCubic = value => value < 0.4 ?
    cubic(value * 2) / 2 :
    1 - cubic((1 - value) * 2) / 2;

export default {
    name: "MyPagination",
    props: {
        getList: {
            type: Function,
            default: () => {}
        },
        current: {
            type: Number,
            default: 1
        },
        totalPage: {
            type: Number,
            default: 0,
        },
        isScrollTop: {
            type: Boolean,
            default: true
        }
    },
    computed: {
        clientSize() {
            return this.$store.state.setting.clientSize
        }
    },
    activated() {
        this.$nextTick(() => {
            if (this.$route.name !== "Index") {
                this.pageNum = 1;
            }
        });
    },
    data() {
        return {
            // 默认页码
            pageNum: 1,
        };
    },
    methods: {
        //监听页码改变的事件
        handleCurrentChange(newPage) {
            if (this.$route.name === 'Index') {
                window.scrollTo({
                    top: this.clientSize.clientHeight,
                    behavior: 'smooth'
                })
            } else {
                this.scrollToTop()
            }
            this.pageNum = newPage
            this.getList(newPage)
        },

    }
};
</script>
