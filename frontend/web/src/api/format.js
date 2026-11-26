// 分 -> 元，保留两位小数
export function formatPrice(fen) {
    return (fen / 100).toFixed(2)
}

// 后端时间字符串转展示格式 "yyyy-MM-dd HH:mm:ss"
export function formatTime(str) {
    if (!str) return ''
    return String(str).replace('T', ' ').slice(0, 19)
}
