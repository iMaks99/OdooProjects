package com.example.maks.odooprojects.network;

import com.example.maks.odooprojects.models.ProjectProject;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetDataService {

    @GET("getProjectsAll/")
    Call<List<ProjectProject>> getAllProjects(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @FormUrlEncoded
    @POST("connection/")
    Call<ResponseBody> connectToDb(
            @Field("db_name") String db_name,
            @Field("db_host") String db_host,
            @Field("db_port") String db_port,
            @Field("db_user") String db_user,
            @Field("db_password") String db_password);


    @FormUrlEncoded
    @POST("login/")
    Call<String> login(
            @Field("db_name") String db_name,
            @Field("user_mail") String db_mail
    );
}
