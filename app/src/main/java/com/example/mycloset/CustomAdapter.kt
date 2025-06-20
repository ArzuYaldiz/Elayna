package com.example.mycloset

import Utils.FirebaseUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageButton

class CustomAdapter(
    private val dataSet: MutableList<ClothImageActivity2.clothsJpg>,
    private val onClothClick: (ClothImageActivity2.clothsJpg) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clothImageButton: ImageButton = view.findViewById(R.id.clothImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cloth, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cloth = dataSet[position]

        FirebaseUtil.setWardrobeItemJpg(
            holder.itemView.context,
            cloth.imageId,
            holder.clothImageButton
        )

        holder.clothImageButton.setOnClickListener {
            onClothClick(cloth)
        }
    }

    override fun getItemCount() = dataSet.size
}
