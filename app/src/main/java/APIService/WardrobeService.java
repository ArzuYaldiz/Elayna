package APIService;

import com.example.mycloset.dataClasses.ClothUploadDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WardrobeService {

    @POST("/wardrobe")
    Call<RegisterResponseDto> imageTo3d(@Body ClothUploadDto clothUploadDto);

    @GET("/wardrobe/{TASK_ID}")
    Call<RegisterResponseDto> GetObjFromMeshy(@Path("TASK_ID") String taskId, @Query("user_id") int userId);

}
