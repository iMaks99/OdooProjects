package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.maks.odooprojects.network.MessagingService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private ProgressDialog progressDialog;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button login = getActivity().findViewById(R.id.login_btn);
        login.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading projects...");
            progressDialog.show();

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
            String dbName = sharedPreferences.getString("db_name", "");

            IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
            Call<String> result = service.login(
                    dbName,
                    MessagingService.getToken(getContext()),
                    ((EditText) getActivity().findViewById(R.id.user_email_et)).getText().toString()
            );

            result.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {

                        String token = response.body();

                        sharedPreferences.edit().putString("token", token).apply();
                        Toast.makeText(getContext(), "Logged in", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Wrong login or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Ooops... Something wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
