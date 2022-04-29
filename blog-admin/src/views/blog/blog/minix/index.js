import {
  getCategoryList,
  createCategory
} from '@/api/category'
import {
  getAllTagList,
  createTag
} from '@/api/tag'
import {
  createVditor
} from '@/plugins/CreateVditor'
import ElDragSelect from '@/components/DragSelect'
import {
  randomColor
} from '@/utils/color'
import {
  mapGetters
} from 'vuex';

export const blogMinix = {
  components: {
    ElDragSelect
  },
  data() {
    return {
      descriptionVditor: '',
      contentVditor: '',
      isShowPassword: false,
      listLoading: true,
      categoryList: [],
      tagList: [],
      ids: [],
      blog: {
        title: "",
        imgUrl: "",
        content: "",
        description: "",
        top: 0, // 是否置顶 不置顶
        commentEnabled: true, // 开启评论
        publish: true, // 默认发布
        appreciation: false, // 开启赞赏
        secrecy: false, // 是否保密 默认不开启公开的
        password: "", // 密码
        cid: 1,
        tagIds: [],
        views: 0,
      },
      cacheKey:'wuxin_blog_isCache',
      isCache: false
    }
  },
  computed: {
    ...mapGetters(['roles', 'userId'])
  },

  methods: {
    // 描述
    initVditor() {
      let that = this
      that.descriptionVditor = createVditor('vditor-description', 400, that.isCache)
      that.contentVditor = createVditor('vditor-content', 500, that.isCache)
      that.getLabelList()
    },
    getLabelList() {
      getCategoryList().then(res => {
        this.categoryList = res.result
      })
      getAllTagList().then(res => {
        this.tagList = res.result
      })
    },

    showPassword(userId) {
      if (this.roles.indexOf('root') !== -1 || userId === this.userId) {
        this.isShowPassword = !this.isShowPassword
      } else {
        this.$message.error('密码查看失败！仅对root用户和作者展示！')
      }
    },
    // 清空
    clear() {
      this.contentVditor.setValue('')
      this.descriptionVditor.setValue('')
    },

    validTag(id) {
      const arr = this.tagList.filter((item) => {
        return item.tagId === id;
      });

      if (arr.length === 0) {
        let tag = {
          name: id,
          color: randomColor(),
        };

        createTag(tag).then((res) => {
          if (res.code === 200) {
            this.ids.push(parseInt(res.tagId));
          }
        });
      } else {
        this.ids.push(id)
      }


    },

    validCategory(cid) {
      const arr = this.categoryList.filter((item) => {
        return item.cid === cid;
      });

      if (arr.length === 0) {
        createCategory({
          'name': cid,
          'color': randomColor(),
        }).then((res) => {
          this.blog.cid = res.cid
        });
      }


    },


    updateCahce(){
      let that = this
      window.localStorage.setItem(that.cacheKey,that.isCache)
      if(this.isCache){
        this.$message.success('本地缓存开启成功！')
      }else{
        this.$message.warning('本地缓存成功关闭！')
      }
      console.log(this.isCache);
    }


  },
  mounted() {
    // 是否缓存
    let isCache = window.localStorage.getItem(this.cacheKey)
    if (isCache === true || isCache==='true') {
      this.isCache = true
    }
    this.initVditor()

  }
}
