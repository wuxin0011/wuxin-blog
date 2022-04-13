<template>
  <div class="ui attached segment "  style="width: 100%;min-height: 100vh;">
    <div v-if="list&&list.length!==0">
      <Title :title="title" />
      <div class="update-question-container" >
        <div class="ui input" style="float: right;z-index: 100">
          <input type="text" placeholder="Search..." v-model="keywords">
        </div>
        <el-timeline>
          <el-timeline-item v-for="(item, index) in list"  :key="`update-${index}`"
                            :color="item.color"
                            :timestamp="new Date(item.createTime).getTime() | formatDate" size="large">
            <span > {{ item.content }}</span>
          </el-timeline-item>
        </el-timeline>
      </div>

    </div>
    <LoadFail v-else/>
  </div>
</template>

<script>

import UpdateList from '@/components/update/UpdateList'
import Title from '@/components/common/Title'
import {getUpdateQuestion} from '@/api/update.js'
import dateFormat from "@/utils/date";
import LoadFail from "@/views/error/LoadFail";

export default {
  name: 'update',
  components: {
    LoadFail,
    UpdateList,
    Title
  },
  data() {
    return {
      updateQuestionList: [],
      title: '更新内容',
      keywords:''
    }
  },

  filters: {
    formatDate(time) {
      let date = new Date(time);
      return dateFormat.formatDate(date, "yyyy-MM-dd");
    },
  },
  computed: {
    list() {
      return this.updateQuestionList.filter((item, index) => {
        let time = item.createTime.indexOf(this.keywords) !== -1
        let content = item.content.indexOf(this.keywords) !== -1
        return time || content;
      })
    }
  },
  methods: {
    getList() {
      getUpdateQuestion().then((res) => {
        if (res.code === 200) {
          this.updateQuestionList = res.result
        }
      })
    }
  },
  created() {
    this.getList();
  },
};
</script>
