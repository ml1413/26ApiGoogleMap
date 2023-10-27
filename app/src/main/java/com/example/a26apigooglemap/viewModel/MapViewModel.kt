package com.example.a26apigooglemap.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a26apigooglemap.Request.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>(UiState.Empty)
    val uiState: LiveData<UiState> = _uiState



    fun getDataNearbyPlaces(location: String, radius: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val response = repository.getNearbyPlaces(location = location, radius = radius)
            checkResponse(response)
        }
    }

    fun getDataComplexRoute(
        originId: String,
        destinationId: String,
        waypoints: String
    ) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val response = repository.getComplexRoute(originId, destinationId, waypoints)
            checkResponse(response)
        }
    }


    private fun <T> checkResponse(response: Response<T>) {
        try {
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    _uiState.postValue(UiState.Result(responseBody = result))
                }
            } else {
                _uiState.postValue(UiState.Error(error = response.code().toString()))
            }
        } catch (e: Exception) {
            _uiState.postValue(UiState.Error(error = e.message.toString()))
        }
    }

    sealed class UiState {
        object Empty : UiState()
        object Loading : UiState()
        class Result(val responseBody: Any?) : UiState()
        class Error(val error: String) : UiState()
    }


}