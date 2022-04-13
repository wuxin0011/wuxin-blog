<template lang="html">
  <div class="ui container" style="margin-top: 20px;">
    <CommentForm @addComment="addMyComment" :comment-type="commentType" :id="id" :submitLoading="submitLoading"
                 v-if="commentEnabled" :type="type" />
    <CommentList @addReply="addMyReply" @setParentId="setParentId" :id="id" :limit="query.limit"
                 :comment-total="commentTotal" :type="type" :comment-list="commentList" :commentCount="commentCount"
                 :submitLoading="submitLoading"
                 :comment-enabled="commentEnabled" :parentCommentId="parentCommentId" @loadingMore="loadingMore" />
  </div>
</template>

<script>
import CommentForm from "@/components/comment/CommentForm";
import CommentList from "@/components/comment/CommentList";
import {
  mapActions
} from "vuex";
import {
  addComment,
  addReply,
  getCommentList,
} from "@/api/comment";
import {
  SET_CLEAN_CONTENT
} from "@/store/mutations-type";

export default {
  name: "Comment",
  components: {
    CommentList,
    CommentForm,
  },
  props: {
    commentEnabled: {
      type: Boolean,
      default: true
    },
    type: {
      type: Number | String,
      default: -1
    },
    // 文章id如果是关于我和友情链接不需id
    id: {
      type: Number | String,
      default: null
    }
  },
  data() {
    return {
      commentType: 'comment',
      query: {
        current: 1,
        limit: 5
      },
      submitLoading:false, //提交时加载提示
      totalPage: 0,
      parentCommentId: -1,
      commentCount: 0,
      commentTotal: 0,
      commentList: [],
    };
  },

  methods: {

    ...mapActions('comment', ['setCommentUser']),

    addMyComment(comment) {
      this.setLoading(true)
      addComment(comment).then((res) => {
        if (res.code === 200) {
          this.setLoading(false)
          this.$notify.success("发表成功！");
          // 清空发布内容
          this.$store.commit(`comment/${SET_CLEAN_CONTENT}`, '')
          // 重新加载评论
          this.getList()
        }
      }).then(() => {
        this.setCommentUser({
          "username": comment.username,
          "email": comment.email,
          "subscription": comment.subscription
        })

      }).catch(() => {
        this.errorLoading()
      });
    },

    addMyReply(reply) {
      this.setLoading(true)
      addReply(reply).then((res) => {
        if (res.code === 200) {
          // 清空发布内容
          this.$store.commit(`comment/${SET_CLEAN_CONTENT}`, '')
          this.setLoading(false)
          this.$notify.success("发表成功！");
          // 重新加载评论
          this.getList()
        }
      }).then(() => {
        this.setCommentUser({
          "username": reply.replyUsername,
          "email": reply.replyEmail,
          "subscription": reply.subscription
        })
      }).catch(() => {
        this.errorLoading()
      })
    },

    getList() {
      if (this.commentEnabled) {
        getCommentList(this.query.current, this.query.limit, this.type, this.id).then(res => {
          if (res.code === 200) {
            const {
              commentList,
              commentCount,
              commentTotal
            } = res
            this.commentList = commentList
            this.commentTotal = commentTotal
            this.commentCount = commentCount
          }
        })

      }
    },

    loadingMore(limit) {
      this.query.limit = limit
      this.getList()
    },

    setParentId(id) {
      this.parentCommentId = id
    },

    setLoading(loading) {
      this.submitLoading = loading
    },

    errorLoading() {
      setTimeout(() => {
        this.setLoading(false)
      }, 5000)
    },

  },

  mounted() {
    this.getList()
  },
};
</script>
