package com.example.kstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kstore.data.CartProduct
import com.example.kstore.firebase.FirebaseCommon
import com.example.kstore.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel(){

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()


    // If Product is Already in the Cart then increase the Quantity
    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let{
                    if(it.isEmpty()){// Add the New Product
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct){ // Increase the Quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        }else{ // Add new Product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }


    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductIntoCart(cartProduct){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){ _, e->
            viewModelScope.launch {
                if (e == null){
                    _addToCart.emit(Resource.Success(cartProduct))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}