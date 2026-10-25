<template>
  <!-- 注册页整体容器：通过 #userRegisterPage 限制最大宽度并居中 -->
  <div id="userRegisterPage">
    <!-- 页面标题 -->
    <h2 class="title">AI 应用生成平台 - 用户注册</h2>
    <!-- 标题下方的描述文字 -->
    <div class="desc">可以不写一行代码，让AI帮你生成完整应用</div>

    <!--
      a-form：Ant Design 的表单组件
      :model="formState"  绑定表单数据对象
      @finish="handleSubmit"  当所有校验规则通过、点击提交时，会自动触发 handleSubmit
    -->
    <a-form :model="formState" name="register" autocomplete="off" @finish="handleSubmit">
      <!-- 账号输入项：rules 是下方 script 里定义的校验规则 -->
      <a-form-item name="userAccount" :rules="accountRules">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <!-- 密码输入项：a-input-password 会自动带“显示/隐藏密码”的小眼睛 -->
      <a-form-item name="userPassword" :rules="passwordRules">
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <!-- 确认密码项：除了必填、长度，还要和上面的密码一致 -->
      <a-form-item name="checkPassword" :rules="checkPasswordRules">
        <a-input-password v-model:value="formState.checkPassword" placeholder="请再次输入密码" />
      </a-form-item>

      <!-- 底部提示：已经有账号的用户可以点击跳到登录页 -->
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">去登录</RouterLink>
      </div>

      <!-- 提交按钮：html-type="submit" 表示点击后触发表单的 @finish 校验与提交 -->
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { userRegister } from '@/api/userController'

// 表单数据对象，类型用接口文档生成的 API.UserRegisterRequest，包含 3 个字段
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 路由实例，用于注册成功后做页面跳转
const router = useRouter()

// 账号校验规则：必填 + 至少 4 位（要和后端校验保持一致）
const accountRules: Rule[] = [
  { required: true, message: '请输入账号' },
  { min: 4, message: '账号不能小于 4 位' },
]

// 密码校验规则：必填 + 至少 8 位
const passwordRules: Rule[] = [
  { required: true, message: '请输入密码' },
  { min: 8, message: '密码不能小于 8 位' },
]

// 确认密码校验规则：在上面的基础上，增加“两次密码必须一致”的自定义校验
const checkPasswordRules: Rule[] = [
  { required: true, message: '请再次输入密码' },
  { min: 8, message: '密码不能小于 8 位' },
  {
    // 自定义校验器：_rule 是规则本身，value 是当前输入框的值
    validator: (_rule, value) => {
      // 没填写，或者和上面的密码相同，就校验通过
      if (!value || formState.userPassword === value) {
        return Promise.resolve()
      }
      // 否则校验失败，提示两次密码不一致
      return Promise.reject(new Error('两次输入的密码不一致'))
    },
    // 输入框失焦时触发该校验
    trigger: 'blur',
  },
]

/**
 * 提交注册表单
 * 注意：这个方法只有在表单所有校验规则通过后，才会被 @finish 调用
 * @param values 用户填写的表单数据（就是 formState）
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  // 调用后端注册接口：POST /user/register
  const res = await userRegister(values)
  // 后端统一返回 BaseResponse，成功时 code 为 1，data 为新用户 id
  if (res.data.code === 1 && res.data.data) {
    // 注册成功：提示用户，并自动跳转到登录页
    message.success('注册成功，请登录')
    router.push({
      path: '/user/login',
      // replace: true 表示替换当前历史记录，避免点浏览器“后退”又回到注册页
      replace: true,
    })
  } else {
    // 注册失败（比如账号重复、参数不合规）：用后端返回的 message 提示
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<style scoped>
/* 注册卡片整体限制宽度并水平居中，样式和登录页保持一致 */
#userRegisterPage {
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
