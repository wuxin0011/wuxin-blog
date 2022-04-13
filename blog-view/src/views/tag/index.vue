<template>
  <div>
    <BlogList :getList="getList" :blogList="blogList" :totalPage="totalPage" />
  </div>
</template>

<script>
import BlogList from "@/components/blog/BlogList";
import {getBlogListByTagName} from "@/api/tag";

export default {
  data() {
    return {
      blogList: [],
      totalPage: 0,
    };
  },
  components: {
    BlogList,
  },
  computed: {
    name() {
      return this.$route.params.name
    }
  },
  watch: {
     // 监听路由分类名是否改变
    '$route.fullPath'() {
      if (this.$route.name === 'Tag') {
        this.getList(1)
      }
    }
  },
  created() {
    this.getList(1)
  },

  methods: {
    getList(current) {
      
      getBlogListByTagName(current,this.name).then((res) => {
        
        if (res.code === 200) {
          const {records, pages} = res.result;
          this.blogList = records;
          this.totalPage = pages;
          this.$nextTick(() => {
            Prism.highlightAll();
          });
        } else {
          this.blogList = []
          this.totalPage = 0
        }
      });
    },
  },
};
</script>

