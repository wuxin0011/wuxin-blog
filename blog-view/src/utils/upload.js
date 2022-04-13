/**
 * 
 * @returns uuid生成 防止图片重命名报错
 */
export function randomUUID() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8)
		return v.toString(16)
	})
}


/**
 * 
 * @returns 判断文件是否图片格式
 */
 export function isImgExt(fileName) {
	console.log('fileName',fileName)
    return /\.(apng|avif|bmp|gif|ico|cur|jpg|jpeg|jfif|pjpeg|pjp|png|svg|tif|tiff|webp)$/i.test(fileName)
  }