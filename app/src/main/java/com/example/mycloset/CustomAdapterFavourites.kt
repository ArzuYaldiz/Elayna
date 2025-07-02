package com.example.mycloset

import Utils.FirebaseUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout

class CustomAdapterFavourites(
    private val groupedData: List<List<FavouritesActivity.clothsJpg>>,
    private val onClothClick: (FavouritesActivity.clothsJpg) -> Unit
) : RecyclerView.Adapter<CustomAdapterFavourites.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupContainer: LinearLayout = view.findViewById(R.id.groupContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cloth_favourites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groupedData[position]
        holder.groupContainer.removeAllViews()

        for (cloth in group) {
            val button = ImageButton(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(220, 220).apply {
                    setMargins(8, 0, 8, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                background = null
            }

            FirebaseUtil.setWardrobeItemJpg(
                holder.itemView.context,
                cloth.imageId,
                button
            )

            button.setOnClickListener {
                onClothClick(cloth)
            }

            holder.groupContainer.addView(button)
        }
    }

    override fun getItemCount() = groupedData.size
}
