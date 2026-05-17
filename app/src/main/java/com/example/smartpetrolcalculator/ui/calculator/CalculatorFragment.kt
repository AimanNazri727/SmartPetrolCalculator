package com.example.smartpetrolcalculator.ui.calculator

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartpetrolcalculator.databinding.FragmentCalculatorBinding
import com.example.smartpetrolcalculator.ui.home.CalculationResult
import com.example.smartpetrolcalculator.ui.home.ChartState
import com.example.smartpetrolcalculator.ui.home.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val petrolTypes = listOf("RON95", "RON97", "Diesel")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            petrolTypes
        )
        (binding.spinnerPetrolType as AutoCompleteTextView).setAdapter(adapter)

        // Set default tanpa auto-fill dulu — tunggu API
        binding.spinnerPetrolType.setText(petrolTypes[0], false)

        binding.spinnerPetrolType.setOnItemClickListener { _, _, position, _ ->
            val selected = petrolTypes[position]
            autoFillPrice(selected)

            // Kalau pilih RON97 atau Diesel, budi switch tak relevant
            if (selected != "RON95") {
                binding.switchBudi.isChecked = false
            }
        }
    }

    private fun autoFillPrice(petrolType: String) {
        val price = viewModel.getPriceForType(petrolType)
        if (price > 0) {
            binding.etPrice.setText(String.format("%.2f", price))
            binding.tvPriceSource.text = "✅ API"
        } else {
            binding.etPrice.text?.clear()
            binding.tvPriceSource.text = "📡 API"
        }
    }

    private fun setupClickListeners() {
        binding.btnCalculate.setOnClickListener { onCalculate() }
        binding.btnReset.setOnClickListener { onReset() }
    }

    private fun onCalculate() {
        val petrolType = binding.spinnerPetrolType.text.toString().trim()
        val priceStr   = binding.etPrice.text.toString().trim()
        val usageStr   = binding.etUsage.text.toString().trim()

        if (petrolType.isEmpty() || priceStr.isEmpty() || usageStr.isEmpty()) {
            Snackbar.make(binding.root, "Sila isi semua maklumat", Snackbar.LENGTH_SHORT).show()
            return
        }
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            binding.tilPrice.error = "Masukkan harga yang sah"; return
        }
        binding.tilPrice.error = null

        val usage = usageStr.toDoubleOrNull()
        if (usage == null || usage <= 0) {
            binding.tilUsage.error = "Masukkan jumlah liter yang sah"; return
        }
        binding.tilUsage.error = null

        // RON97 & Diesel — tiada BUDI walaupun switch ON
        val isBudi = binding.switchBudi.isChecked && petrolType == "RON95"
        viewModel.calculate(petrolType, price, usage, isBudi)
    }

    private fun onReset() {
        binding.spinnerPetrolType.setText(petrolTypes[0], false)
        binding.etPrice.text?.clear()
        binding.etUsage.text?.clear()
        binding.switchBudi.isChecked       = false
        binding.tilPrice.error             = null
        binding.tilUsage.error             = null
        binding.layoutResultSection.visibility = View.GONE
        viewModel.reset()
        autoFillPrice("RON95")
    }

    private fun observeViewModel() {
        viewModel.chartState.observe(viewLifecycleOwner) { state ->
            if (state is ChartState.Success) {
                val d = state.data
                binding.tvBarRon95.text  = "RM${String.format("%.2f", d.latestRon95)}"
                binding.tvBarRon97.text  = "RM${String.format("%.2f", d.latestRon97)}"
                binding.tvBarDiesel.text = "RM${String.format("%.2f", d.latestDiesel)}"

                // Auto-fill harga bila API dah load
                val currentType = binding.spinnerPetrolType.text.toString()
                if (binding.etPrice.text.isNullOrEmpty() || binding.etPrice.text.toString() == "0.00") {
                    autoFillPrice(currentType.ifEmpty { "RON95" })
                }
            }
        }

        viewModel.calculationResult.observe(viewLifecycleOwner) { result ->
            if (result != null) displayResults(result)
        }
    }

    private fun displayResults(result: CalculationResult) {
        binding.layoutResultSection.visibility = View.VISIBLE

        binding.tvSummaryType.text  = result.petrolType
        binding.tvSummaryUsage.text = "${String.format("%.1f", result.fuelUsage)} liter"
        binding.tvSummaryPrice.text = "RM${String.format("%.2f", result.pricePerLitre)}/L"

        binding.tvFormula.text = "${String.format("%.1f", result.fuelUsage)}L × RM${String.format("%.2f", result.pricePerLitre)} = RM${String.format("%.2f", result.totalCost)}"

        binding.tvTotalCost.text    = "RM ${String.format("%,.2f", result.totalCost)}"
        binding.tvFinalPayable.text = "RM ${String.format("%,.2f", result.finalPayable)}"

        // Set warna text manual supaya nampak
        binding.tvTotalCost.setTextColor(android.graphics.Color.parseColor("#003087"))
        binding.tvFinalPayable.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        binding.tvFormula.setTextColor(android.graphics.Color.parseColor("#555555"))

        if (result.isBudiEligible) {
            binding.rowBudi.visibility   = View.VISIBLE
            binding.rowSaving.visibility = View.VISIBLE
            binding.tvBudiRebate.text    = "RM ${String.format("%,.2f", result.budiRebate)}"
            binding.tvTotalSaving.text   = "RM ${String.format("%,.2f", result.totalSaving)}"
            binding.tvBudiRebate.setTextColor(android.graphics.Color.parseColor("#F57F17"))
            binding.tvTotalSaving.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        } else {
            binding.rowBudi.visibility   = View.GONE
            binding.rowSaving.visibility = View.GONE
        }

        binding.root.post { binding.layoutResultSection.requestFocus() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}