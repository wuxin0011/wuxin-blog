<template>
<div>
    <BlogList :getList="getList" :blogList="blogList" :totalPage="totalPage" />
</div>
</template>

<script>
import BlogList from "@/components/blog/BlogList";
import {
    getBlogList
} from "@/api/blog";
import {
    formatLink
} from "@/utils/link";

export default {
    data() {
        return {
            blogList: [],
            totalPage: 0,
            loading: false,
        }
    },
    components: {
        BlogList,
    },

    beforeRouteEnter(to, from, next) {
        next(vm => {
            if (from.name !== 'Blog') {
                //其它页面跳转到首页时，重新请求数据
                //设置一个flag，让首页的分页组件指向正确的页码
                // vm.$store.commit(SET_IS_BLOG_TO_HOME, false)
                vm.getList(1)
            } else {
                //如果文章页面是起始访问页，首页将是第一次进入，即缓存不存在，要请求数据
                if (!vm.getBlogListFinish) {
                    vm.getList(1)
                }
                //从文章页面跳转到首页时，使用首页缓存
                // vm.$store.commit(SET_IS_BLOG_TO_HOME, true)
            }
        })
    },

    methods: {
        getList(pageNum) {
            getBlogList(pageNum).then(res => {
                if (res.code === 200) {
                    const {
                        records,
                        pages
                    } = res.result
                    records.forEach(blog => {
                        blog.description = formatLink(blog.description)
                    })
                    this.blogList = records
                    this.totalPage = pages
                    this.$nextTick(() => {
                        //  加载代码高亮插件
                        Prism.highlightAll()
                    })
                    // this.loading = true
                }
            }).catch(() => {
                this.$message.error("请求失败")
            })
        },

    },
    // mounted() {
    //     this.getList(1);
    // },
}
</script>

<style scoped>
</style>
