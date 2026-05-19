package com.example.model

data class NoSaleResource(
    val key: String,
    //  4是杂货 5是囤积
    val level: String,
    val count:Int,
    val dataTime: String
)
