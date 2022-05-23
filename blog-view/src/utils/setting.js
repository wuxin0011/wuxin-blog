// 颜色配置默认选项列表
export const colors = [
    'default',
    'red',
    'orange',
    'yellow',
    'olive',
    'green',
    'teal',
    'blue',
    'violet',
    'pink',
    'brown',
    'grey'
]





// 图片列表
export const backgroundImageList = [
    'default',
    'https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/blog-resource/img/bg1.jpg',
    'https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/blog-resource/img/bg2.jpg',
    'https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/blog-resource/img/bg3.jpg',
]


// 页面默认配置
export const defaultSetting = {
    // 将配置信息全部保存为一个对象
    // 专注模式
    count: 1,
    // 默认不显示侧边栏
    focusMode: false,
    // 导航栏颜色默认黑色
    inverted: true,
    // 菜单颜色 默认 无
    menuColor: '',
    // 是否显示名字 默认 显示
    showName: true,
    // 夜间模式 默认 不显示
    nightMode: false,
    // 背景设置
    background: {
        // 背景透明度 默认 无
        opacity: 1,
        // 是否显示背景图 默认 不显示
        isShowImage: false,
        // 背景图 地址 默认地址
        image: "default",
        // 图片模糊背景化
        filter: '10px',
        // 背景色 默认无
        color: '#fff',
        // 图片透明度 默认 无
        imageOpacity: 1
    },

    // 默认页面窗口大小
    clientSize: {
        clientHeight: 0,
        clientWidth: 1080
    },


}


/**
 * 设置背景图
 * @param show
 * @param url
 * @returns {string|null}
 */
export function settingBackgroundImageUrl(show, url) {
    if ((show) && (url !== 'default')) {
        return 'url(' + `${url}` + ')' || "url('https://fastly.jsdelivr.net/gh/wuxin0011/wuxin@main/img/202112/20211217101725.png')"
    } else {
        return null
    }

}

/**
 * 修改背景色配置
 * @param color
 * @param mode
 * @returns {string|*}
 */
export function settingBackgroundColor(color, mode) {

    if (color && !mode) {
        return color
    } else {
        return mode ? 'rgb(18, 18, 18)' : '#fff'
    }
}

/**
 * 设置url
 * @param url
 * @returns {string}
 */
export function settUrl(url) {
    return 'url(' + `${url}` + ')'
}


export function validKeywords(keywords) {
    if (
        keywords.indexOf('操') !== -1 ||
        keywords.indexOf('尼玛') !== -1 ||
        keywords.indexOf('nm') !== -1 ||
        keywords.indexOf('n m') !== -1 ||
        keywords.indexOf('cnm') !== -1 ||
        keywords.indexOf('c n m') !== -1 ||
        keywords.indexOf('日') !== -1 ||
        keywords.indexOf('m') !== -1 ||
        keywords.indexOf('艹') !== -1 ||
        keywords.indexOf('cao') !== -1 ||
        keywords.indexOf('草') !== -1 ||
        keywords.indexOf('si') !== -1 ||
        keywords.indexOf('死') !== -1 ||
        keywords.indexOf('婊') !== -1 ||
        keywords.indexOf('儿') !== -1 ||
        keywords.indexOf('爷') !== -1 ||
        keywords.indexOf('姑') !== -1 ||
        keywords.indexOf('姥') !== -1 ||
        keywords.indexOf('爸') !== -1 ||
        keywords.indexOf('妈') !== -1 ||
        keywords.indexOf('弟') !== -1 ||
        keywords.indexOf('ba') !== -1 ||
        keywords.indexOf('ma') !== -1 ||
        keywords.indexOf('末日') !== -1 ||
        keywords.indexOf('崴') !== -1 ||
        keywords.indexOf('薇') !== -1 ||
        keywords.indexOf('日本') !== -1 ||
        keywords.indexOf('日 本') !== -1 ||
        keywords.indexOf('huang') !== -1 ||
        keywords.indexOf('h u a n g') !== -1 ||
        keywords.indexOf('黄色') !== -1 ||
        keywords.indexOf('riben') !== -1 ||
        keywords.indexOf('meiguo') !== -1

    ) {

        return true
    }
    return false
}