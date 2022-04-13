<template>
  <div class="ui container">
    <div v-if="about&&about.content">
      <Title title="关于我" />
      <div class="ui attached segment typo content" v-html="about.content" />
      <div class="ui bottom teal attached segment  comments">
        <Comment :commentEnabled="about.commentEnabled" :type="2"/>
      </div>
    </div>
    <LoadFail v-else />
  </div>
</template>

<script>
import Title from "@/components/common/Title";
import {
  getAbout,
} from '@/api/about'
import Comment from "@/components/comment/Comment";
import {
  formatLink
} from "@/utils/link";
import LoadFail from "@/views/error/LoadFail";

export default {
  name: "Blog",
  components: {
    LoadFail,
    Comment,
    Title,
  },
  data() {
    return {
      title: "关于我",
      about: {}
    };
  },

  created() {
    this.init();
  },

  methods: {

    init() {
      getAbout().then(res => {
        this.about = res.result
        this.about.content = formatLink(this.about.content)
        this.$nextTick(() => {
          Prism.highlightAll()
        });
      });
    },
  },


};
</script>

<style scoped>
.el-divider {
  margin: 1rem 0 !important;
}

h1::before,
h2::before,
h3::before,
h4::before,
h5::before,
h6::before {
  display: block;
  content: " ";
  height: 55px;
  margin-top: -55px;
  visibility: hidden;
}
</style>
