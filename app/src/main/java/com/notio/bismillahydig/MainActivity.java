package com.notio.bismillahydig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.notio.bismillahydig.database.DBLocalHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String SUMBER_LOGIN, ID_LOGIN, UNIQ_ID;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urusanToolbar();
        progressBar = findViewById(R.id.main_progressbar);
        buatUniqID();
        cekDBLokal();
        validasiLogin();
    }

    private void urusanToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            Toast.makeText(this, "Keluar", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void validasiLogin() {
        Log.e(TAG, "generateUniqID: " + UNIQ_ID);

        if (SUMBER_LOGIN == null){
            progressBar.setVisibility(View.GONE);
            kirimPenggunaKeHalamanLogin();
        } else {
            if (SUMBER_LOGIN.equals("facebook")){
                if (ID_LOGIN == null){
                    progressBar.setVisibility(View.GONE);
                    kirimPenggunaKeHalamanLogin();
                } else {
                    Toast.makeText(this, "SELAMAT ANDA SUDAH BISA LOGIN DENGAN FACEBOOK", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void buatUniqID() {
        UNIQ_ID = "35" +
                Build.BOARD.length()%10 + Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10;
    }

    private void cekDBLokal() {
        progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "checkDatabaseLokal: ");
        DBLocalHandler dbLocalHandler = new DBLocalHandler(this);
        ArrayList<HashMap<String, String>> userDB = dbLocalHandler.getUser(1);
        for (Map<String, String> map : userDB){
            SUMBER_LOGIN = map.get("sumber_login");
            ID_LOGIN = map.get("id_login");
//            PASSWORD = map.get("password");
        }
    }

    private void kirimPenggunaKeHalamanLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
