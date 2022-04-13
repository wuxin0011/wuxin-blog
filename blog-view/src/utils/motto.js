// 今日诗词API 是一个可以免费调用的诗词接口：https://www.jinrishici.com

const keyName = "jinrishici-token";

export function load(callback, errHandler) {
    if (window.localStorage && window.localStorage.getItem(keyName)) {
        return commonLoad(callback, errHandler, window.localStorage.getItem(keyName));
    } else {
        return corsLoad(callback, errHandler);
    }
}

export function corsLoad(callback, errHandler) {
    const newCallBack = function (result) {
        window.localStorage.setItem(keyName, result.token);
        callback(result);
    };
    return sendRequest(newCallBack, errHandler, "https://v2.jinrishici.com/one.json?client=npm-sdk/1.0");
}

export function commonLoad(callback, errHandler, token) {
    return sendRequest(callback, errHandler, "https://v2.jinrishici.com/one.json?client=npm-sdk/1.0&X-User-Token=" + encodeURIComponent(token));
}

export function sendRequest(callback, errHandler, apiUrl) {
    var xhr = new XMLHttpRequest();
    xhr.open('get', apiUrl);
    xhr.withCredentials = true;
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            var data = JSON.parse(xhr.responseText);
            if (data.status === "success") {
                callback(data);
            } else {
            	if (errHandler) {
            		errHandler(data);
            	}
            }
        }
    };
}

