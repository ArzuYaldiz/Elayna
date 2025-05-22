package Utils;


import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

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

    public static StorageReference getWardrobeItemStorageRef(String user_id){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://elayna-8c765.firebasestorage.app");
        return storage.getReference().child("Wardrobe")
                .child("wardrobe_item_"+user_id + ".jpg");
    }

    public static StorageReference getWardrobeObjStorageRef(String user_id){
        return FirebaseStorage.getInstance().getReference().child("Wardrobe")
                .child("wardrobe_item_"+user_id + ".obj");
    }
}
