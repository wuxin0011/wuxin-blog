const path = require('path')
module.exports = {
    configureWebpack: {
        resolve: {
            // alias: {
            //     'assets': '@/assets',
            //     'common': '@/common',
            //     'style': '@/style',
            //     'components': '@/components',
            //     'network': '@/network',
            //     'configs': '@/configs',
            //     'views': '@/views',
            //     'plugins': '@/plugins',
            // }
            alias: {
                '@': path.resolve(__dirname, './src'),
            }
        },

    },

    // devServer: {                //记住，别写错了devServer
    //     port: 80,				//设置本地默认端口
    //     proxy: {                //设置代理，必须填
    //         '/': {           //设置拦截器  拦截器格式=>斜杠+拦截器名字，名字可以自己定
    //             // target: 'http://1.117.46.114:8888',    //代理的目标地址
    //             target: 'http://wuxin-api:8888',    //代理的目标地址
    //             changeOrigin: true,                 //是否设置同源，输入是的
    //             pathRewrite: {                      //路径重写
    //                 '/wuxin-api': ''                      //选择忽略拦截器里面的单词
    //                 // '/': ''                      //选择忽略拦截器里面的单词
    //             }
    //         }
    //     }
    // }


    // devServer: { 
    //     port: 80, 
    //     proxy: { 
    //         '/wuxin-api': { 
    //             target: 'http://wuxin-api:8888', 
    //             changeOrigin: true, 
    //         }
    //     }
    // }
}