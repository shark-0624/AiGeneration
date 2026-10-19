<!--<template>-->
<!--  <input v-model="username" placeholder="请输入用户名" />-->
<!--  <input  placeholder="请输入用户名&#45;&#45;" />-->
<!--  <input type="checkbox" v-model="isFlag">-->
<!--  <p>isFlag: {{ isFlag }}</p>-->
<!--  <p>username: {{ username }}</p>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from 'vue'-->
<!--const isFlag = ref()-->
<!--const username = ref('')-->
<!--</script>-->



<!--<template>-->
<!--  &lt;!&ndash;<label> 用来给表单控件（如 input、checkbox）加文字说明。-->

<!--  它最大的作用是：点击文字也能选中/切换对应的控件。-->
<!--&ndash;&gt;-->
<!--  <label>-->
<!--    &lt;!&ndash;    isAgree为true/false，-->
<!--    是因为 v-model 用在 checkbox 上，而 checkbox 的 v-model 会把勾选状态（布尔值）写回变量。&ndash;&gt;-->
<!--    <input type="checkbox"  v-model="isAgree"/>-->
<!--    同意协议-->
<!--  </label>-->
<!--  <p>isAgree: {{ isAgree }}</p>-->
<!--</template>-->

<!--<script setup>-->
<!--import {ref} from 'vue'-->

<!--const isAgree = ref(false)-->
<!--</script>-->


<!--多个复选框：绑定数组-->
<!--<template>-->
<!--  <label>-->
<!--    <input type="checkbox" value="apple" v-model="fruits" />-->
<!--    苹果-->
<!--  </label>-->
<!--  <label>-->
<!--    <input type="checkbox" value="banana" v-model="fruits" />-->
<!--    香蕉-->
<!--  </label>-->
<!--  <p>fruits: {{ fruits }}</p>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from 'vue'-->
<!--const fruits = ref([])-->
<!--</script>-->

<!--下拉选择：单选绑定字符串，多选绑定数组-->
<!--<template>-->
<!--  <select v-model="city">-->
<!--    <option disabled value="">请选择</option>-->
<!--    <option value="bj">北京</option>-->
<!--    <option value="sh">上海</option>-->
<!--  </select>-->
<!--  <p>city: {{ city }}</p>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from 'vue'-->
<!--const city = ref('')-->
<!--</script>-->

<template>
  <select v-model="cities" multiple>
    <option disabled value="">请选择</option>-->
    <option value="bj">北京</option>
    <option value="sh">上海</option>
  </select>
  <p>cities: {{ cities }}</p>
</template>

<script setup>
import { ref } from 'vue'
const cities = ref([])
</script>
