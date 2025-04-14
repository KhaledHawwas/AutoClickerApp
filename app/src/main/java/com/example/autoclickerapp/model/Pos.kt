package com.example.autoclickerapp.model

data class Pos(
    val x: Int,
    val y: Int
)
fun String.toPos(): Pos {
    val parts = this.split(",")
    return Pos(parts[0].toInt(), parts[1].toInt())
}
fun Pos.toStringPos(): String {
    return "$x,$y"
}
