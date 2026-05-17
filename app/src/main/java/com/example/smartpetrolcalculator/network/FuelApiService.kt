package com.example.smartpetrolcalculator.network

import com.example.smartpetrolcalculator.data.FuelPrice
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FuelApiService {

    @GET("data-catalogue")
    suspend fun getFuelPriceHistory(
        @Query("id")    id:    String = "fuelprice",
        @Query("limit") limit: Int    = 8,
        @Query("sort")  sort:  String = "-date"
    ): Response<List<FuelPrice>>
}