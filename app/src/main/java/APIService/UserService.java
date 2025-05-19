package APIService;

import com.example.mycloset.dataClasses.ProfileInformation;
import com.example.mycloset.dataClasses.RegisterRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.example.mycloset.dataClasses.UpdateUserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

    @POST("update")
    Call<RegisterResponseDto> updateUser(@Body UpdateUserDto request);

    @GET("profile")
    Call<ProfileInformation> getProfile (@Query("user_id") int user_id);
}
