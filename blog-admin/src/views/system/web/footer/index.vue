<template>
<div class="m-margin-tb-small">
    <MyCard title="标签设置">
        <FooterContent v-for="item in footerLabel" :item="item" :key="item.id" @deleteItem="deleteItem" @updateItem="updateItem" />
        <el-button type="primary" icon="el-icon-plus" size="mini" @click.prevent="createItem()">添加</el-button>
    </MyCard>
</div>
</template>

<script>
import FooterContent from '@/views/system/web/footer/FooterContent'
import MyCard from '@/components/MyComponents/MyCard'
import {
    delWebFooterLabel,
    updateWebFooterLabel
} from '@/api/system'

export default {
    name: 'WebFooter',
    components: {
        MyCard,
        FooterContent
    },
    props: {
        footerLabel: {
            type: Array,
            default: () => {
                return []
            }
        }
    },

    methods: {
        createItem() {
            const temp = {
                'id': Date.now(),
                'typeName': '',
                'typeColor': 'grey',
                'name': '',
                'color': 'rgb(115,110,110)',
                'url': '',
                'title': ''
            }
            this.footerLabel.push(temp)
        },

        updateItem(data) {
            updateWebFooterLabel(data).then(res => {
                if (res.code === 200) {
                    this.$notify.success(res.result)
                }
            })
        },

        deleteItem(id) {
            delWebFooterLabel(id).then(res => {
                if (res.code === 200) {
                    this.$notify.success('删除成功')
                    this.getData()
                }
            })
        }

    }

}
</script>
