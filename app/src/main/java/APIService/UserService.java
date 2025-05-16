package APIService;

import com.example.mycloset.dataClasses.RegisterRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.example.mycloset.dataClasses.UpdateUserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("update")
    Call<RegisterResponseDto> updateUser(@Body UpdateUserDto request);
}
