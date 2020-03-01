package com.gsgana.gsledger.utilities

import com.google.gson.JsonObject
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header


const val URL = "https://us-central1-pricedata-475d0.cloudfunctions.net/testpost/"

interface ServerNetwork {
    @POST("/testpost")
    fun postRequest(
        @Header("token") token: String,
        @Body params:JsonObject
    ) : Call<Data>
}

data class Data(var reg : String,
                var metal : String,
                var currency : String)
