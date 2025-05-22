package Utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AndroidUtil {

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void setClothObjPic(Context context, Uri objUri, ImageView imageView){
        Glide.with(context).load(objUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
