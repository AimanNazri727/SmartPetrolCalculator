package com.example.smartpetrolcalculator.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.smartpetrolcalculator.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    // ⚠️ TUKAR NI DENGAN INFO KAMU SEBENAR
    private val authorName = "Muhammad Aiman Bin Mohd Nazri"
    private val matricNo   = "2025158881"
    private val courseName = "Mobile Technology"
    private val githubUrl  = "https://github.com/username/SmartPetrolCalculator"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvAuthorName.text = authorName
        binding.tvMatric.text     = matricNo
        binding.tvCourse.text     = courseName
        binding.tvGithubUrl.text  = githubUrl
        binding.tvGithubUrl.setOnClickListener { openUrl(githubUrl) }
        binding.btnGithub.setOnClickListener   { openUrl(githubUrl) }
    }

    private fun openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}