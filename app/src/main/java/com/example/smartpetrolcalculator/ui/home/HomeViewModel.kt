package com.example.smartpetrolcalculator.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpetrolcalculator.network.RetrofitClient
import kotlinx.coroutines.launch

data class CalculationResult(
    val petrolType: String,
    val pricePerLitre: Double,
    val fuelUsage: Double,
    val totalCost: Double,
    val budiRebate: Double,
    val totalSaving: Double,
    val finalPayable: Double,
    val isBudiEligible: Boolean
)

data class FuelChartData(
    val dates: List<String>,
    val ron95: List<Float>,
    val ron97: List<Float>,
    val diesel: List<Float>,
    val latestRon95: Double,
    val latestRon97: Double,
    val latestDiesel: Double,
    val latestDate: String
)

sealed class ChartState {
    object Loading : ChartState()
    data class Success(val data: FuelChartData) : ChartState()
    data class Error(val message: String) : ChartState()
}

class HomeViewModel : ViewModel() {

    private val _calculationResult = MutableLiveData<CalculationResult?>()
    val calculationResult: LiveData<CalculationResult?> = _calculationResult

    private val _chartState = MutableLiveData<ChartState>(ChartState.Loading)
    val chartState: LiveData<ChartState> = _chartState

    private var latestRon95  = 0.0
    private var latestRon97  = 0.0
    private var latestDiesel = 0.0

    companion object {
        const val BUDI_MADANI_RATE = 1.99
    }

    init { fetchChartData() }

    fun fetchChartData() {
        _chartState.value = ChartState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.fuelApiService.getFuelPriceHistory(limit = 8)
                if (response.isSuccessful) {
                    val list = response.body()
                    if (!list.isNullOrEmpty()) {
                        // Filter rows yang ada semua 3 nilai (buang rows kosong)
                        val valid = list.filter {
                            (it.ron95 ?: 0.0) > 1.0 &&
                                    (it.ron97 ?: 0.0) > 1.0 &&
                                    (it.diesel ?: 0.0) > 1.0
                        }

                        val sorted = if (valid.isNotEmpty()) valid.reversed() else list.reversed()

                        val dates  = sorted.map { formatDate(it.date) }
                        val ron95  = sorted.map { (it.ron95  ?: 0f).toFloat() }
                        val ron97  = sorted.map { (it.ron97  ?: 0f).toFloat() }
                        val diesel = sorted.map { (it.diesel ?: 0f).toFloat() }

                        val latest = sorted.last()
                        latestRon95  = latest.ron95  ?: 0.0
                        latestRon97  = latest.ron97  ?: 0.0
                        latestDiesel = latest.diesel ?: 0.0

                        _chartState.value = ChartState.Success(
                            FuelChartData(
                                dates        = dates,
                                ron95        = ron95,
                                ron97        = ron97,
                                diesel       = diesel,
                                latestRon95  = latestRon95,
                                latestRon97  = latestRon97,
                                latestDiesel = latestDiesel,
                                latestDate   = latest.date
                            )
                        )
                    } else {
                        _chartState.value = ChartState.Error("Tiada data diterima")
                    }
                } else {
                    _chartState.value = ChartState.Error("Ralat API: ${response.code()}")
                }
            } catch (e: Exception) {
                _chartState.value = ChartState.Error("Ralat: ${e.localizedMessage}")
            }
        }
    }

    fun getPriceForType(petrolType: String): Double {
        return when (petrolType.uppercase()) {
            "RON95"  -> latestRon95
            "RON97"  -> latestRon97
            "DIESEL" -> latestDiesel
            else     -> 0.0
        }
    }

    fun calculate(
        petrolType: String,
        pricePerLitre: Double,
        fuelUsage: Double,
        isBudiEligible: Boolean
    ) {
        val totalCost    = fuelUsage * pricePerLitre
        val isRon95      = petrolType.equals("RON95", ignoreCase = true)
        val budiRebate   = if (isBudiEligible && isRon95) fuelUsage * BUDI_MADANI_RATE else 0.0

        // RON95 layak BUDI → bayar rebat je
        // RON97/Diesel → bayar harga penuh
        val finalPayable = if (isBudiEligible && isRon95) budiRebate else totalCost

        _calculationResult.value = CalculationResult(
            petrolType     = petrolType,
            pricePerLitre  = pricePerLitre,
            fuelUsage      = fuelUsage,
            totalCost      = totalCost,
            budiRebate     = budiRebate,
            totalSaving    = totalCost - budiRebate,
            finalPayable   = finalPayable,
            isBudiEligible = isBudiEligible && isRon95
        )
    }


    fun reset() { _calculationResult.value = null }

    private fun formatDate(dateStr: String): String {
        return try {
            val parts = dateStr.split("-")
            val day   = parts[2].trimStart('0')
            val month = when (parts[1]) {
                "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mac"
                "04" -> "Apr"; "05" -> "Mei"; "06" -> "Jun"
                "07" -> "Jul"; "08" -> "Ogo"; "09" -> "Sep"
                "10" -> "Okt"; "11" -> "Nov"; "12" -> "Dis"
                else -> parts[1]
            }
            "$day $month"
        } catch (e: Exception) { dateStr }
    }
}