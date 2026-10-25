<template>
  <!-- 用户管理页面容器 -->
  <div id="userManagePage">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchParams" @finish="doSearch">
        <a-form-item label="账号">
          <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
      </a-form>
      <a-divider />
    <!--
      a-table：Ant Design 的数据表格组件
      :columns      列定义（有哪些列、表头叫什么、对应哪个字段）
      :data-source  表格数据（当前页的数据数组）
      :pagination   分页配置（当前页、每页条数、总条数）
      row-key       每一行的唯一标识字段，用 id 即可
      @change       翻页 / 改变每页条数时触发，用来重新请求数据
    -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      row-key="id"
      @change="doTableChange"
    >
      <!--
        #bodyCell 是“自定义单元格内容”的插槽
        解构出 column（当前列配置）和 record（当前这一行的数据）
        根据 column 的不同，给特定列渲染自定义内容（图片、标签、按钮等）
        没有匹配到的列，会按默认逻辑直接显示字段文本
      -->
      <template #bodyCell="{ column, record }">
        <!-- 头像列：用 a-image 渲染图片，宽度 120 -->
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>

        <!-- 角色列：admin 显示绿色“管理员”，其他显示蓝色“普通用户” -->
        <template v-else-if="column.dataIndex === 'userRole'">
          <a-tag v-if="record.userRole === 'admin'" color="green">管理员</a-tag>
          <a-tag v-else color="blue">普通用户</a-tag>
        </template>

        <!-- 创建时间列：用 dayjs 把时间格式化为 YYYY-MM-DD HH:mm:ss -->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <!-- 操作列：通过 key 判断，放编辑、删除按钮；a-space 给按钮之间加间距 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <!-- 编辑：把当前行完整数据 record 传给 doEdit，用于回填弹窗 -->
            <a-button type="primary" @click="doEdit(record)">编辑</a-button>
            <!-- 删除：只需要这一行的 id -->
            <a-button type="primary" danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!--
      编辑用户弹窗
      v-model:open        双向绑定弹窗显示/隐藏（editVisible 为 true 就弹出）
      title               弹窗标题
      :confirm-loading    确定按钮的 loading，防止重复提交
      @ok                 点击“确定”时触发提交
      （点右上角 X / 取消会自动把 open 置为 false，无需额外写 @cancel）
    -->
    <a-modal
      v-model:open="editVisible"
      title="编辑用户"
      :confirm-loading="editLoading"
      @ok="handleEditSubmit"
    >
      <!-- 编辑表单：layout="vertical" 让标签在输入框上方，更适合弹窗 -->
      <a-form :model="editForm" layout="vertical">
        <!-- 账号不允许修改：disabled 置灰只读，仅用来提示当前编辑的是哪个账号 -->
        <a-form-item label="账号">
          <a-input v-model:value="editForm.userAccount" disabled />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="头像链接">
          <a-input v-model:value="editForm.userAvatar" placeholder="请输入头像 URL" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editForm.userProfile" placeholder="请输入简介" :rows="3" />
        </a-form-item>
        <!-- 角色：下拉选择普通用户 / 管理员 -->
        <a-form-item label="角色">
          <a-select v-model:value="editForm.userRole">
            <a-select-option value="user">普通用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
// vue 提供的组合式 API：ref 定义响应式变量，reactive 定义响应式对象，
// computed 定义计算属性，onMounted 是组件挂载完成后的生命周期钩子
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import type { TableColumnsType } from 'ant-design-vue'
// dayjs 是一个时间处理库（ant-design-vue 已内置依赖），用来格式化时间
import dayjs from 'dayjs'
// 从接口文件中引入“分页查询用户”、“删除用户”、“更新用户”接口
import {deleteUser, listUserVoByPage, updateUser} from '@/api/userController'

// 表格列定义：title 是表头文字，dataIndex 对应数据字段名，key 用于自定义操作列
const columns: TableColumnsType = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 当前页的表格数据（UserVO 数组，默认为空）
const data = ref<API.UserVO[]>([])
// 数据总条数（用于分页器显示）
const total = ref(0)

// 搜索 / 分页条件，默认第 1 页、每页 10 条
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

/**
 * 拉取分页数据
 * 调用后端 POST /user/list/page/vo 接口，把当前页数据和总条数更新到表格
 */
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  // 有数据：更新表格数据和总条数
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    // 接口报错时给出提示
    message.error('获取数据失败，' + res.data.message)
  }
}

// 组件挂载完成后，立即请求一次数据，让表格进入页面就有内容
onMounted(() => {
  fetchData()
})

// 分页配置：用 computed 让它随着页码和总条数自动更新
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1, // 当前页码
    pageSize: searchParams.pageSize ?? 10, // 每页条数
    total: total.value, // 总条数
    showSizeChanger: true, // 允许切换每页条数
    showTotal: (total: number) => `共 ${total} 条`, // 显示总条数
  }
})

/**
 * 表格翻页 / 改变每页条数时触发
 * @param page 分页器传回的当前状态（current 当前页、pageSize 每页条数）
 */
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  // 更新页码后重新拉取数据
  fetchData()
}


// 查询框搜索功能
const doSearch = () => {
  // 搜索前重置页码为 1
  searchParams.pageNum = 1
  // 重新拉取数据
  fetchData()
}


/**
 * 删除指定用户
 * @param id 要删除的用户 id，来自点击那一行的 record.id（后端是雪花 id，数字类型）
 *
 * async 表示这是一个异步函数，内部可以用 await 等待接口返回；
 * id: number 表示该函数接收一个名为 id、类型为数字的参数（必须和后端 / 接口类型一致）
 */
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  // deleteUser 要求参数类型为 API.DeleteRequest = { id?: number }，所以 id 必须是 number
  const res = await deleteUser({ id })
  if (res.data.code === 1) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}


// ==================== 编辑用户相关 ====================
// 编辑弹窗是否显示（false 隐藏、true 显示）
const editVisible = ref(false)
// 弹窗“确定”按钮的 loading，防止重复提交
const editLoading = ref(false)
// 编辑表单数据：用 UserVO 类型，既包含需要展示的账号，也包含可修改的字段
const editForm = reactive<API.UserVO>({
  id: undefined,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 'user',
})

/**
 * 点击“编辑”按钮：把当前行数据回填到表单，并打开弹窗
 * @param record 当前这一行的用户数据（a-table 的 #bodyCell 插槽传出来的 record）
 */
const doEdit = (record: API.UserVO) => {
  // 把当前行的字段逐个赋值到表单，实现“回填”
  Object.assign(editForm, {
    id: record.id,
    userAccount: record.userAccount,
    userName: record.userName,
    userAvatar: record.userAvatar,
    userProfile: record.userProfile,
    userRole: record.userRole,
  })
  // 打开弹窗
  editVisible.value = true
}

/**
 * 提交编辑：调用 updateUser 接口，成功后关闭弹窗并刷新表格
 */
const handleEditSubmit = async () => {
  // 没有 id 说明数据异常，直接返回
  if (!editForm.id) {
    return
  }
  editLoading.value = true
  // 只提交后端更新接口需要的字段（UserUpdateRequest：id + 可改字段，不含账号）
  const res = await updateUser({
    id: editForm.id,
    userName: editForm.userName,
    userAvatar: editForm.userAvatar,
    userProfile: editForm.userProfile,
    userRole: editForm.userRole,
  })
  editLoading.value = false
  if (res.data.code === 1) {
    message.success('更新成功')
    // 关闭弹窗
    editVisible.value = false
    // 刷新表格，展示最新数据
    fetchData()
  } else {
    message.error('更新失败，' + res.data.message)
  }
}




</script>

<style scoped>
/* 给页面留出内边距，避免表格紧贴导航栏 */
#userManagePage {
  width: 1200px;
  padding: 16px;
}
</style>
