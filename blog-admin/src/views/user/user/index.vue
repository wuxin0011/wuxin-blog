<template>
<!-- 表格 -->
<div class="app-container">
    <MySearchHeader :show-pre="false" :show-create-button="false" @handleSearch="handleFilter" />
    <el-table v-loading="listLoading" :data="userList" fit highlight-current-row class="m-table" height="400">
        <el-table-column label="序号" prop="id" align="center" width="100" type="index">
        </el-table-column>
        <el-table-column label="用户昵称" width="200" align="center">
            <template slot-scope="{ row }">
                <MyUserLink :username="row.nickname" />
            </template>
        </el-table-column>

        <el-table-column label="头像" width="100" align="center">
            <template slot-scope="{ row }">
                <MyImage :image="row.avatar" />
            </template>
        </el-table-column>

        <el-table-column label="邮箱" align="center">
            <template slot-scope="{ row }">
                <span class="link-type" @click="handleUpdateUser(row)">{{
            row.email
          }}</span>
            </template>
        </el-table-column>

        <el-table-column label="注册日期" width="200" align="center" prop="createTime">
        </el-table-column>
        <el-table-column label="操作" align="center">
            <template slot-scope="{ row, $index }">
                <el-button type="success" icon="el-icon-user" @click="notOnLine" circle />
                <el-button type="primary" icon="el-icon-edit" circle @click="handleUpdateUser(row)" />
                <el-button type="danger" icon="el-icon-delete" circle @click="handleDelete(row.userId, $index)" />
            </template>
        </el-table-column>
    </el-table>

    <MyPagination v-show="total > 0" :total="total" :page.sync="query.current" :limit.sync="query.limit" @pagination="getList" />

    <el-dialog title="编辑用户信息" :visible.sync="dialogFormVisible" style="min-width: 500px">
        <UserEdit :temp="temp" @cancelEdit="cancelEdit" @getList="getList" />
    </el-dialog>
</div>
</template>

<script>
import UserEdit from "@/views/user/component/UserEdit";
import MyImage from "@/components/MyComponents/MyImage";
import MyUserLink from "@/components/MyComponents/MyUsernameLink";
import {
    delUser,
    updateUser,
    getUserList
} from "@/api/user";
import {
    query
} from "@/mixin/query";

export default {
    name: "UserList",
    components: {
        MyUserLink,
        MyImage,
        UserEdit,
    },
    mixins: [query],
    data() {
        return {
            userList: [],
            total: 0,
            listLoading: true,
            dialogFormVisible: false,
            temp: {},
        };
    },
    mounted() {
        this.getList();
    },
    methods: {
        getList() {
            this.listLoading = true;
            getUserList(this.query).then((res) => {
                this.userList = res.result.records;
                this.total = res.result.total;
            });

            this.listLoading = false;
        },

        update() {
            this.$message.success("修改用户操作还未上线哦！");
        },

        // 修改用户信息处理
        handleUpdateUser(user) {
            this.temp = user; // 将用户信息赋值给对话框
            this.dialogFormVisible = true; // 显示对话框
        },

        notOnLine() {
            this.$message.warning("禁言功能还未上线！");
        },

        cancelEdit() {
            this.dialogFormVisible = false;
        },

        handleDelete(userId, index) {
            delUser(userId).then(() => {
                this.$message.success(res.result);
                this.userList.splice(index, 1);
            });
        },
    },
};
</script>
