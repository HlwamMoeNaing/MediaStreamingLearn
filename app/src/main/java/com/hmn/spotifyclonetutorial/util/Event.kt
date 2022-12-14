package com.hmn.spotifyclonetutorial.util

class Event<out T>(private val data:T) {
    var hasBeenHandled = false
    private set

    fun getContentIfNotHandle():T?{
        return if(hasBeenHandled){
            null
        }else{
            hasBeenHandled = true
            data
        }
    }

    fun peekContent() = data
}