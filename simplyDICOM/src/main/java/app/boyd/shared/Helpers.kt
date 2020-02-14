package app.boyd.shared

fun Double.coercePercent(): Double {
    return this.coerceIn(0.0, 100.0)
}