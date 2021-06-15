package com.example.cleanarquitecture.business.data.network

import com.example.cleanarquitecture.business.domain.state.*

abstract class ApiResponseHandler<ViewState,Data>(
    private val response:ApiResult<Data?>,
    private val stateEvent:StateEvent
) {

    suspend fun getResult():DataState<ViewState>{
        return when(response){
            is ApiResult.GenericError -> {
                DataState.error(
                    response = Response(
                        message = stateEvent.errorInfo() +
                                "Reason: ${response.errorMessage}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is ApiResult.NetWorkError -> {
                DataState.error(
                    response = Response(
                        message = stateEvent?.errorInfo() +
                                "Reason: ${NetworkErrors.NETWORK_ERROR}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is ApiResult.Success -> {
                if (response.value == null){
                    DataState.error(
                        response = Response(
                            message = stateEvent.errorInfo() +
                                    "Reason: ${NetworkErrors.NETWORK_ERROR}",
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

    abstract suspend fun handleSucces(resultObject:Data):DataState<ViewState>

}