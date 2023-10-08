package com.example.nobeteczane.adaptor

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nobeteczane.model.EczaneModel
import com.example.nobeteczane.databinding.RecyclerRowBinding

class EzcaneRecyclerAdaptor(private val liste: List<EczaneModel> , private val bilgi: (String,String) -> Unit)  : RecyclerView.Adapter<EzcaneRecyclerAdaptor.ViewHolder>() {

    class ViewHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = RecyclerRowBinding.inflate(inflate,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return liste.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.eczaneIsim.text = liste[position].name
        holder.binding.eczaneAdres.text = liste[position].address
        val mesafe = String.format("%.1f", liste[position].mesafe)+" KM"
        holder.binding.eczaneMesafe.text = mesafe

        val mSpannableString = SpannableString(liste[position].phone)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        holder.binding.eczaneTelefon.text= mSpannableString

        holder.binding.eczaneIlce.text = liste[position].dist

        val hedef = liste[position].enlem.toString() + "," +liste[position].boylam.toString()

        holder.binding.tarif.setOnClickListener {
            bilgi(hedef,"tarif")
        }
        holder.binding.eczaneTelefon.setOnClickListener {
            bilgi(liste[position].phone,"telefon")
        }

    }

}