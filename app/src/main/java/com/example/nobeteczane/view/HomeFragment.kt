package com.example.nobeteczane.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nobeteczane.adaptor.EzcaneRecyclerAdaptor
import com.example.nobeteczane.model.EczaneModel
import com.example.nobeteczane.model.EczaneViewModel
import com.example.nobeteczane.model.KonumViewModel
import com.example.nobeteczane.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var konumViewModel: KonumViewModel
    private lateinit var eczaneViewModel: EczaneViewModel
    private lateinit var siralanmisEczane : ArrayList<EczaneModel>

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        konumViewModel = ViewModelProvider(requireActivity())[KonumViewModel::class.java]
        eczaneViewModel = ViewModelProvider(requireActivity())[EczaneViewModel::class.java]

        val recyclerView = binding.eczaneRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        eczaneViewModel.list.observe(requireActivity()) {
            siralanmisEczane = it
            val adapter = EzcaneRecyclerAdaptor(siralanmisEczane){ bilgi,islem ->
                if (islem == "tarif") {
                    val mapIntentUri = Uri.parse("google.navigation:q=$bilgi")
                    val webIntentUri =
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$bilgi")

                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    val webIntent = Intent(Intent.ACTION_VIEW, webIntentUri)

                    val secim = Intent.createChooser(webIntent, "Yol Tarifi Uygulamasını Seçin")
                    secim.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(mapIntent))

                    if (secim.resolveActivity(requireActivity().packageManager) != null) {
                        requireContext().startActivity(secim)
                    } else {
                        requireContext().startActivity(webIntent)
                    }
                }
                else if (islem == "telefon") {
                    val telefonintent = Intent(Intent.ACTION_DIAL)
                    val uri = Uri.parse("tel:$bilgi")
                    telefonintent.data = uri

                    if (telefonintent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(telefonintent)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Telefon uygulaması bulunamadı.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }



            recyclerView.adapter= adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}