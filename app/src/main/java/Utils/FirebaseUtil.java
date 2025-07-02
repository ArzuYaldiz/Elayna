package Utils;


import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {
    List<StorageReference> modelRefs = new ArrayList<>();


    public static StorageReference getCurrentProfilePicStorageRef(String user_id){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child("profile_pic_user_"+user_id);
    }

    public static StorageReference getCurrentProfilePic(String user_id){
        return FirebaseStorage.getInstance()
                .getReference("profile_pic/profile_pic_user_" + user_id + ".jpg");
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static StorageReference getWardrobeItemStorageRef(String user_id, String seasonSpinner, String sectionSpinner, String categorySpinner, String cloth_id){

        Log.d("CLOTH_ID FIREBASE:", cloth_id);
        return FirebaseStorage.getInstance().getReference().child("Wardrobe").child(user_id)
                    .child(String.format("wardrobe_item_%s_%s_%s_%s.jpg",cloth_id, seasonSpinner,sectionSpinner,categorySpinner));


    }

    public static StorageReference getWardrobeItemStorageRef(String user_id, String url){

        return FirebaseStorage.getInstance().getReference().child("Wardrobe").child(user_id)
                .child("url");


    }
    public static StorageReference getWardrobeItemStorageRefNew(String user_id, String season, String type){
        if(season != null)
            return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                .child("wardrobe_item_"+season+type+user_id + ".jpg");
        return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                .child("wardrobe_item_"+type+user_id + ".jpg");
    }
    public static void setWardrobeItemJpg(Context context, Uri imageUri, ImageButton imageButton){
        Glide.with(context).load(imageUri).apply(RequestOptions.noTransformation()).into(imageButton);
    }

    public static StorageReference getWardrobeGlbStorageRef(String user_id, String seasonSpinner, String sectionSpinner, String categorySpinner, String cloth_id){
        return FirebaseStorage.getInstance().getReference().child("Wardrobe").child(user_id)
                .child(String.format("wardrobe_item_%s_%s_%s_%s.glb",cloth_id, seasonSpinner,sectionSpinner,categorySpinner));

    }

    public static  List<StorageReference> getWardrobeGlbStorageRefList(String user_id){
        List<StorageReference> modelList = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Wardrobe").child(user_id);

        storageRef.listAll()
                .addOnSuccessListener(listResult ->{
                    for (StorageReference item : listResult.getItems()) {
                        if (item.getName().endsWith(".glb")) {
                            Log.d("FİREBASE: ", item.getPath());
                            modelList.add(item);
                        }

                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to list wardrobe items", e);
                });
        return modelList;
    }

    public static StorageReference getWardrobeGlbStorageRefName(String user_id){
        return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                .child("wardrobe_item_"+user_id + ".glb");
    }
    public static StorageReference getWardrobeGlbStorageRefNew(String user_id, String season, String type){
        if(season != null)
            return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                    .child("wardrobe_item_"+season+type+user_id + "..glb");
        return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                .child("wardrobe_item_"+type+user_id + "..glb");
    }

}
