import { colors } from '@/utils/color'
import LabelTableList from './compontent/LabelTableList'
import CustomLabel from '@/components/MyComponents/CustomLabel';

export const minix = {
  components: { LabelTableList,CustomLabel },
  data() {
    return {
      colors: colors,
      temp: { name: '', color: 'teal' },
      query: {
        current: 1,
        limit: 10,
        keywords: null
      },
      list: [],
      total: 0,
      listLoading: false,
      dialogFormVisible: false,
      dialogStatus: '',
      labelRules: {
        name: [
          { required: true, message: '名称不能为空！', trigger: 'blur' }
        ],
        color: [
          { required: true, message: '颜色不能为空！', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 取消
    async cancel() {
      this.dialogFormVisible = false
      await this.restTemp()
    },

    dataChange(obj) {
      this.temp = obj
    },
    // 重置
    restTemp() {
      this.temp = { name: '', color: 'teal' }
    },
    // 查看标签分类blog
    selectTagName(tagName) {
      
    },

    // 处理
    handle(currentRow, oldCurrentRow) {
      
    },

    handleSearch(query) {
      this.query.current = 1
      this.query.limit = 10
      this.query.keywords = query.keywords
      this.getList()
    },

    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },

    handleUpdate(obj) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.temp = obj
      
    }
  }
}
