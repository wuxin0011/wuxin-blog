<template>
  <div class="ui top attached segment center aligned" >
    <h2>{{title}}</h2>
  </div>
</template>
<script>

export default {
  name:'Title',
  props:{
    title:{
      type:String,
      default:'欢迎！'
    }
  }
}

</script>
<style scoped>


.title{
  line-height: 30px;
  font-size: 25px;
  text-align: center;
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  letter-spacing: 2px;
}


</style>