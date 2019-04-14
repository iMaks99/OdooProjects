package com.example.maks.odooprojects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;

public class DBConnectionFragment extends Fragment {

    private ProgressDialog progressDialog;

    public DBConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_db_connection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button connect = getActivity().findViewById(R.id.connect_to_db);

        connect.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading projects...");
            progressDialog.show();

            IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

            String dbName = ((EditText) getActivity().findViewById(R.id.db_name_et)).getText().toString();
            String dbHost = ((EditText) getActivity().findViewById(R.id.db_host_et)).getText().toString();
            String dbPort = ((EditText) getActivity().findViewById(R.id.db_port_et)).getText().toString();
            String dbUser = ((EditText) getActivity().findViewById(R.id.db_user_et)).getText().toString();
            String dbPassword = ((EditText) getActivity().findViewById(R.id.db_password_et)).getText().toString();

            Call<ResponseBody> result = service.connectToDb(
                    dbName,
                    dbHost,
                    dbPort,
                    dbUser,
                    dbPassword
            );
            result.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {

                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("db_name", dbName).apply();
                        sharedPreferences.edit().putString("db_host", dbHost).apply();
                        sharedPreferences.edit().putString("db_port", dbPort).apply();
                        sharedPreferences.edit().putString("db_user", dbUser).apply();
                        sharedPreferences.edit().putString("db_password", dbPassword).apply();

                        sharedPreferences.edit().putBoolean("is_db_connected", true).apply();

                        Toast.makeText(getContext(), "Successfully connected to DB!", Toast.LENGTH_SHORT).show();

                        if (sharedPreferences.getString("token", "").isEmpty()) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.auth_content_frame, new LoginFragment())
                                    .commit();
                        } else {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), "Cannot connect to DB.\nPlease check parameters.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Ooops... Something wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
