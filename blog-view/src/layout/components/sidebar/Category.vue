<template>
<div class="ui segments m-toc toc-wrapper m-box">
    <div class="ui secondary segment"><i class="list ul icon"></i>分类</div>
    <div class="ui teal segment">
        <div class="ui middle selection list">
            <div class="item" v-for="(item,index) in categoryList" :key="`categoryName${index}`">
                <div class="left floated">
                    <router-link :to="`/category/${item.name}`" >{{item.name}}</router-link>
                </div>
                <div class="right floated">
                    <router-link :to="`/category/${item.name}`" >{{item.value}}</router-link>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import {
    getDashboardCategory
} from '@/api/home'

export default {
    name: "Category",
    data() {
        return {
            categoryList: []
        }
    },
    mounted () {
        this.getDashboardCategory()
    },
    methods: {
        getDashboardCategory() {
            getDashboardCategory().then(res => {
                if (res.code === 200) {
                    this.categoryList = res.result
                    
                }
            })
        }
    },

};
</script>

<style lang="scss" scoped>
.m-display-float-left {
    float: left;
}

.m-display-float-right {
    float: right;
}

a {
    color: rgba(1, 1, 1, .5)
}
</style>
