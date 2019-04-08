package com.example.maks.odooprojects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.maks.odooprojects.network.GetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Boolean isConnected = sharedPreferences.getBoolean("is_db_connected", false);

        if (token.isEmpty()) {

            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (!isConnected) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Connecting to server...");
            progressDialog.show();

            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            Call<ResponseBody> result = service.connectToDb(
                    sharedPreferences.getString("db_name", ""),
                    sharedPreferences.getString("db_host", ""),
                    sharedPreferences.getString("db_port", ""),
                    sharedPreferences.getString("db_user", ""),
                    sharedPreferences.getString("db_password", "")
            );

            result.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        sharedPreferences.edit().putBoolean("is_db_connected", true).apply();
                        goToMainPage();
                    } else {
                        Toast.makeText(MainActivity.this, "Cannot connect to DB.\nPlease check parameters.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Ooops... Something wrong", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            goToMainPage();
        }
    }

    void goToMainPage() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new ProjectsRecyclerViewFragment(), "ProjectRecyclerFragment")
                .commit();
    }
}
