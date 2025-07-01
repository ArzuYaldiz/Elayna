package com.example.mycloset;

import APIService.WardrobeService
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloset.dataClasses.ClothRequestDto
import com.google.ar.core.Config
import com.google.firebase.storage.FirebaseStorage
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory


class ClothImageActivity2 : ComponentActivity() {

    private lateinit var arSceneView: ArSceneView
    private lateinit var modelNode: ArModelNode
    private val modelNodes = mutableListOf<ArModelNode>()

    private lateinit var recyclerView: RecyclerView;
    private var user_id: String? = null

    data class clothsJpg(var clothname:String, var imageId: Uri)
    data class clothsGlb(var modelname:String,var modelId:Uri)

    private var clothList = mutableListOf<clothsJpg>()
    private var modelList = mutableListOf<clothsGlb>()
    private val clothType = mutableListOf<ClothRequestDto>()

    private lateinit var clothPlaceButton: Button
    private lateinit var winterButton: Button
    private lateinit var summerButton: Button
    private lateinit var allButton: Button
    private lateinit var topButton: Button
    private lateinit var bottomButton: Button
    private lateinit var shoesButton: Button
    private lateinit var accessoryButton: Button

    private val fullClothList = mutableListOf<ClothImageActivity2.clothsJpg>()  // All clothes
    private val filteredClothList = mutableListOf<ClothImageActivity2.clothsJpg>()  // Filtered clothes

    private var currentSeason: String? = null
    private var currentCategory: String? = null

    private val modelNodesByType = mutableMapOf<String, ArModelNode>()
    val urlToClothTypeMap = mutableMapOf<String, ClothRequestDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloth_image2)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.135.3:8080/")
            //.baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val wardrobeService = retrofit.create(WardrobeService::class.java)


        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        } else {
            setupCameraAndAR(wardrobeService)
        }


        //setupARScene()
    }

    private fun fetchClothData(wardrobeService: WardrobeService, userId: Int) {

        wardrobeService.getClothType(userId).enqueue(object : Callback<List<ClothRequestDto>> {
            override fun onResponse(
                call: Call<List<ClothRequestDto>>,
                response: Response<List<ClothRequestDto>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    clothType.clear()
                    clothType.addAll(result)

                    // You can now use clothList in your RecyclerView or logs
                    Log.d("ClothList", clothList.toString())
                    clothType.forEach { clothDto ->
                        urlToClothTypeMap[clothDto.image_url] = clothDto
                    }
                } else {
                    Log.e("ERROR: ","No data found")
                }
            }

            override fun onFailure(call: Call<List<ClothRequestDto>>, t: Throwable) {
                Log.e("ERROR: ","No data found: ", t)
            }
        })
    }


    private fun filterList(adapter: CustomAdapter) {

        val filtered = fullClothList.filter { cloth ->
            val name = cloth.clothname.uppercase()
            val seasonMatches = currentSeason?.let { name.contains(it) } ?: true
            val categoryMatches = currentCategory?.let { name.contains(it) } ?: true
            seasonMatches && categoryMatches
        }

        filteredClothList.clear()
        filteredClothList.addAll(filtered)

        adapter.notifyDataSetChanged()
    }

    private fun setupCameraAndAR(wardrobeService: WardrobeService) {
        arSceneView = findViewById(R.id.cameraView)
        recyclerView = findViewById(R.id.recycler_clothes)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        clothPlaceButton = findViewById(R.id.placeButton)
        winterButton= findViewById(R.id.btn_winter)
        summerButton= findViewById(R.id.btn_summer)
        allButton= findViewById(R.id.btn_all)
        topButton= findViewById(R.id.btn_top)
        bottomButton= findViewById(R.id.btn_bottom)
        shoesButton= findViewById(R.id.btn_shoe)
        accessoryButton= findViewById(R.id.btn_accessory)

        user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
            .getString("userId", null)

        val storageRef = FirebaseStorage.getInstance().reference.child("Wardrobe/$user_id")


        fetchClothData(wardrobeService, user_id!!.toInt())

        // Initialize adapter ONCE with an empty list
        var adapter = CustomAdapter(filteredClothList) { cloth ->
            // Only load model when button is clicked
            val matchingModel = modelList.find { it.modelname == cloth.clothname}

            val imageUrl = cloth.imageId.toString()
            val clothTypeInfo = urlToClothTypeMap[imageUrl]

            if (clothTypeInfo != null) {
                val clothType = clothTypeInfo.clothType
                val clothSeason = clothTypeInfo.season
                Log.d("ClothInfo", "Selected cloth type: $clothType, season: $clothSeason")

                // Now find the model that matches this cloth and load it
                val matchingModel = modelList.find { it.modelname == cloth.clothname }
                if (matchingModel != null) {
                    loadModel(matchingModel.modelname, matchingModel.modelId, clothType)
                } else {
                    Toast.makeText(this, "Model not found for ${cloth.clothname}", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Type info not found for this cloth", Toast.LENGTH_SHORT).show()
            }

        }

        allButton.setOnClickListener {
            currentSeason = null
            currentCategory = null
            filteredClothList.clear()
            filteredClothList.addAll(fullClothList)
            adapter.notifyDataSetChanged()
        }

        summerButton.setOnClickListener {
            currentSeason = "SUMMER"
            filterList(adapter)
        }

        winterButton.setOnClickListener {
            currentSeason = "WINTER"
            filterList(adapter)
        }

        topButton.setOnClickListener {
            currentCategory = "TOP"
            filterList(adapter)
        }

        bottomButton.setOnClickListener {
            currentCategory = "BOTTOM"
            filterList(adapter)
        }

        shoesButton.setOnClickListener {
            currentCategory = "SHOES"
            filterList(adapter)
        }

        accessoryButton.setOnClickListener {
            currentCategory = "ACCESSORY"
            filterList(adapter)
        }


        recyclerView.adapter = adapter

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    if (item.name.endsWith(".jpg")) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            val name = item.name.removeSuffix(".jpg")
                            val cloth = clothsJpg(name, uri)
                            fullClothList.add(cloth)
                            adapter.notifyItemInserted(clothList.size - 1)
                            filterList(adapter)

                            val clothTypeInfo = urlToClothTypeMap[imageUrl]
                            if (clothTypeInfo != null) {
                                // You now have the type info associated with this cloth image
                                Log.d("ClothType", "Cloth $name has type ${clothTypeInfo.image_url} and season ${clothTypeInfo.season}")
                            }
                        }
                    }
                    else if (item.name.endsWith(".glb")){
                        item.downloadUrl.addOnSuccessListener { uri ->
                            val name = item.name.removeSuffix(".glb")
                            val model = clothsGlb(name, uri)
                            modelList.add(model)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading wardrobe items", exception)
            }


    }

    private fun loadModel(modelRef: String, modelUri: Uri, clothType: String) {

        arSceneView.planeRenderer.isVisible = false
        arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
        arSceneView.planeRenderer.isShadowReceiver = false

        modelNodesByType[clothType]?.let { existingNode ->
            arSceneView.removeChild(existingNode)
            modelNodes.remove(existingNode)
            modelNodesByType.remove(clothType)
        }


        val newNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
            isVisible = true

            loadModelGlbAsync(
                glbFileLocation = modelUri.toString(),
                centerOrigin = null
            )
            scale = Float3(0.6f)

        }

        modelNodes.add(newNode)
        modelNodesByType[clothType] = newNode
        arSceneView.addChild(newNode)

        clothPlaceButton.setOnClickListener {
            newNode.anchor()
        }

        //burada kategoriyr bak eğer aynı kategoriyse modelNodeu kaldır eğer farklıysa ekleee
        //modelNodes a bak eklenen modellerin ismi var mı oradaa

        val name = modelRef
        val model = clothsGlb(name, modelUri)
    }


}





