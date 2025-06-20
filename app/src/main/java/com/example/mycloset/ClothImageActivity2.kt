package com.example.mycloset;

import Utils.FirebaseUtil
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Layout.Alignment
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.Config
import com.google.firebase.storage.FirebaseStorage
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import kotlin.collections.mutableListOf
import kotlin.toString


class ClothImageActivity2 : ComponentActivity() {

    private lateinit var arSceneView: ArSceneView
    private lateinit var modelNode: ArModelNode

    private lateinit var recyclerView: RecyclerView;
    private var user_id: String? = null

    data class clothsJpg(var clothname:String, var imageId: Uri)//Food
    data class clothsGlb(var modelname:String,var modelId:Uri)

    private var clothList = mutableListOf<clothsJpg>()
    private var modelList = mutableListOf<clothsGlb>()


    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloth_image2)

        Log.d("DEBUG", "Firebase items found, starting to load URIs")

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        } else {
            setupCameraAndAR()
        }

        //setupARScene()
    }

    private fun setupCameraAndAR() {
        arSceneView = findViewById(R.id.cameraView)
        recyclerView = findViewById(R.id.recycler_clothes)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
            .getString("userId", null)

        val storageRef = FirebaseStorage.getInstance().reference.child("Wardrobe/$user_id")

        // Initialize adapter ONCE with an empty list
        val adapter = CustomAdapter(clothList) { cloth ->
            // Only load model when button is clicked
            val modelRef = FirebaseUtil.getWardrobeGlbStorageRef(user_id)

            Log.d("GLB_DOWNLOAD", "Model URL: $modelRef")
            modelRef.downloadUrl.addOnSuccessListener { uri ->
                loadModel(uri.toString())
            }.addOnFailureListener {
                Log.e("GLB_DOWNLOAD", "Failed to download model for ${cloth.clothname}", it)
                Toast.makeText(this, "Model not found", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = adapter

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    if (item.name.endsWith(".jpg")) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            val name = item.name.removeSuffix(".jpg")
                            val cloth = clothsJpg(name, uri)
                            clothList.add(cloth)
                            adapter.notifyItemInserted(clothList.size - 1)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading wardrobe items", exception)
            }
    }

     private fun loadModel(modelName: String) {
         arSceneView.planeRenderer.isVisible = false
         arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
         arSceneView.planeRenderer.isShadowReceiver = false
         if (!::modelNode.isInitialized) {
             modelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                 isVisible = true
             }
             arSceneView.addChild(modelNode)
         }

         modelNode.loadModelGlbAsync(
             glbFileLocation = modelName,
             scaleToUnits = 1f,
             centerOrigin = null

         )
         arSceneView.addChild(modelNode)
     }

}





