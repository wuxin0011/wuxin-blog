<template>
<div class="ui segments  m-box tag-sidebar" v-if="tagList&&tagList.length!==0" >
    <div class="ui secondary segment " ><i class="tags icon" ></i>标签云</div>
    <div class="ui teal segment m-padding-small" >
        <router-link :to="`/tag/${tag.name}`" class="ui label m-text-500" :class="tag.color" v-for="(tag,index) in tagList" :key="`tag${index}`">
            {{ tag.name }}
        </router-link>
    </div>
</div>
</template>

<script>
export default {
    name: "TagSidebar",
    computed: {
      tagList() {
        return this.$store.state.index.tagList 
      },
    
    },

}
</script>

<style scoped>
.secondary.segment {
    padding: 10px;
}

.m-padding-small {
    padding: 7px;
}

.label {
    margin: 3px !important;
}
</style>
