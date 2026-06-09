package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.sample.SampleRequest
import com.example.tamangfood.data.model.sample.toDomain
import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): SampleRepository{

    override suspend fun sample(): Flow<NetworkState> = callbackFlow{
        trySend(NetworkState.Loading)
        try{
            val sampleRequest = SampleRequest(name = "name")
            val response = apiService.sample(sampleRequest)
            // Call API
            if(response.code == HTTP.SUCCESS.status){ // Success
                trySend(NetworkState.Success(response.toDomain()))
            }else{ // Failed
                trySend(NetworkState.Error("Error"))
            }
        }
        catch (e: Exception){
            trySend(NetworkState.Error(e.message.toString()))
        }
        awaitClose {  }
    }

}