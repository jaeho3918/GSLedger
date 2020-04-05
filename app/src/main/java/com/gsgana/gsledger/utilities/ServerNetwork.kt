package com.gsgana.gsledger.utilities

import com.google.gson.JsonObject
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header


const val URL = "https://us-central1-gsledger-29cad.cloudfunctions.net/"

interface ServerSummit {
    @POST("/testpost/summit")
    fun postRequest(
        @Header("token") token: String,
        @Body params:JsonObject
    ) : Call<Data>
}

interface ServerRequest {
    @POST("/testpost/request")
    fun postRequest(
        @Header("token") token: String,
        @Body params:JsonObject
    ) : Call<DataRequest>
}

data class Data(var reg : String,
                var metal : String,
                var cur : String)

data class DataRequest(var price1 : String,
                       var price2 : String)
