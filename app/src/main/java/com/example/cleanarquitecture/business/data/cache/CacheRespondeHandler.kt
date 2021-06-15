package com.example.cleanarquitecture.business.data.cache

import com.example.cleanarquitecture.business.domain.state.*

abstract class CacheRespondeHandler<ViewState,Data>(
    private val response:CacheResult<Data?>,
    private val stateEvent : StateEvent?
){
    suspend fun getResult(): DataState<ViewState> {
        return when(response){
            is CacheResult.GenericError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}" +
                                "Reason: ${response.errorMessage}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is CacheResult.Success ->{
                if (response.value == null){
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}" +
                                    "Reason: ${CacheErrors.CACHE_DATA_NULL}",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                }else{
                    handleSucces(resultObject = response.value)
                }
            }
        }
    }

    abstract fun handleSucces(resultObject: Data): DataState<ViewState>
}