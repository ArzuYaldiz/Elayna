package com.example.mycloset;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mycloset.dataClasses.ClothingItem;

import java.util.List;

// RecyclerView adaptörü (basit versiyon)
public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {
    private List<ClothingItem> items;

    public ClothesAdapter(List<ClothingItem> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgClothing;

        public ViewHolder(android.view.View view) {
            super(view);
            imgClothing = view.findViewById(R.id.img_clothing);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothingItem item = items.get(position);
        holder.imgClothing.setImageResource(item.getImageResId());
        // Eğer isim göstermek istersen, layout'a TextView ekleyebilirsin
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
