import Layout from '@/layout'

const countRouter = {
  path: '/count',
  component: Layout,
  redirect: '/count/visitor',
  meta: { title: '数据统计', noCache: true, icon: 'login' },
  children: [
    {
      path: 'visitor',
      component: () => import('@/views/visitor/index.vue'),
      meta: { title: '访问统计', noCache: true, icon: 'login' }
    }
  ]
}

export default countRouter
