<template>
<!-- <div class="site" :class="{ 'm-animation-welcome': loading }" :style="styleSetting"> -->
<div class="site" :style="styleSetting">
    <MyNav />
    <!--首页大图只在首页显示-->
    <div class="m-mobile-hide">
        <HeaderIndex v-if="$route.name === 'Index'" />
    </div>
    <!--页面设置-->
    <PageSetting />

    <div class="main">
        <div class="m-padded-tb-big">
            <div class="ui container">
                <div class="ui stackable grid">
                    <!--左侧-->
                    <div class="three wide column m-mobile-hide">
                        <transition enter-active-class="animate__animated animate__fadeInLeft" leave-active-class="animate__animated animate__fadeOutLeft">
                            <div v-if="settingState.focusMode">
                                <Blogger />
                            </div>
                        </transition>
                    </div>
                    <div class="ten wide computer sixteen wide mobile column">
                        <keep-alive include="Index">
                            <router-view />
                        </keep-alive>
                    </div>
                    <div class="three wide column m-mobile-hide">
                        <transition-group enter-active-class="animate__animated animate__fadeInRight" leave-active-class="animate__animated animate__fadeOutRight">
                            <div v-if="settingState.focusMode" :key="1">
                                <Tag />
                                <Category />

                            </div>
                            <!-- <div :key="2">
                                <Tocbot v-if="checkRoute('Blog')" />
                            </div> -->
                        </transition-group>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!--音乐插件-->
    <MyPlayer />
    <!-- 回到顶部按钮-->
    <el-backtop style="box-shadow: none; background: none">
        <svg t="1638758260552" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4084" width="48" height="48">
            <path d="M1009.19461 5.118447a32.054274 32.054274 0 0 0-35.125341 0.255922l-959.708789 639.805859a31.830341 31.830341 0 0 0-14.043738 29.942914 31.830341 31.830341 0 0 0 19.929952 26.360002l250.292052 100.161607 117.692288 205.953506a31.990293 31.990293 0 0 0 27.415681 16.123108H415.998608c11.228593 0 21.657428-5.950194 27.415681-15.547283l66.443839-110.782384 310.14589 124.026365a31.734371 31.734371 0 0 0 27.543642-1.855437c8.445437-4.734563 14.23568-13.05204 15.867185-22.617137l159.951465-959.708788A32.054274 32.054274 0 0 0 1009.19461 5.118447zM100.446359 664.662317L841.821398 170.3803 302.784962 747.389214c-2.847136-1.695486-5.374369-3.934806-8.509418-5.182427l-193.829185-77.54447z m225.627536 105.216073l-0.223932-0.319903L931.842082 120.955298 415.230841 925.895049l-89.156946-156.016659z m480.750122 177.322194l-273.229092-109.278841a63.564712 63.564712 0 0 0-19.929952-3.806845L934.401305 181.896806l-127.577288 765.303778z" fill="#1296db" p-id="4085"></path>
        </svg>
    </el-backtop>
    <!--底部-->
    <FooterIndex :blog-list="newBlogList" :site="site" :label-list="footerLabel" />
</div>
</template>

<script>
import MyNav from "@/layout/components/Nav";
import { getAdminUserInfo, getRandomBlog, webSite } from "@/api/home.js";
import FooterIndex from "@/layout/components/Footer";
import HeaderIndex from "@/layout/components/sidebar/Header";
import Tag from "@/layout/components/sidebar/Tag";
import Category from "@/layout/components/sidebar/Category";
import PageSetting from "@/layout/components/sidebar/PageSetting";
import RandomBlog from "@/layout/components/sidebar/RandomBlog";

import { mapActions, mapGetters } from "vuex";
import {
  settingBackgroundColor,
  settingBackgroundImageUrl,
} from "@/utils/setting";
import Blogger from "@/layout/components/sidebar/Blogger";
import MyPlayer from "@/layout/components/sidebar/MyPlayer";
import {
  SET_CLIENT_SIZE,
  SET_SITE_INFO,
  SAVE_SETTING,
  SET_CATEGORY_LIST,
  SET_ADMIN_USER,
  SET_TAG_LIST,
} from "../store/mutations-type";
import Tocbot from "@/layout/components/sidebar/Tocbot";
import { defaultSetting } from "@/utils/setting.js";
import { setSetStore, getStore } from "@/utils/session";

export default {
  name: "LayoutIndex",
  components: {
    Tocbot,
    MyPlayer,
    Blogger,
    RandomBlog,
    PageSetting,
    Tag,
    Category,
    HeaderIndex,
    FooterIndex,
    MyNav,
  },
  data() {
    return {
      randomBlogList: [],
      footerLabel: [],
      newBlogList: [],
      loading: false,
      site:{}
    };
  },
  watch: {
    settingState: {
      immediate: true,
      deep: true,
      handler() {

        // 对象深度监听中，发生该改变保存到本地
        this.$store.commit("setting/" + SAVE_SETTING, this.settingState);
      },
    },
  },
  computed: {
    ...mapGetters(["settingState"]),
    // 背景设置
    styleSetting() {
      const { nightMode } = this.settingState;
      const { image, isShowImage, color } = this.settingState.background;
      return {
        backgroundColor: settingBackgroundColor(color, nightMode),
        backgroundImage: settingBackgroundImageUrl(isShowImage, image),
      };
    },

    checkRoute() {
      return function (name) {
        return this.$route.name === name;
      };
    },
  },

  mounted() {
    // 初始化获取浏览器信息
    this.init();
    this.initData();
    // 代码高亮插件
    setTimeout(() => {
      Prism.highlightAll();
    }, 500);
  },

  methods: {
    ...mapActions("setting", ["saveSetting", "getSetting"]),
    init() {
    //   this.$store.state.setting = getStore(SAVE_SETTING) ? getStore(SAVE_SETTING) : defaultSetting;
      this.loading = false;
      this.$store.commit("setting/" + SET_CLIENT_SIZE, {
        clientWidth: document.body.clientWidth,
        clientHeight: document.body.clientHeight,
      });
      // 保持页面可视化大小
      window.onresize = () => {
        this.$store.commit("setting/" + SET_CLIENT_SIZE, {
          clientWidth: document.body.clientWidth,
          clientHeight: document.body.clientHeight,
        });
      };

      // 页面刷新前保持state中数据
      if (getStore("store")) {
        this.$store.replaceState(
          Object.assign({}, this.$store.state, getStore("store"))
        );
      }

      //   页面刷新时将state数据存储到sessionStorage中
      window.addEventListener("beforeunload", () => {
        setSetStore("store", this.$store.state);
      });

      //   ios端渲染问题
      window.addEventListener("pagehide", () => {
        setSetStore("store", this.$store.state);
      });

    //   页面加载完成
      this.$nextTick(() => {
        this.loading = true;
      });
    },

    initData() {
      webSite().then((res) => {
        if (res.code === 200) {
          const { tagList, site, categoryList, newBlogList, footerLabelList } = res;
          this.newBlogList = newBlogList;
          this.footerLabel = footerLabelList;
          this.site = site
          this.$store.commit("index/" + SET_SITE_INFO, site);
          this.$store.commit("index/" + SET_TAG_LIST, tagList);
          this.$store.commit("index/" + SET_CATEGORY_LIST, categoryList);
        }
      });

      getAdminUserInfo().then((res) => {
        if (res.code === 200) {

          this.$store.commit("index/" + SET_ADMIN_USER, res);
        }
      });
    },

    randomBlog() {
      getRandomBlog().then((res) => {
        if (res.code === 200) {
          this.randomBlogList = res.result;
        }
      });
    },
  },
};
</script >

<style scoped>
.site {
    display: flex;
    min-height: 100vh;
    flex-direction: column;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-position: center;
    background-size: cover;
    background-color: #e86161;
}

.main {
    margin-top: 40px;
    flex: 1;
}

.main .ui.container {
    width: 1400px !important;
    margin-left: auto !important;
    margin-right: auto !important;
}

.ui.grid .three.column {
    padding: 0;
}

.ui.grid .ten.column {
    padding-top: 0;
}

.m-display-none {
    display: none !important;
}
</style>
