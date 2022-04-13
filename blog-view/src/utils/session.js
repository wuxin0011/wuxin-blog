import Cookies from "js-cookie";
/**
 * 添加localStore
 * @param key
 * @param value
 */
export function setSetStore(key, value) {
    window.localStorage.setItem(key, JSON.stringify(value))
}

/**
 * 获取localStore
 */
export function getStore(key) {
    try {
        return JSON.parse(window.localStorage.getItem(key))
    } catch (error) {
        return null;
    }
}

/**
 * 移除localStore
 */
export function removeStore(key) {
    window.localStorage.removeItem(key)
}

/**
 * 清空本地所有缓存
 */
export function clearStore() {
    window.localStorage.clear()
}


/**
 * 获取cookie
 */
export function getCookies(key) {
    
    return Cookies.get(key)
}

/**
 * 使用cookie设置过期时间保存数据
 */
export function setCookies(key, value, expiresTime) {
    
    if (!expiresTime) {
        // 默认过期时间为3分钟
        expiresTime = 18
    }
    let expires = new Date(new Date() * 1 + expiresTime * 1000)

    return Cookies.set(key, value, {expires: expires})


}

/**
 * 移除cookie
 */
export function removeCookies(key) {

    return Cookies.remove(key)

}
