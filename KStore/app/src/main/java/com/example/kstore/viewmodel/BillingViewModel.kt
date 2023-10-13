package com.example.kstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kstore.data.Address
import com.example.kstore.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel(){

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }

    fun getUserAddress(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())

            firestore.collection("user").document(firebaseAuth.uid!!).collection("address")
                .addSnapshotListener{value, error ->

                    if (error != null){ // We have Error
                        viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                        return@addSnapshotListener
                    }
                    val addresses = value?.toObjects(Address::class.java)
                    viewModelScope.launch {
                        _address.emit(Resource.Success(addresses!!))
                    }

                }

        }
    }

}