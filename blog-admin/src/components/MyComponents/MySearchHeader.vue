<template>
<el-row :gutter="5">
    <el-col :span="4" :xs="24" v-if="showPre" class="m-el-col">
        <slot name="pre" />
    </el-col>
    <el-col :span="4" :xs="24" v-if="showSearchInput" class="m-el-col">
        <el-input v-model="query.keywords" :placeholder="placeholder" size="small" clearable class="w-100" @keyup.enter.native="handleSearch" />
    </el-col>
    <el-col :span="8" :xs="24" v-if="showTimeButton" class="m-el-col">
        <el-date-picker v-if="showTimeButton" v-model="query.date" class="w-100" :picker-options="pickerOptions" :value-format="type==='daterange'?'yyyy-MM-dd':'yyyy-MM-dd HH:mm:ss'" :type="type" :style="{width: type==='daterange'?'260px':'350px'}" range-separator="至" size="small" start-placeholder="开始日期" end-placeholder="结束日期" @blur="handleSearch" />
    </el-col>
    <el-col :span="4" :xs="24" class="m-el-col" v-if="showSearchButton || showCreateButton || showSlot">
        <el-row type="flex" :gutter="4">
            <el-col :span="spanShow" :xs="xsShow" v-if="showSearchButton">
                <el-button class=" w-100" type="primary" icon="el-icon-search" size="small" @click.prevent="handleSearch">
                    搜索
                </el-button>
            </el-col>
            <el-col :span="spanShow" :xs="xsShow" v-if="showCreateButton">
                <el-button class="w-100" type="primary" icon="el-icon-plus" size="small" @click="handlerCreate">
                    添加
                </el-button>
            </el-col>
            <el-col :span="spanShow" :xs="xsShow" v-if="showSlot">
                <slot />
            </el-col>
        </el-row>
    </el-col>

</el-row>
</template>

<script>
export default {
    name: "MySearchHeader",
    props: {
        showPre: {
            type: Boolean,
            default: true,
        },

        /* 默认关键词*/
        placeholder: {
            type: String,
            default: "关键词...",
        },

        type: {
            type: String,
            default: "daterange",
        },

        /* 是否显示搜索输入框*/
        showSearchInput: {
            type: Boolean,
            default: true,
        },

        /* 是否显示时间组件*/
        showTimeButton: {
            type: Boolean,
            default: true,
        },
        /* 是否显示搜索按钮*/
        showSearchButton: {
            type: Boolean,
            default: true,
        },

        /* 是否显示添加按钮*/
        showCreateButton: {
            type: Boolean,
            default: true,
        } /* 是否显示添加按钮*/ ,

        /* 是否失去焦点时候触发搜索*/
        activeBlur: {
            type: Boolean,
            default: true,
        },
        /* 是否失去焦点时候触发搜索*/
        showSlot: {
            type: Boolean,
            default: false,
        },

        /* 搜索条件*/
        query: {
            type: Object,
            default: () => {
                return {
                    current: 1,
                    limit: 10,
                    keywords: "",
                    start: "",
                    end: "",
                };
            },
        },
    },
    computed: {
        isShowCol() {
            return this.showSearchButton && this.showCreateButton ? 12 : 24
        },

        spanShow() {

            if (this.showCreateButton && this.showSearchButton && this.showSlot) {
                return 8
            }
            if (this.showCreateButton && this.showSearchButton && this.showSlot == false ||
                this.showCreateButton && this.showSlot && this.showSearchButton == false ||
                this.showSlot && this.showSearchButton && this.showCreateButton == false
            ) {
                return 12
            }
            return 12
        },
        xsShow() {

            if (this.showCreateButton && this.showSearchButton && this.showSlot) {
                return 8
            }
            if (this.showCreateButton && this.showSearchButton && this.showSlot == false ||
                this.showCreateButton && this.showSlot && this.showSearchButton == false ||
                this.showSlot && this.showSearchButton && this.showCreateButton == false
            ) {
                return 12
            }
            return 24
        }

    },
    data() {
        return {
            pickerOptions: {
                shortcuts: [{
                        text: "最近三天",
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 3);
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: "最近一周",
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: "最近一个月",
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: "最近三个月",
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: "最近半年",
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
                            picker.$emit("pick", [start, end]);
                        },
                    },
                ],
            },
        };
    },
    created() {
        if (this.type !== "daterange") {
            this.createData();
        }
    },

    methods: {
        /* 添加事件*/
        handlerCreate() {
            this.$emit("handleCreate");
        },

        /* 搜索事件*/
        handleSearch() {
            try {
                if (this.query.date && this.query.date[0] && this.query.date[1]) {
                    this.query.start = this.query.date[0];
                    this.query.end = this.query.date[1];
                }
            } catch (e) {
                this.query.start = null;
                this.query.end = null;
            } finally {
                
                this.$emit("handleSearch", this.query);
            }
        },
        cancel() {
            /* 不触发事件，防止报错*/
        },
        createData() {
            this.pickerOptions.shortcuts = [{
                    text: "最近一小时",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 1);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近三小时",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 3);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近十二小时",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 12);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近一天",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 1);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近三天",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 3);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近一周",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近一个月",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                        picker.$emit("pick", [start, end]);
                    },
                },
                {
                    text: "最近三个月",
                    onClick(picker) {
                        const end = new Date();
                        const start = new Date();
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                        picker.$emit("pick", [start, end]);
                    },
                },
            ];
        },
    },
};
</script>

<style scoped>
.m-el-col {
    margin: 5px !important;
}

.w-100 {
    width: 100% !important;
}
</style>
