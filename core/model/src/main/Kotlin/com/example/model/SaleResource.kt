package com.example.model

data class SaleResource(
    val key: String,
    //  4是杂货 5是囤积
    val level: String,
    val unitPrice: Double,
    val count:Int,
    val dataTime: String
)
