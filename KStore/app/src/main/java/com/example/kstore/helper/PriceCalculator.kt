package com.example.kstore.helper

fun Float?.getProductPrice(price: Float): Float{
    // this --> percentage
    if (this == null)
        return price

    val remainingPercentage = 1f - this
    val priceAfterOffer = remainingPercentage * price

    return priceAfterOffer
}