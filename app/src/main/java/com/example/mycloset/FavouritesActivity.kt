package com.example.mycloset

import APIService.WardrobeService
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloset.dataClasses.FavouritesDto
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.ar.core.Config
import com.google.firebase.storage.FirebaseStorage
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavouritesActivity:ComponentActivity(){
    private lateinit var arSceneView: ArSceneView
    private val modelNodes = mutableListOf<ArModelNode>()

    data class clothsJpg(var clothname:String, var imageId: Uri)
    data class clothsGlb(var modelname:String,var modelId:Uri)

    private var favList = mutableListOf<FavouritesDto>()
    private var clothList = mutableListOf<clothsJpg>()
    private var modelList = mutableListOf<clothsGlb>()
    val modelNodesByType = mutableMapOf<Int, MutableList<ArModelNode>>()

    private lateinit var recyclerView: RecyclerView;

    private lateinit var returnButton: ImageButton
    private lateinit var clothPlaceButton: Button

    private var groupedList: List<List<FavouritesActivity.clothsJpg>> = emptyList()

    private var user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        findViews()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.135.3:8080/")
            //.baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val wardrobeService = retrofit.create(WardrobeService::class.java)

        returnButton.setOnClickListener {
            val i = Intent(getApplicationContext(), ProfilePageActivity::class.java)
            startActivity(i)
        }

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        } else {
            setupCameraAndAR(wardrobeService)
        }
    }

    private fun findViews(){
        arSceneView = findViewById(R.id.cameraView)
        recyclerView = findViewById(R.id.recycler_clothes)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        clothPlaceButton = findViewById(R.id.placeButton)
        returnButton = findViewById(R.id.btn_back)
    }

    private fun setupCameraAndAR(wardrobeService: WardrobeService) {
        user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
            .getString("userId", null)

        wardrobeService.getFavourites(user_id).enqueue(object : Callback<List<FavouritesDto>> {
            override fun onResponse(
                call: Call<List<FavouritesDto>>,
                response: Response<List<FavouritesDto>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    favList.clear()
                    favList.addAll(result)

                } else {
                    Log.e("ERROR: ", "No data found")
                }
            } override fun onFailure(call: Call<List<FavouritesDto>>, t: Throwable) {
                Log.e("ERROR: ","No data found: ", t)
            }
        })

        val storageRef = FirebaseStorage.getInstance().reference.child("Wardrobe/$user_id")
/*
        var adapter = CustomAdapterFavourites(groupedList) { cloth ->

            val imageUrl = cloth.imageId.toString()
            // Now find the model that matches this cloth and load it
            val matchingModel = modelList.find { it.modelname == cloth.clothname }
            if (matchingModel != null) {
                loadModel(matchingModel.modelname, matchingModel.modelId)

            } else {
                Toast.makeText(this, "Model not found for ${cloth.clothname}", Toast.LENGTH_SHORT).show()
            }

        }*/

        val groupedMap = mutableMapOf<Int, MutableList<FavouritesActivity.clothsJpg>>()

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val tasks = mutableListOf<Task<*>>()
                for (item in listResult.items) {
                    var array = item.name.split("_")
                    var id = array[2]
                    if(id.startsWith("0")){
                        id = id.substring(1);
                    }
                    val clothIdInt = id.toIntOrNull()
                    if (clothIdInt != null && item.name.endsWith(".jpg")) {
                        val match = favList.find { it.cloth_id == clothIdInt }
                        match?.let {
                            Log.d("MATCH", "Found matching favourite: ${it.cloth_id}")
                            val task = item.downloadUrl.addOnSuccessListener { uri ->
                                val name = item.name.removeSuffix(".jpg")
                                val cloth = FavouritesActivity.clothsJpg(name, uri)

                                val combinId = it.combin_id
                                val list = groupedMap.getOrPut(combinId) { mutableListOf() }
                                list.add(cloth)
                            }
                            tasks.add(task)
                        }
                    }
                    else if (clothIdInt != null && item.name.endsWith(".glb")){
                        val match = favList.find { it.cloth_id == clothIdInt }
                        match?.let {
                            item.downloadUrl.addOnSuccessListener { uri ->
                                val name = item.name.removeSuffix(".glb")
                                val model = clothsGlb(name, uri)
                                modelList.add(model)
                            }
                        }
                    }
                }
                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    groupedList = groupedMap.values.toList() // ✅ set global variable

                    var adapter = CustomAdapterFavourites(groupedList) { cloth ->

                        val matchingModel = modelList.find { it.modelname == cloth.clothname }

                        if (matchingModel != null) {
                            var foundGroupIndex: Int? = null
                            var foundItemIndex: Int? = null

                            groupedList.forEachIndexed { groupIndex, group ->
                                val itemIndex = group.indexOfFirst { it.clothname == cloth.clothname}
                                if (itemIndex != -1) {
                                    foundGroupIndex = groupIndex
                                    foundItemIndex = itemIndex
                                    return@forEachIndexed  // stop the loop early since we found it
                                }
                            }
                            Log.d("MODEL FAV: ", foundGroupIndex.toString())

                            loadModel(matchingModel.modelname, matchingModel.modelId, foundGroupIndex)
                        } else {
                            Toast.makeText(this, "Model not found for ${cloth.clothname}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    recyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading wardrobe items", exception)
            }
    }

    private fun loadModel(modelRef: String, modelUri: Uri, foundItemIndex: Int?) {

        arSceneView.planeRenderer.isVisible = false
        arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
        arSceneView.planeRenderer.isShadowReceiver = false

        if (foundItemIndex != null) {
            if (modelNodesByType.keys.any { it != foundItemIndex }) {
                // Remove all existing nodes from scene and clear data
                modelNodesByType.values.flatten().forEach { node ->
                    arSceneView.removeChild(node)
                    modelNodes.remove(node)
                }
                modelNodesByType.clear()
            }
        }

        val newNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
            isVisible = true
            name = modelRef
            loadModelGlbAsync(
                glbFileLocation = modelUri.toString(),
                centerOrigin = null
            )
            scale = Float3(0.6f)

        }
        modelNodes.add(newNode)
        val nodesList = modelNodesByType.getOrPut(foundItemIndex as Int) { mutableListOf() }
        nodesList.add(newNode)
        arSceneView.addChild(newNode)

        clothPlaceButton.setOnClickListener {
            newNode.anchor()
        }

        //burada kategoriyr bak eğer aynı kategoriyse modelNodeu kaldır eğer farklıysa ekleee
        //modelNodes a bak eklenen modellerin ismi var mı oradaa
    }


}