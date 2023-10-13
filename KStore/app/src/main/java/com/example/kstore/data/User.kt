package com.example.kstore.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath:String =""
){
    // If you wanna use the Firebase
    constructor(): this("","","","")
}
