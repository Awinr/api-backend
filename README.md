## 易联API

**一款api管理平台**

https://cdn.statically.io/gh/Awinr/picx-images-hosting@master/images/image-20231027182719236.55c8e0pdjo00.webp

1. 从场景上说，API开放平台是一个提供API调用的平台，用户可以使用接口，管理员可以发布、下线接口、接入接口，以及可视化接口的调用情况、数据。
2. 从架构上来说，API开放平台分为6个模块，分别为：

1. 1. api-backend：负责用户和接口管理功能
   2. api-gateway：负责API网关的路由转发、统一鉴权、统一业务处理、访问控制，流量染色等，提高安全性的同时、便于系统的开发维护。
   3. api-interface:提供模拟接口的功能。
   4. api-common：抽取统一方法、实体类在多个模块项目中复用，减少重复编写
   5. api-sdk：提供接口定制化的SDK调用，给用户提供简单，快捷，高效的接口调用体验
   6. api-parent：以上所有模块的父模块，对其进行统一管理

管理员创建发布接口后保存到数据库中，用户调用接口，会根据客户端提供的定制化SDK调用，请求到 gateway网关中，进行用户的鉴权以及路由转发，以及调用统计等，得到数据库中对应数据资源的模拟接口。