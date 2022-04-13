import Layout from '@/layout'

const systemRouter = {
  path: '/sys',
  component: Layout,
  redirect: '/sys/setting',
  meta: {
    title: '网站设置',
    icon: 'system'
  },
  children: [
    {
      path: 'setting',
      name: 'WebSetting',
      component: () => import('@/views/system/web/index.vue'),
      meta: {
        title: '基本设置',
        noCache: true,
        icon: 'web'
      }
    },
    {
      path: 'repo',
      name: 'Repo',
      component: () => import('@/views/system/repo/index.vue'),
      meta: {
        title: '图床',
        noCache: true,
        icon: 'clipboard'
      }
    },
  ]
}
export default systemRouter
