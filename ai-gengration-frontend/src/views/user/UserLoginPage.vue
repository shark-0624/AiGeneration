<template>
  <!-- 登录页整体容器：通过 #userLoginPage 限制最大宽度并居中 -->
  <div id="userLoginPage">
    <!-- 页面标题 -->
    <h2 class="title"> AI 应用生成平台 - 用户登录</h2>
    <!-- 标题下方的描述文字 -->
    <div class="desc">可以不写一行代码，让AI帮你生成完整应用</div>

    <!--
      a-form：Ant Design 的表单组件
      :model="formState"  绑定表单数据对象
      @finish="handleSubmit"  当所有校验规则通过、点击提交时，会自动触发 handleSubmit
    -->
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <!-- 账号输入项：required 表示必填 -->
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <!-- 密码输入项：a-input-password 会自动带“显示/隐藏密码”的小眼睛；min 8 表示至少 8 位 -->
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <!-- 底部提示：没有账号的用户可以点击跳到注册页 -->
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>

      <!-- 提交按钮：html-type="submit" 表示点击后触发表单的 @finish 校验与提交 -->
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>


<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogin } from '@/api/userController'

// 表单数据对象，类型用接口文档生成的 API.UserLoginRequest，包含账号和密码
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

// 路由实例，用于登录成功后跳转到首页
const router = useRouter()
// 全局登录用户状态（Pinia），用来保存当前登录用户的信息
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * 注意：这个方法只有在表单所有校验规则通过后，才会被 @finish 调用
 * @param values 用户填写的表单数据（就是 formState）
 */
const handleSubmit = async (values: API.UserLoginRequest) => {
  // 调用后端登录接口：POST /user/login
  const res = await userLogin(values)
  // 后端统一返回 BaseResponse，成功时 code 为 1，data 为登录用户信息
  if (res.data.code === 1 && res.data.data) {
    // 登录成功后，重新拉取一次登录用户信息，存到全局状态中
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      // replace: true 表示替换当前历史记录，避免点浏览器“后退”又回到登录页
      replace: true,
    })
  } else {
    // 登录失败（比如账号密码错误）：用后端返回的 message 提示
    message.error('登录失败，' + res.data.message)
  }
}
</script>


<style scoped>
/* 登录卡片整体限制宽度并水平居中 */
#userLoginPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

</style>
