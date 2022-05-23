<template>
<div>
    <el-popover placement="bottom" width="400" trigger="click" style="margin: 2em auto">
        <div class="ui grid divided">
            <div class="eight wide column">
                <div class="ui orange basic label">
                    <div class="image">
                        <div class="image-top">一毛是鼓励 一块是真爱</div>
                        <img :src="wechatPayment" class="ui rounded bordered image image-appreaction" />
                        <div class="image-bottom">微信</div>
                    </div>
                </div>
            </div>

            <div class="eight wide column">
                <div class="ui orange basic label">
                    <div class="image">
                        <div class="image-top">一毛是鼓励 一块是真爱</div>
                        <img :src="alipay" class="ui rounded bordered image image-appreaction" />
                        <div class="image-bottom">支付宝</div>
                    </div>
                </div>
            </div>
        </div>
        <el-button slot="reference" class="ui orange inverted circular button m-text-500">赞赏</el-button>
    </el-popover>
</div>
</template>

<script>
export default {
    name: "Appreciate",
    props: {
        wechatPayment: {
            type: String,
            default: "https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/img/202112/20211203093215.png",
        },
        alipay: {
            type: String,
            default: "https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/img/202112/20211203093231.png",
        },
    },
};
</script>

<style scoped>
.image-top {
    font-size: 10px;
    text-align: center;
    margin-bottom: 5px;
}

.image-bottom {
    font-size: 12px;
    text-align: center;
    margin-top: 5px;
}

.image-appreaction {
    width: 150px !important;
}
</style>
