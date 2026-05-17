package com.example.smartpetrolcalculator.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartpetrolcalculator.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.chartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ChartState.Loading -> {
                    binding.layoutChartLoading.visibility = View.VISIBLE
                    binding.lineChart.visibility          = View.GONE
                    binding.layoutChartLegend.visibility  = View.GONE
                    binding.layoutPricePills.visibility   = View.GONE
                    binding.tvChartError.visibility       = View.GONE
                }
                is ChartState.Success -> {
                    binding.layoutChartLoading.visibility = View.GONE
                    binding.lineChart.visibility          = View.VISIBLE
                    binding.layoutChartLegend.visibility  = View.VISIBLE
                    binding.layoutPricePills.visibility   = View.VISIBLE
                    binding.tvChartError.visibility       = View.GONE

                    val d = state.data
                    binding.lineChart.setData(
                        listOf(
                            ChartDataSet("RON95",  d.ron95,  Color.parseColor("#CC0001")),
                            ChartDataSet("RON97",  d.ron97,  Color.parseColor("#003087")),
                            ChartDataSet("Diesel", d.diesel, Color.parseColor("#2E7D32"))
                        ),
                        d.dates
                    )
                    binding.tvPillRon95.text  = "RM${String.format("%.2f", d.latestRon95)}"
                    binding.tvPillRon97.text  = "RM${String.format("%.2f", d.latestRon97)}"
                    binding.tvPillDiesel.text = "RM${String.format("%.2f", d.latestDiesel)}"
                    binding.tvChartDate.text  = "Terkini: ${d.latestDate}"
                }
                is ChartState.Error -> {
                    binding.layoutChartLoading.visibility = View.GONE
                    binding.tvChartError.visibility       = View.VISIBLE
                    binding.tvChartError.text             = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}