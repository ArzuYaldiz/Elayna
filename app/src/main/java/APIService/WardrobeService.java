package APIService;

import android.net.Uri;

import com.example.mycloset.dataClasses.ClothRequestDto;
import com.example.mycloset.dataClasses.ClothUploadDto;
import com.example.mycloset.dataClasses.FavouritesDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WardrobeService {

    @POST("/wardrobe")
    Call<RegisterResponseDto> imageTo3d(@Body ClothUploadDto clothUploadDto);

    @GET("/wardrobe/{TASK_ID}")
    Call<RegisterResponseDto> GetObjFromMeshy(@Path("TASK_ID") String taskId, @Query("user_id") int userId);

    @POST("/cloth-id")
    Call<Integer> getNewClothId();
    @Multipart
    @POST("/upload-cloth")
    Call<RegisterResponseDto> uploadCloth(
            @Part MultipartBody.Part file,
            @Part("season") RequestBody season,
            @Part("section") RequestBody section,
            @Part("category") RequestBody category,
            @Part("imageUrl") RequestBody imageUri,
            @Part("userId") RequestBody userId
    );

    @POST("/cloth-type")
    Call <List<ClothRequestDto>> getClothType(@Query("user_id") int userId);

    @POST("/favorites-upload")
    Call <String> addFavourites(@Body List<String> favourites, @Query("user_id") String userId);

    @POST("/favorites-get")
    Call<List<FavouritesDto>> getFavourites(@Query("user_id") String user_id);
}
