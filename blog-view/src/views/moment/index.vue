<template>
<div>
    <div class="ui top attached segment" style="text-align: center">
        <h2 class="m-text-500">我的动态</h2>
    </div>
    <div class="ui attached segment m-padding-bottom-large">
        <div class="moments">
            <div class="moment" v-for="(moment, index) in momentList" :key="index">
                <div class="avatar">
                    <img :src="moment.avatar" />
                </div>
                <div class="ui card">
                    <div class="content m-top">
                        <span style="font-weight: 700">{{ moment.username }}</span>
                        <span class="right floated">{{ moment.createTime }}</span>
                    </div>
                    <div class="content typo" v-viewer v-html="moment.content"></div>
                    <div class="extra content">
                        <a class="right floated like" @click.prevent="likeMoment(moment)">
                            <i class="heart icon" :class="isLike(moment.momentId) ? 'like-color' : 'outline'"></i>
                            {{ moment.likes }}
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <div style="margin:10px auto">
            <MyPagination :get-list="getList" :totalPage="totalPage" />
        </div>
    </div>
</div>
</template>

<script>
import {
    getDateDiff
} from "@/utils/validate";
import {
    getStore,
    setSetStore
} from "@/utils/session";
import {
    getMomentList,
    likeMoment
} from "@/api/moment";
import {
    formatLink
} from "@/utils/link";

export default {
    name: "Moment",
    filters: {
        formatDateTime: function (value) {
            return getDateDiff(value);
        },
    },
    data() {
        return {
            momentList: [],
            momentIds: getStore("momentIds") ? getStore("momentIds") : [],
            current: 1,
            limit: 5,
            totalPage: 0,
            total: 0,
        };
    },

    computed: {
        isLike() {
            return function (id) {
                return this.momentIds.indexOf(id) !== -1;
            };
        },
    },

    watch: {
        momentIds(newValue) {
            setSetStore("momentIds", newValue);
        },
    },
    mounted() {
        this.getList(this.current);
    },
    methods: {
        getList(current) {
            this.current = current;
            getMomentList(this.current, this.limit).then((res) => {
                if (res.code === 200) {
                    
                    const {
                        records,
                        pages,
                        total
                    } = res.result;
                    // 添加链接target="_blank"
                    records.forEach((item) => {
                        item.content = formatLink(item.content);
                    });
                    this.momentList = records;
                    this.total = total;
                    this.totalPage = pages;
                    // 内容加载完成需要渲染代码高亮
                    this.$nextTick(() => {
                        Prism.highlightAll();
                    });
                }
            });
        },

        likeMoment(moment) {
            const {
                momentId
            } = moment;
            if (this.momentIds.indexOf(momentId) > -1) {
                this.$notify.warning("不可以重复点赞哦!");
                return;
            }
            likeMoment(moment)
                .then((res) => {
                    if (res.code === 200) {
                        this.$notify.success("点赞成功");
                        this.momentIds.push(momentId);
                        this.momentList.forEach((moment) => {
                            if (moment.momentId === momentId) {
                                return moment.likes++;
                            }
                        });
                    }
                })
                .catch(() => {
                    this.$notify.error("异常错误！");
                });
        },
    },
};
</script>

<style scoped>
.avatar {
    margin-left: -62.5px;
    float: left !important;
}

.avatar img {
    height: 45px;
    width: 45px;
    border-radius: 500px;
}

.moments {
    margin-left: 26px !important;
    padding-left: 40px !important;
    border-left: 1px solid #dee5e7 !important;
}

.moment {
    margin-top: 30px;
}

.moment:first-child {
    margin-top: 0 !important;
}

.card {
    width: 100% !important;
}

.card:before {
    border-width: 0 0 1px 1px !important;
    transform: translateX(-50%) translateY(-50%) rotate(45deg) !important;
    bottom: auto !important;
    right: auto !important;
    top: 22px !important;
    left: 0 !important;
    position: absolute !important;
    content: "" !important;
    background-image: none !important;
    z-index: 2 !important;
    width: 12px !important;
    height: 12px !important;
    transition: background 0.1s ease !important;
    background-color: inherit !important;
    border-style: solid !important;
    border-color: #d4d4d5 !important;
}

.content.m-top {
    padding: 10px 14px !important;
}

.content .right.floated {
    font-size: 12px !important;
}

.content.typo * {
    font-size: 14px !important;
}

.content.typo img {
    margin: auto !important;
}

/* 图片内容居中显示，文字向左开始 */
.ui.card.content img,
.ui.cards>.card.content img {
    display: "" !important;
    vertical-align: middle !important;
}

.extra.content {
    padding: 5px 14px !important;
}

.extra.content a {
    color: rgba(0, 0, 0, 0.7) !important;
    font-size: 12px !important;
}

.extra.content a:hover {
    color: red !important;
}

.extra.content .like-color {
    color: red !important;
}

.extra.content i {
    font-size: 12px !important;
}

.pagination {
    text-align: center;
    margin-top: 3em;
}

.privacy {
    background: repeating-linear-gradient(145deg,
            #f2f2f2,
            #f2f2f2 15px,
            #fff 0,
            #fff 30px) !important;
}
</style>
