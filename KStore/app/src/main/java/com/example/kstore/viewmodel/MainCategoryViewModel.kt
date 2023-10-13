package com.example.kstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kstore.data.Product
import com.example.kstore.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct: StateFlow<Resource<List<Product>>> = _specialProduct

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val _bestDealProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProduct: StateFlow<Resource<List<Product>>> = _bestDealProduct

    private val pagingInfo = PagingInfo()
//    private val bestDealsPagingInfo = BestDealsPagingInfo()
    init {
        fetchSpecialProduct()
        fetchBestDeals()
        fetchBestProduct()
    }

    fun fetchSpecialProduct(){
        viewModelScope.launch {
            _specialProduct.emit(Resource.Loading())
            firestore.collection("Products")
                .whereEqualTo("category", "Special").get()
                .addOnSuccessListener { result ->
                    val specialProductList = result.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _specialProduct.emit(Resource.Success(specialProductList))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProduct.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDeals(){
//        if(!pagingInfo.isPagingEndBestDeals) {
            viewModelScope.launch {
                _bestDealProduct.emit(Resource.Loading())
                firestore.collection("Products").whereEqualTo("category", "Best Deals").get()  // .limit(pagingInfo.bestDealsProductPage * 3).
                    .addOnSuccessListener { result ->
                        val bestDealsProduct = result.toObjects(Product::class.java)
//                        pagingInfo.isPagingEndBestDeals = bestDealsProduct == pagingInfo.oldBestDealsProducts
//                        pagingInfo.oldBestDealsProducts = bestDealsProduct
                        viewModelScope.launch {
                            _bestDealProduct.emit(Resource.Success(bestDealsProduct))
                        }
//                        pagingInfo.bestDealsProductPage++
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _bestDealProduct.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
//        }
    }

    fun fetchBestProduct(){
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                firestore.collection("Products").limit(pagingInfo.bestProductPage * 10).get()  // whereEqualsTo("category", "Chair").orderBy("id").Query.Direction.ASCENDING
                    .addOnSuccessListener { result ->
                        val bestProducts = result.toObjects(Product::class.java)                        // To add more ordering index
                        pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                        pagingInfo.oldBestProducts = bestProducts
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Success(bestProducts))
                        }
                        pagingInfo.bestProductPage++
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
        }
    }

    internal data class PagingInfo(
        var bestProductPage: Long = 1,
        var oldBestProducts: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false,
//        var bestDealsProductPage : Long = 1,
//        var oldBestDealsProducts: List<Product> = emptyList(),
//        var isPagingEndBestDeals: Boolean = false
    )
    // Data Class for Best Deals
}