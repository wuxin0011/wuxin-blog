<template>
  <div>
    <BlogList
        :getList="getList"
        :blogList="blogList"
        :totalPage="totalPage"
    />
  </div>
</template>

<script>
import BlogList from "@/components/blog/BlogList";
import { getBlogByCategoryName } from "@/api/tag";

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
  watch: {
    // 监听路由分类名是否改变
    '$route.fullPath'() {
      if (this.$route.name === 'Category') {
        this.getList(1)
      }
    }
  },
  computed: {
    categoryName() {
      return this.$route.params.name
    }
  },
  created(){
    this.getList(1)
  },
  methods: {
    getList(current) {
      
      getBlogByCategoryName(current,this.categoryName).then((res) => {
        if (res.code === 200) {
          const { pages, records } = res.result;
          this.blogList = records;
          this.totalPage = pages;
          this.$nextTick(() => {
            Prism.highlightAll();
          });
        }else {
          this.blogList = []
          this.totalPage = 0
        }

      });
    },

    currentPage(current){
      
      this.getList(current)
    }
  },


};
</script>

