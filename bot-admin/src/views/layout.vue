<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside class="layout-aside"
      :width="isCollapse ? '64px' : '250px'">
      <div class="layout-logo">
        <h1>系统</h1>
      </div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#545c64"
        class="layout-menu"
        default-active="/index"
        :collapse="isCollapse"
        text-color="#fff"
        @open="handleOpen"
        @close="handleClose"
        :router="true"
      >
        <el-menu-item index="/index">
          <el-icon><icon-menu /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/status">
          <el-icon><document /></el-icon>
          <span>状态</span>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon><setting /></el-icon>
          <span>Navigator Four</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="layout-main">
      <el-header class="main-header">
        <div class="header-left">
          <!-- 折叠按钮 -->
<!--          <div class="collapse-btn" @click="isCollapse = !isCollapse">-->
<!--            <el-icon>-->
<!--              <component :is="isCollapse ? 'Expand' : 'Fold'" />-->
<!--            </el-icon>-->
<!--          </div>-->
          <div class="page-header">
            <el-page-header>
              <template #breadcrumb>
                <el-breadcrumb separator="/">
                  <el-breadcrumb-item :to="{ path: './page-header.html' }">
                    homepage
                  </el-breadcrumb-item>
                  <el-breadcrumb-item>
                    <a href="./page-header.html">route 1</a>
                  </el-breadcrumb-item>
                  <el-breadcrumb-item>route 2</el-breadcrumb-item>
                </el-breadcrumb>
              </template>
              <template #content>
                <span class="text-large font-600 mr-3"> Title </span>
              </template>
            </el-page-header>
          </div>
          <!-- 面包屑导航 -->

        </div>
      </el-header>
      <el-main>
        <RouterView/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {
  Document,
  Menu as IconMenu,
  Location,
  Expand,
  Fold,
  Setting,
} from '@element-plus/icons-vue'
import {ref} from "vue";

let isCollapse = ref(false);
const handleOpen = (key, keyPath) => {
  console.log(key, keyPath)
}
const handleClose = (key, keyPath) => {
  console.log(key, keyPath)
}
</script>

<style lang="scss" scoped>
.layout-container {
  padding: 20px;
  background-color: #f5f7fa;
  height: 100vh;
  width: 100vw;
}
.layout-menu {
  height: 95%;
  border-right: none;
}
.layout-logo{
  height: 5%;
}
.layout-aside{
  height:  100%;
}
.header-left {
  display: flex;
  align-items: center;
  flex: 1;

  .collapse-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    margin-right: 16px;
    cursor: pointer;
    border-radius: 4px;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f5f5;
    }

    .el-icon {
      font-size: 20px;
      color: #666;
    }
  }

  .breadcrumb {
    font-size: 14px;
  }
}
</style>
