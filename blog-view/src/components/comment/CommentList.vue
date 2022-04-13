<template>
<div class="ui comments" style="width:100%;min-widht:100%;max-widht:100%;">
    <div v-if="commentEnabled">
        <div v-if="commentList && commentList.length !== 0">
            <h3 class="ui dividing header">共 {{ commentCount }} 条评论</h3>
            <div class="comment" v-for="(comment, index) in commentList" :key="`comment${index}`">
                <a class="avatar">
                    <img :src="comment.avatar" v-if="comment.avatar" />
                    <svg v-else t="1644976238253" class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="44" height="44">
                        <path d="M510.238 4.399C233.571 4.399 7.21 230.761 7.21 507.427c0 276.661 226.362 503.023 503.028 503.023 276.661 0 503.023-226.362 503.023-503.023 0-276.668-226.362-503.028-503.023-503.028z m12.572 150.907c191.779 0 191.779 144.623 191.779 226.362 0 81.745-75.455 232.652-191.779 235.793-113.182 0-191.779-150.907-191.779-235.793 0.001-81.74 0.001-226.362 191.779-226.362z m-12.572 826.85c-144.623 0-276.667-66.026-364.695-169.769 12.574-31.441 28.293-66.026 50.303-84.886 47.156-37.73 188.631-100.608 188.631-100.608l88.03 169.774 15.721-40.873-25.152-50.303 50.303-50.299 50.303 50.299-22.011 53.446 12.578 40.874 91.172-166.627s141.475 62.877 188.636 100.608c22.004 15.716 37.725 44.015 47.157 69.161-84.884 110.039-220.071 179.204-370.977 179.204z m0 0z" fill="#cdcdcd" p-id="4574"></path>
                    </svg>
                </a>
                <div class="content m-padding-left-small">
                    <a class="author">{{ comment.username }}</a>
                    <a class="ui label left pointing mini m-margin-left-small" v-if="comment.commentUserId === 1" :class="`${ site && site.commentLabelColor ? site.commentLabelColor : 'orange' }`">{{ site && site.commentLabelName ? site.commentLabelName : "作者" }}</a>
                    <div class="text">
                        <a class="ui label basic pink mini" v-if="comment.top !== 0">置顶</a>
                        {{ comment.content }}
                    </div>
                    <div class="actions">
                        <span class="metadata date">{{ comment.createTime | formatDateTime }}</span>
                        <a class="reply" @click.prevent="closeReply(-1)" v-if="parentCommentId === comment.commentId">取消</a>
                        <a class="reply" @click.prevent="showReply(comment)" v-else>回复</a>
                        <CommentForm v-if="parentCommentId === comment.commentId" :comment-type="commentType" :comment-user-id="comment.commentUserId" :comment-id="comment.commentId" :submitLoading="submitLoading" :id="id" :type="type" :placeholder="placeholder" @addReply="addReply" :reply-content="comment.content" />
                    </div>

                    <div class="comments" v-for="(reply, replyIndex) in comment.replyList" :key="reply.replyId">
                        <div class="comment" v-if="replyIndex < 3 || loadingList[comment.commentId]">
                            <a class="avatar">
                                <img :src="reply.replyAvatar" v-if="reply.replyAvatar" />
                                <svg v-else t="1644976238253" class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="32" height="32">
                                    <path d="M510.238 4.399C233.571 4.399 7.21 230.761 7.21 507.427c0 276.661 226.362 503.023 503.028 503.023 276.661 0 503.023-226.362 503.023-503.023 0-276.668-226.362-503.028-503.023-503.028z m12.572 150.907c191.779 0 191.779 144.623 191.779 226.362 0 81.745-75.455 232.652-191.779 235.793-113.182 0-191.779-150.907-191.779-235.793 0.001-81.74 0.001-226.362 191.779-226.362z m-12.572 826.85c-144.623 0-276.667-66.026-364.695-169.769 12.574-31.441 28.293-66.026 50.303-84.886 47.156-37.73 188.631-100.608 188.631-100.608l88.03 169.774 15.721-40.873-25.152-50.303 50.303-50.299 50.303 50.299-22.011 53.446 12.578 40.874 91.172-166.627s141.475 62.877 188.636 100.608c22.004 15.716 37.725 44.015 47.157 69.161-84.884 110.039-220.071 179.204-370.977 179.204z m0 0z" fill="#cdcdcd" p-id="4574"></path>
                                </svg>
                            </a>
                            <div class="content">
                                <a class="author">{{ reply.replyUsername }}</a>
                                <a class="ui label left pointing mini m-margin-left-small" v-if="reply.replyUserId === 1" :class="`${ site && site.commentLabelColor ? site.commentLabelColor : 'orange' }`">{{ site && site.commentLabelName ? site.commentLabelName : "作者" }}</a>
                                <div class="text">
                                    <a class="author m-padding-right-small" v-if="reply.replyUsername !== reply.commentUsername">
                                        @ {{ reply.commentUsername }}</a>{{ reply.replyContent }}
                                </div>
                                <div class="actions">
                                    <span class="metadata date">{{ reply.createTime | formatDateTime }}</span>
                                    <a class="reply" @click.prevent="closeReply(-1)" v-if="parentCommentId === reply.replyId">取消</a>
                                    <a class="reply" @click.prevent="showReply1(reply)" v-else>回复</a>
                                    <CommentForm v-if="reply.replyId === parentCommentId" :comment-type="commentType" :submitLoading="submitLoading" :comment-id="comment.commentId" :id="id" :comment-user-id="reply.replyUserId" :type="type" @addReply="addReply" :reply-content="reply.replyContent" :placeholder="placeholder" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="comments m-more" v-if=" comment.replyList && comment.replyList.length > 3 && !loadingList[comment.commentId] ">
                        <span class="metadata">共计{{ comment.replyList.length }}条回复</span>
                        <a class="author m-loading-more-link" @click.prevent="loadingReply(comment.commentId)">点击加载更多</a>
                    </div>
                </div>
            </div>
            <div style="text-align: center">
                <div class="ui text basic button m-margin-tb-big item" :class="{ loading: loading }" @click="loadingComment" v-if="isShowLoading">
                    剩余评论{{ remainingComments }}条,点击加载更多
                </div>
            </div>
        </div>
        <h3 class="ui header" v-else>
            <div class="ui divider"></div>
            ~暂无评论~
        </h3>
    </div>
    <h3 class="ui header" v-else>
        <div class="ui divider"></div>
        ~评论已关闭~
    </h3>
</div>
</template>

<script>
import CommentForm from "@/components/comment/CommentForm";
import {
    getDateDiff
} from "@/utils/validate.js";

export default {
    name: "CommentList",
    components: {
        CommentForm,
    },
    data() {
        return {
            commentType: "reply",
            commentId: 0,
            replyId: 0,
            placeholder: "评论千万条，文明第一条",
            loadingList: {},
            loading: false,
        };
    },

    filters: {
        formatDateTime(value) {
            return getDateDiff(value);
        },
    },
    props: {
        commentEnabled: {
            type: Boolean,
            default: true,
        },
        commentList: {
            type: Array,
            default: () => {
                return [];
            },
        },
        commentCount: {
            type: Number,
            default: 0,
        },
        parentCommentId: {
            type: Number | String,
            default: -1,
        },
        type: {
            type: Number | String,
            default: -1,
        },

         submitLoading: {
            type: Boolean,
            default: false,
        },

        id: {
            type: Number | String,
            default: null,
        },
        limit: {
            type: Number | String,
            default: 5,
        },

        commentTotal: {
            type: Number,
            default: 0,
        },
    },

    computed: {
        site() {
            return this.$store.state.index.site;
        },

        // 是否显示加载按钮
        isShowLoading() {

            return this.commentList.length < this.commentTotal;
        },

        //  剩余评论多少条
        remainingComments() {
            // 已加装评论
            let total = 0;
            this.commentList.forEach((comment) => {
                total++;
                comment.replyList.forEach(() => {
                    total++;
                });
            });

            return this.commentCount - total;
        },
    },

    methods: {
        showReply(comment) {
            this.placeholder = `回复 @${comment.username}`;
            this.$emit("setParentId", comment.commentId);
        },

        showReply1(reply) {
            this.placeholder = `回复 @${reply.replyUsername}`;
            this.$emit("setParentId", reply.replyId);
        },

        closeReply() {
            this.$emit("setParentId", -1);
        },

        addReply(reply) {
            this.$emit("addReply", reply);
        },

        loadingReply(commentId) {
            if (commentId === null || commentId === "") {
                return;
            }
            if (this.loadingList[commentId]) {
                return;
            }
            // 使用set方法给loading对象添加属性 this.laodingList 形式为:{1:1,1}
            this.$set(this.loadingList, commentId, commentId);
        },

        loadingComment() {
            this.loading = true;
            setTimeout(() => {
                this.loading = false;
                this.limit = this.limit + 5;
                this.$emit("loadingMore", this.limit);
            }, 3000);
        },
    },
};
</script>

<style scoped>
.typo a {
    border-bottom: 1px solid #fff;
    font-family: "Times New Roman", Times, serif;
}

.ui.comments {
    min-width: 100% !important;
}

.ui.comments .comment>.avatar~.content {
    margin-left: 4em !important;
}

.avatar>img {
    border-radius: 50% !important;
    width: 50px !important;
    height: 50px !important;
}

.reply {
    padding-left: 20px !important;
}

.m-padding-right-small {
    padding-right: 10px !important;
}

.m-padding-left-small {
    padding-left: 10px !important;
}

.ui.comments .comment .comments {
    margin: 1em 0 0.5em 0.5em !important;
    padding: 0 0.2em !important;
}

.m-more {
    margin-top: 20px;
}

.m-loading-more-link {
    margin-left: 20px !important;
    color: black !important;
}

.m-margin-tb-big {
    color: black !important;
    margin-top: 20px !important;
    margin-bottom: 10px !important;
    text-align: center !important;
}

.m-loading-more-link,
.m-margin-tb-big:hover {
    color: rgba(160, 147, 159, 0.815) !important;
}
</style>
