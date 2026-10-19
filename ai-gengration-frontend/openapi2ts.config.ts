//根据后端接口,生成前端请求
export default {
  //前端生成请求调用代码,需要在文件内引入请求库,路径为src/request.ts(请求库)
  requestLibPath: "import request from '@/request'",
  //后端的接口文档地址
  schemaPath: 'http://localhost:8123/api/v3/api-docs',
  //生成的代码目录
  serversPath: './src',
}
