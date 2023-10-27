package com.example.a26apigooglemap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapRoadLineViewMode @Inject constructor(private val repository: Repository) : ViewModel() {
    val _uiState = MutableLiveData<UiStateRL>(UiStateRL.Empty)

    fun getDateRoadsLine(currentLication: String, destination: String) {
        _uiState.value = UiStateRL.Loading
        viewModelScope.launch {
            val response = repository.getSimpleRoutes(currentLication, destination)
            try {
                if (response.isSuccessful) {
                    response.body()?.let { directionsResponse ->
                        _uiState.postValue(UiStateRL.Result(directionsResponse = directionsResponse))
                    }
                } else {
                    _uiState.postValue(UiStateRL.Error(error = response.code().toString()))
                }
            } catch (e: Exception) {
                _uiState.postValue(UiStateRL.Error(error = e.message.toString()))
            }
        }
    }


    sealed class UiStateRL {
        object Empty : UiStateRL()
        object Loading : UiStateRL()
        class Result(val directionsResponse: DirectionsResponse) : UiStateRL()
        class Error(val error: String) : UiStateRL()
    }
}