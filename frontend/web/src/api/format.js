// 分 -> 元，保留两位小数
export function formatPrice(fen) {
    return (fen / 100).toFixed(2)
}