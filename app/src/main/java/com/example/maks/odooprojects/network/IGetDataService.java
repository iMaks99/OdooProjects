package com.example.maks.odooprojects.network;

import com.example.maks.odooprojects.models.HRDepartment;
import com.example.maks.odooprojects.models.HREmployee;
import com.example.maks.odooprojects.models.MailActivity;
import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskTag;
import com.example.maks.odooprojects.models.ProjectTaskType;
import com.example.maks.odooprojects.models.ResPartner;

import java.lang.reflect.GenericArrayType;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
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

    @GET("getuser/")
    Call<ResPartner> getUser(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getprojectbyid/")
    Call<ProjectProject> getProjectById(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );

    @POST("createproject/")
    Call<Integer> newProject(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Body ProjectProject project
    );

    @GET("editproject/")
    Call<ResponseBody> deleteProject(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );

    @POST("editproject/")
    Call<ResponseBody> editProject(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Body ProjectProject project
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

    @GET("getprojecttasks/")
    Call<List<ProjectTask>> getProjectTasks(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );

    @POST("addprojecttask/")
    Call<Integer> newProjectTask(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Body ProjectTask projectTask
    );

    @GET("deleteprojecttask/")
    Call<ResponseBody> deleteProjectTask(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("task_id") int taskId
    );

    @POST("editprojecttask/")
    Call<ResponseBody> editProjectTask(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Body ProjectTask projectTask
    );

    @GET("gettaskbyid/")
    Call<ProjectTask> getTaskById(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("task_id") int taskId
    );

    @GET("getprojecttasktype")
    Call<List<ProjectTaskType>> getProjectTaskStages(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("project_id") int projectId
    );

    @GET("gettaskmailactivity/")
    Call<List<MailActivity>> getTaskMailActivity(
            @Header("Authorization") String token,
            @Header("dbname") String dbname,
            @Query("task_id") int taskId
    );

    @GET("gettasktagsall/")
    Call<List<ProjectTaskTag>> getTaskTagsAll(
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

    @GET("getuserrespartners/")
    Call<List<ResPartner>> getUserPartners(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @GET("getrespartnersall")
    Call<List<ResPartner>> getPartnersAll(
            @Header("Authorization") String token,
            @Header("dbname") String dbname
    );

    @FormUrlEncoded
    @POST("login/")
    Call<String> login(
            @Field("db_name") String db_name,
            @Field("fcm_token") String fcm_token,
            @Field("user_mail") String db_mail
    );
}
