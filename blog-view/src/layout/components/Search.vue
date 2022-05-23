<template>
<div>
    <sui-modal v-model="open" size="small" style="min-height: 500px">
        <sui-modal-header class="header">
            <div class="ui input right icon m-search-input " :class="{'loading':loading}">
                <input type="text" placeholder="search..." v-model="keywords" @keyup.enter="search">
                <i class="ui icon link search" @click="search"></i>
            </div>
        </sui-modal-header>
        <sui-modal-content scrolling>
            <div class="ui segments" style="min-height: 300px">
                <transition enter-active-class="m-animate-fadeIn">
                    <div class="ui center aligned segment" v-if="showKeywords&&blogs.length===0&&isSearch" key="notContent">
                        <p class="m-text-thin"> 搜索不到您输入的内容: <span style="color: red;font-weight: 700;">{{ showKeywords }}</span>，去这些标签看看吧!
                        </p>
                        <div class="ui labels ">
                            <router-link :class="`ui label ${tag.color}`" @click.native="toggle" v-for="(tag,index) in tagList" :key="index" :to="`/tag/${tag.name}`">
                                {{ tag.name }}
                            </router-link>
                        </div>
                        <div class="ui labels">
                            <router-link :class="`ui label ${category.color}`" v-for="(category,index) in categoryList" :key="index" @click.native="toggle" :to="`/category/${category.name}`">{{ category.name }}
                            </router-link>
                        </div>
                    </div>
                    <div v-else>
                        <router-link class="item " v-for="(blog,index) in blogs" :key="`search${index}`" :class="{'loading':loading}" :to="`/blog/${blog.blogId}`" @click.native="toggle">{{index+1}} &nbsp;、&nbsp; {{ blog.title }}</router-link>
                    </div>
                </transition>
            </div>
        </sui-modal-content>
        <sui-modal-actions>
            <button class="ui blue button" @click="toggle">关闭</button>
        </sui-modal-actions>
    </sui-modal>
</div>
</template>

<script>
import {
    searchBlog
} from "@/api/blog";

export default {
    name: "Search",
    data() {
        return {
            blogs: [],
            keywords: null,
            loading: false,
            isSearch: false,
            showKeywords: null,
        };
    },
    props: {
        open: {
            type: Boolean,
            default: false,
        },
    },

    computed: {
        tagList() {
            return this.$store.state.index.tagList;
        },
        categoryList() {
            return this.$store.state.index.categoryList;
        },
    },

    methods: {
        toggle() {
            this.$emit("changeSearch", true);
        },
        search() {
            const keywords = this.keywords;
            if (keywords === null || keywords.trim() === "") {
                this.$message.warning("关键词不能为空！");
                return;
            }

            if (keywords.trim().length > 15) {
                this.$message.warning("关键词不能过15个字符！");
                return;
            }

            if (
                keywords.indexOf("%") !== -1 ||
                keywords.indexOf("_") !== -1 ||
                keywords.indexOf("操") !== -1 ||
                keywords.indexOf("nm") !== -1 ||
                keywords.indexOf("[") !== -1 ||
                keywords.indexOf("#") !== -1 ||
                keywords.indexOf("*") !== -1 ||
                keywords.indexOf("<") !== -1 ||
                keywords.indexOf(">") !== -1 ||
                keywords.indexOf("h1") !== -1 ||
                keywords.indexOf("h2") !== -1 ||
                keywords.indexOf("h3") !== -1 ||
                keywords.indexOf("h4") !== -1
            ) {
                this.$message.warning("关键词不能包括特殊字符");
                return;
            }
            this.loading = true;
            setTimeout(() => {
                searchBlog(this.keywords).then((res) => {
                    if (res.code === 200) {
                        this.blogs = res.result;
                    }
                    this.loading = false;
                    this.isSearch = true;
                    this.showKeywords = this.keywords;
                }).catch(() => {
                    this.loading = false
                });
            }, 3000);
        },
    },
};
</script>

<style scoped>
.m-search-input {
    width: 100%;
}

.m-search-input>input {
    font-size: 18px;
}

.m-label-content {
    text-align: center;
}

.header {
    color: darkcyan;
    font-size: 18px;
    font-family: "mp-quote", -apple-system-font, BlinkMacSystemFont,
        "Helvetica Neue", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei UI",
        "Microsoft YaHei", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji",
        "Noto Color Emoji", "Segoe UI Symbol", "Android Emoji", "EmojiSymbols";
}

a.item {
    color: black !important;
}

a.item:hover {
    color: skyblue !important;
}

.m-animate-fadeIn {
    animation-name: m_fadeIn;
    animation-iteration-count: 1;
    animation-duration: 1s;
}

@-webkit-keyframes m_fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes m_fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}
</style>
