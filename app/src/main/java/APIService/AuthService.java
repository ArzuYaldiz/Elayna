package APIService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.mycloset.dataClasses.AuthenticationRequestDto;
import com.example.mycloset.dataClasses.AuthenticationResponseDto;
import com.example.mycloset.dataClasses.RegisterRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;

public interface AuthService {
    @POST("register")
    Call<RegisterResponseDto> register(@Body RegisterRequestDto request);

    @GET("verify")
    Call<AuthenticationResponseDto> verifyUser(@Query("token") String token);

     @POST("login")
    Call<RegisterResponseDto> login(@Body AuthenticationRequestDto request);
}
