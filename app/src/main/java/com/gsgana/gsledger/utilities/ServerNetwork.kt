package com.gsgana.gsledger.utilities

import com.google.gson.JsonObject
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header


const val URL = "https://us-central1-gsledger-29cad.cloudfunctions.net/"

interface ServerRequest {
    @POST("/testpost")
    fun postRequest(
        @Header("token") token: String,
        @Body params:JsonObject
    ) : Call<DataRequest>
}

interface ServerSummit {
    @POST("/summitData")
    fun postRequest(
        @Header("token") token: String,
        @Body params:JsonObject
    ) : Call<Data>
}

data class DataRequest(var price1 : String,
                       var price2 : String)


data class Data(var reg : String,
                var pre : String,
                var cur : String,
                var min : String,
                var max : String)
