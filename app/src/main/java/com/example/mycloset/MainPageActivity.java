package com.example.mycloset;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycloset.dataClasses.ClothingItem;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {
    private Button btnWinter, btnSummer, btnAll, btnTop, btnBottom, btnShoe;
    private RecyclerView recyclerClothes;
    private ClothesAdapter clothesAdapter;
    private List<ClothingItem> clothingList = new ArrayList<>();
    private String currentSeason = "Winter";
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // View'ları tanımla
        btnWinter = findViewById(R.id.btn_winter);
        btnSummer = findViewById(R.id.btn_summer);
        btnAll = findViewById(R.id.btn_all);
        btnTop = findViewById(R.id.btn_top);
        btnBottom = findViewById(R.id.btn_bottom);
        btnShoe = findViewById(R.id.btn_shoe);
        recyclerClothes = findViewById(R.id.recycler_clothes);

        // RecyclerView ayarları
        clothesAdapter = new ClothesAdapter(clothingList);
        recyclerClothes.setLayoutManager(new GridLayoutManager(this, 3)); // 3 sütunlu grid
        recyclerClothes.setAdapter(clothesAdapter);

        // Buton dinleyicileri
        btnWinter.setOnClickListener(v -> {
            currentSeason = "Winter";
            loadClothes();
        });

        btnSummer.setOnClickListener(v -> {
            currentSeason = "Summer";
            loadClothes();
        });

        btnAll.setOnClickListener(v -> {
            currentCategory = "All";
            loadClothes();
        });

        btnTop.setOnClickListener(v -> {
            currentCategory = "Top";
            loadClothes();
        });

        btnBottom.setOnClickListener(v -> {
            currentCategory = "Bottom";
            loadClothes();
        });

        btnShoe.setOnClickListener(v -> {
            currentCategory = "Shoe";
            loadClothes();
        });

        // Sayfa açıldığında default olarak Winter + All
        loadClothes();
    }

    private void loadClothes() {
        clothingList.clear();
        List<ClothingItem> loadedItems = fetchClothes(currentSeason, currentCategory);
        clothingList.addAll(loadedItems);
        clothesAdapter.notifyDataSetChanged();
    }

    // Dummy veri ile örnek fonksiyon -- ileride gerçek veriyle değiştirebilirsin
    private List<ClothingItem> fetchClothes(String season, String category) {
        List<ClothingItem> result = new ArrayList<>();
        // Sadece örnek! Gerçek resimler yerine drawable/ic_launcher kullanabilirsin.
        if (season.equals("Winter") && (category.equals("All") || category.equals("Top"))) {
            result.add(new ClothingItem("Sweater", R.drawable.ic_launcher_foreground));
            result.add(new ClothingItem("Jacket", R.drawable.ic_launcher_foreground));
        }
        if (season.equals("Winter") && (category.equals("All") || category.equals("Bottom"))) {
            result.add(new ClothingItem("Pants", R.drawable.ic_launcher_foreground));
        }
        if (season.equals("Winter") && (category.equals("All") || category.equals("Shoe"))) {
            result.add(new ClothingItem("Boots", R.drawable.ic_launcher_foreground));
        }
        // Summer örnekleri de ekleyebilirsin
        if (season.equals("Summer") && (category.equals("All") || category.equals("Top"))) {
            result.add(new ClothingItem("T-Shirt", R.drawable.ic_launcher_foreground));
        }
        if (season.equals("Summer") && (category.equals("All") || category.equals("Bottom"))) {
            result.add(new ClothingItem("Shorts", R.drawable.ic_launcher_foreground));
        }
        if (season.equals("Summer") && (category.equals("All") || category.equals("Shoe"))) {
            result.add(new ClothingItem("Sandals", R.drawable.ic_launcher_foreground));
        }
        return result;
    }
}
