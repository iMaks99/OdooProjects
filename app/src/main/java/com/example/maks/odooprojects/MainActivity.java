package com.example.maks.odooprojects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maks.odooprojects.models.ResPartner;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog progressDialog;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    Drawable toolbarDrawable;
    SharedPreferences sharedPreferences;
    IGetDataService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init() {

        sharedPreferences = getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Boolean isConnected = sharedPreferences.getBoolean("is_db_connected", false);
        service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

        if (token.isEmpty()) {

            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (!isConnected) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Connecting to server...");
            progressDialog.show();

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void goToMainPage() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Call<ResPartner> request = service.getUser(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<ResPartner>() {
            @Override
            public void onResponse(Call<ResPartner> call, Response<ResPartner> response) {
                if (response.isSuccessful()) {
                    ResPartner user = response.body();
                    View headerView = navigationView.getHeaderView(0);
                    TextView userName = headerView.findViewById(R.id.nav_user_name);
                    userName.setText(user.getName());
                    TextView userMail = headerView.findViewById(R.id.nav_user_mail);
                    userMail.setText(user.getEmail());
                }
            }

            @Override
            public void onFailure(Call<ResPartner> call, Throwable t) {

            }
        });

        showSelectedFragment(new ProjectsRecyclerViewFragment());
    }

    public void crateMenuButton() {
        toggle.setDrawerIndicatorEnabled(true);
        if (toolbarDrawable == null) {
            toolbarDrawable = toolbar.getNavigationIcon();
        }
        toolbar.setNavigationIcon(toolbarDrawable);
        invalidateOptionsMenu();
        toggle.syncState();
    }

    public void createBackButton() {
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(v -> {
            if (!toggle.isDrawerIndicatorEnabled()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            } else {
                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }
            }
        });
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_outline_arrow_back_24px));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_projects:
                fragment = new ProjectsRecyclerViewFragment();
                break;
            case R.id.nav_tasks:
                fragment = new UserTasksTabbedFragment();
                break;
            case R.id.nav_departments:
                fragment = new DepartmentsRecyclerViewFragment();
                break;

            case R.id.nav_logout:
                sharedPreferences.edit().putString("token", "").apply();
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
        }

        showSelectedFragment(fragment);
        return true;
    }

    private void showSelectedFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void setToolbarTitleEnabled(String title) {
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
    }
}
