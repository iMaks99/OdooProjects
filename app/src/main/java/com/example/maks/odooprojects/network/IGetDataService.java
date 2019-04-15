package com.example.maks.odooprojects.network;

import com.example.maks.odooprojects.models.HRDepartment;
import com.example.maks.odooprojects.models.HREmployee;
import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskType;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IGetDataService {

    @GET("getprojectsall/")
    Call<List<ProjectProject>> getAllProjects(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getusertasks/")
    Call<List<ProjectTask>> getUserTasks(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getusertasksstages/")
    Call<List<ProjectTaskType>> getUserTasksStages(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getprojecttasks")
    Call<List<ProjectTask>> getProjectTasks(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );


    @GET("getprojecttasktype")
    Call<List<ProjectTaskType>> getProjectTaskStages(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );


    @FormUrlEncoded
    @POST("connection/")
    Call<ResponseBody> connectToDb(
            @Field("db_name") String db_name,
            @Field("db_host") String db_host,
            @Field("db_port") String db_port,
            @Field("db_user") String db_user,
            @Field("db_password") String db_password
    );

    @GET("getdepartmentsall/")
    Call<List<HRDepartment>> getAllDepartments(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getdepartmentemployees/")
    Call<List<HREmployee>> getDepartmentEmployees(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("department_id") int departmentId
    );

    @FormUrlEncoded
    @POST("login/")
    Call<String> login(
            @Field("db_name") String db_name,
            @Field("user_mail") String db_mail
    );
}
