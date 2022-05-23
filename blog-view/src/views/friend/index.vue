<template>
<div class="container">
    <div v-if="friendList && friendList.length !== 0">
        <Title title="友情链接" />
        <div class="ui attached segment">
            <div class="ui link three stackable cards">
                <a :href="item.url" target="_blank" class="card" :class="randomColor()" v-for="(item, index) in randomFriendList" :key="index">
                    <div class="image">
                        <img :src="item.avatar" alt="" />
                    </div>
                    <div class="content">
                        <div class="header" style="color: white">{{ item.username }}</div>
                        <div class="description" style="color: white">
                            {{ item.introduction }}
                        </div>
                    </div>
                </a>
            </div>
        </div>

        <!--      底部内容-->
        <div class="ui teal attached segment">
            <div class="typo content" v-viewer v-html="message.content"></div>
        </div>

        <!--评论-->
        <div class="ui bottom teal attached segment comments">
            <Comment :type="3" :comment-enabled="message.commentEnabled" />
        </div>
    </div>
    <LoadFail v-else />
</div>
</template>

<script>
import {
    getFriendList
} from "@/api/friend";
import Comment from "@/components/comment/Comment";
import LoadFail from "@/views/error/LoadFail";
import Title from "@/components/common/Title";

export default {
    name: "Friend",
    data() {
        return {
            title: "友情链接",
            friendList: [],
            message: {},
            colorObj: [
                "bg-1",
                "bg-2",
                "bg-3",
                "bg-4",
                "bg-5",
                "bg-6 ",
                "bg-7",
                "bg-8",
                "bg-9",
            ],
        };
    },
    components: {
        Title,
        LoadFail,
        Comment,
    },

    computed: {
        // 标签背景颜色随机
        randomColor() {
            return () => {
                return this.colorObj[Math.floor(Math.random() * this.colorObj.length)];
            };
        },

        // 随机将友情链接排序
        randomFriendList() {
            let len = this.friendList.length;
            let arr = this.friendList;
            for (let i = 0; i < len - 1; i++) {
                let index = parseInt(Math.random() * (len - i));
                let temp = arr[index];
                arr[index] = arr[len - i - 1];
                arr[len - i - 1] = temp;
            }
            return arr;
        },
    },
    created() {
        this.getList();
    },
    methods: {
        getList() {
            getFriendList().then((res) => {
                if (res.code === 200) {
                    const {
                        message,
                        list
                    } = res;
                    this.friendList = list;
                    this.message = message;
                }
            });
        },
    },
};
</script>

<style scoped>
/*.container {*/
/*  margin: 20px 0;*/
/*}*/

.image {
    display: flex !important;
    justify-content: center !important;
}

.ui>.card {
    text-align: center;
    display: flex !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: space-around !important;
    overflow: hidden !important;
    padding: 20px !important;
}

.ui.card>.image,
.ui.cards>.card>.image {
    padding: 0;
    background: rgba(0, 0, 0, 0);
}

/* 头像设置为原型图片 */
img {
    width: 80px !important;
    height: 80px !important;
    border-radius: 50% !important;
}

.ui.card.content.header,
.ui.card.content.description {
    color: #ffffff !important;
}

.ui.card>.content,
.ui.cards>.card>.content {
    border: none !important;
}

.bg-1 {
    background-color: rgb(238, 90, 36) !important;
}

.bg-2 {
    background-color: rgb(100, 100, 0) !important;
}

.bg-3 {
    background-color: rgb(50, 191, 255) !important;
}

.bg-4 {
    background-color: rgb(0, 128, 128) !important;
}

.bg-5 {
    background-color: rgb(100, 100, 130) !important;
}

.bg-6 {
    background-color: rgb(100, 100, 232) !important;
}

.bg-7 {
    background-color: rgb(255, 165, 0) !important;
}

.bg-8 {
    background-color: rgb(139, 69, 19) !important;
}

.bg-9 {
    background-color: rgb(124, 136, 83) !important;
}

.night-mode {
    background-color: rgb(96, 98, 102) !important;
    border: rgb(30, 10, 10) 1px solid;
}

/* 添加阴影 */
.card {
    box-shadow: 1px 1px 1px rgba(0, 0, 0, 0.6), 2px 2px 2px rgba(0, 0, 0, 0.4),
        3px 4px 4px rgba(0, 0, 0, 0.2), 4px 4px 4px rgba(0, 0, 0, 0.1) !important;
}

.card:hover {
    box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.6), 4px 4px 4px rgba(0, 0, 0, 0.4),
        4px 4px 4px rgba(0, 0, 0, 0.2), 6px 6px 6px rgba(0, 0, 0, 0.1) !important;
}

/* 添加字体阴影 */
.content .header,
.content .description {
    text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.3), 2px 2px 2px rgba(0, 0, 0, 0.4) !important;
}

/* 修改默认样式 */
@media only screen and (max-width: 767px) {
    .ui.stackable .cards>.card {
        display: block !important;
        height: auto !important;
        margin: 1em 1em;
        padding: 20px !important;
        width: calc(100% - 2em) !important;
    }
}
</style>
