<template>
<!--  a-layout-footer 表示页面底部区域-->
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
<!--  target="_blank"      表示在 新标签页或新窗口 中打开链接。-->
<!--        _self：当前窗口打开，默认值。 _blank：新标签页打开。-->
        <a
          href="https://www.codefather.cn"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          编程导航原创项目 by 程序员shark
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: #efefef;
  text-align: center;
  padding: 20px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}
</style>
