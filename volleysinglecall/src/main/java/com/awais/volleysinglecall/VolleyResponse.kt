package com.company.volleysinglecall.network

interface VolleyResponse {
    fun onSuccess(response: Any, tag: String)
    fun onFailure(message: String)
    fun noInternet()
}
