<template>
<div>
    <MySearchHeader type="datetimerange" :showPre="false" :showSlot="false" :show-create-button="false" :query="query" @handleSearch="handleSearch">
    </MySearchHeader>
    <el-table ref="multipleTable" height="400" :data="list" size="large" fit>
        <el-table-column label="序号" align="center" width="55" prop="id" type="index" />
        <el-table-column label="标识" align="center" width="200">
            <template slot-scope="{ row }">
                <el-tooltip :content="row.uuid">
                    <span class="m-message" style="cursor: pointer" @click="idSearch(row.uuid)">{{ row.uuid ? row.uuid : "未知" }}</span>
                </el-tooltip>
            </template>
        </el-table-column>
        <el-table-column label="ip" align="center" width="150" prop="ip">
        </el-table-column>
        <el-table-column label="来源" align="center" width="150" prop="address">
        </el-table-column>
        <el-table-column label="浏览器" align="center" width="150" prop="browser">
        </el-table-column>
        <el-table-column label="os" align="center" width="150" prop="os">
        </el-table-column>
        <el-table-column label="最后访问时间" align="center" width="150" prop="updateTime">
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
            <template slot-scope="{ row }">
                <el-button type="text" icon="el-icon-edit" @click="notOnLine">禁止</el-button>
                <el-popconfirm title="确认删除记录？该操作不可恢复！" @confirm="handleDelete(row.id)">
                    <el-button type="text" slot="reference" icon="el-icon-delete">删除</el-button>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>

    <!-- 分页-->
    <MyPagination v-show="total > 0" :total="total" :page.sync="query.current" :limit.sync="query.limit" @pagination="getList" />
</div>
</template>

<script>
import {
    getVisitorList,
    deleteVisitorById
} from "@/api/visitor.js";
import {
    query
} from "@/mixin/query.js";
export default {
    name: "Visitor",
    mixins: [query],

    mounted() {
        this.getList();
    },

    methods: {
        getList() {
            getVisitorList(this.query).then((res) => {
                this.list = res.result.records;
                this.total = res.result.total;
            });
        },

        idSearch(id) {
            this.query.keywords = id;
            this.handleSearch(this.query);
        },

        handleSearch(query) {
            this.query = query;
            this.getList();
        },

        handleDelete(id) {
            deleteVisitorById(id).then((res) => {
                this.$message.success(res.result);
            });
        },

        notOnLine() {
            this.$message.warning("该操作还未上线！");
        },
    },
};
</script>
