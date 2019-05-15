package com.example.maks.odooprojects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class AuthenticationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        sharedPreferences = getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (!sharedPreferences.getBoolean("is_db_connected", false))
            fragmentManager.beginTransaction()
                    .replace(R.id.auth_content_frame, new DBConnectionFragment(), "DBConnectionFragment")
                    .addToBackStack(null)
                    .commit();
        else
            fragmentManager.beginTransaction()
                    .replace(R.id.auth_content_frame, new LoginFragment())
                    .addToBackStack(null)
                    .commit();

    }
}
