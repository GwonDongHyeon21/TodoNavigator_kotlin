package com.example.todo_navigator_kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_navigator_kotlin.R
import com.example.todo_navigator_kotlin.api.AddressResponse

class AddressAdapter(
    private val addresses: List<AddressResponse.AddressItem>,
    private val onItemClick: (AddressResponse.AddressItem) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.addressText)

        fun bind(address: AddressResponse.AddressItem) {
            textView.text = address.roadAddress
            itemView.setOnClickListener {
                onItemClick(address)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int {
        return addresses.size
    }
}
