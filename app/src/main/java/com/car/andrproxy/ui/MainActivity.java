package com.car.andrproxy.ui;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.car.Globals;
import com.car.JNIHelper;
import com.car.LogHelper;
import com.car.NaiveConfig;
import com.car.NaiveHelper;
import com.car.andrproxy.Constants;
import com.car.andrproxy.R;
import com.google.android.material.navigation.NavigationView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setNavigationItemSelectedListener(this);
        init();


        Globals.Init(MainActivity.this);

        copyRawResourceToDir(R.raw.cacert, Globals.getCaCertPath(), true);
        copyRawResourceToDir(R.raw.country, Globals.getCountryMmdbPath(), true);
        copyRawResourceToDir(R.raw.clash_config, Globals.getClashConfigPath(), false);
        File config = new File(Globals.getNaiveConfigPath());
        if(!config.exists()){
            //Just Debug!
            copyRawResourceToDir(R.raw.config, Globals.getNaiveConfigPath(), true);
        }

        Log.e("MainActivity","Config:"+Globals.getNaiveConfigPath());

        NaiveConfig cacheConfig = NaiveHelper.readNaiveConfig(Globals.getNaiveConfigPath());
        if (cacheConfig == null) {
            LogHelper.e(TAG, "read null config");
        } else {
            Globals.setNaiveConfigInstance(cacheConfig);
        }
        if (!Globals.getNaiveConfigInstance().isValidRunningConfig()) {
            LogHelper.e(TAG, "Invalid config!");
        }


        File file = new File(Globals.getNaiveConfigPath());
        if (file.exists()) {
            try {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] content = new byte[(int) file.length()];
                    fis.read(content);
                    String contentStr = new String(content);
                    NaiveConfig ins = Globals.getNaiveConfigInstance();
                    ins.fromJSON(contentStr);

                    //configText.setText(ins.getConfig());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void init() {
        checkPermissions(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean) {
                    Constants.tendToSettings(MainActivity.this);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_text:
                actionNewText();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void actionNewText() {
        checkPermissions(aBoolean -> {
            if (!aBoolean) {
                Constants.tendToSettings(MainActivity.this);
                return;
            }
            startActivity(new Intent(MainActivity.this, IniEditActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkPermissions(Consumer<Boolean> consumer) {
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(consumer);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logcat:
                startActivity(new Intent(this, LogcatActivity.class));
                return true;
            case R.id.about:
                showAbout();
                drawerLayout.close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        String NaiveV = JNIHelper.GetNaiveVersion(getApplicationContext());

        new MaterialDialog.Builder(this)
                .title("AndrProxy")
                .content("Version: 0.0.1\n"+NaiveV+"\nBy: Jidoer")
                .show();
    }

    private void copyRawResourceToDir(int resId, String destPathName, boolean override) {
        File file = new File(destPathName);
        if (override || !file.exists()) {
            try {
                try (InputStream is = getResources().openRawResource(resId);
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
