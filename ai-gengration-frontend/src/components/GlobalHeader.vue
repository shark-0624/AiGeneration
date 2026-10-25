<template>
  <!-- 顶部导航栏（整个页面只有这一个根节点） -->
  <a-layout-header class="header">
    <!-- a-row 不换行，保证 Logo / 菜单 / 用户区 始终在同一行 -->
    <a-row :wrap="false">
      <!-- 左侧：Logo 和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">shark应用生成</h1>
          </div>
        </RouterLink>
      </a-col>

      <!-- 中间：导航菜单，flex:auto 占满中间剩余空间 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>

      <!-- 右侧：用户操作区域（登录 / 已登录的头像下拉） -->
      <a-col>
        <div class="user-login-status">
          <!--
            已登录：把“头像 + 昵称”作为 a-dropdown 的触发器，
            点击后在它正下方弹出 overlay 菜单（退出登录）
          -->
          <a-dropdown v-if="loginUserStore.loginUser.id">
            <!-- 默认插槽里的第一个元素就是下拉触发器 -->
            <a-space class="user-info">
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              <span>{{ loginUserStore.loginUser.userName ?? '未设置昵称' }}</span>
            </a-space>
            <!-- #overlay 插槽：点击触发器后弹出的浮层菜单 -->
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>

          <!-- 未登录：显示登录按钮，点击用路由跳转（不刷新整页） -->
          <a-button v-else type="primary" @click="router.push('/user/login')">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import {computed, h, ref} from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'

// 路由实例、全局登录用户状态（先声明，后面的方法里要用）
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 当前选中菜单的 key
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，让菜单高亮项跟随当前页面
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

function HomeOutlined() {

}

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.codefather.cn', target: '_blank' }, '编程导航'),
    title: '编程导航',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组 通过computed 动态更新
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))



// 处理菜单点击：站内路由才跳转，外链（others）不处理
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
}

/**
 * 退出登录
 * 调用后端注销接口 -> 清空本地登录态 -> 跳转到登录页
 */
const doLogout = async () => {
  const res = await userLogout()
  // 后端统一返回 BaseResponse，成功时 code 为 1
  if (res.data.code === 1) {
    // 把全局登录用户重置为“未登录”状态
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    // 退出后跳转到登录页
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.header {
  background: #fff;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1890ff;
}

/* 右侧用户区：内容靠右对齐并垂直居中 */
.user-login-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 64px;
  white-space: nowrap;
}

/* 下拉触发器：鼠标移上去显示小手，提示可点击 */
.user-info {
  cursor: pointer;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}
</style>
