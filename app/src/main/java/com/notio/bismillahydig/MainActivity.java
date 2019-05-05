package com.notio.bismillahydig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.notio.bismillahydig.database.DBLocalHandler;
import com.notio.bismillahydig.database.ServerYDIG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String SUMBER_LOGIN, ID_LOGIN, UNIQ_ID;
    private ProgressBar progressBar;
    private DBLocalHandler dbLocalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.main_progressbar);
        dbLocalHandler = new DBLocalHandler(this);

        buatUniqID();
        cekDBLokal();
        validasiLogin();

        urusanToolbar();
    }

    private void urusanToolbar() {
        ImageView imageView = findViewById(R.id.main_profilepicture);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }

        int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
        Uri profilePictureUri = ImageRequest.getProfilePictureUri(ID_LOGIN, dimensionPixelSize , dimensionPixelSize );
        Glide.with(this).load(profilePictureUri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_logout).setTitle(Html.fromHtml("<font color='#182336'>Keluar</font>"));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            kirimPenggunaKeHalamanLogin();
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
        ArrayList<HashMap<String, String>> userDB = dbLocalHandler.getUser(1);
        for (Map<String, String> map : userDB){
            SUMBER_LOGIN = map.get("sumber_login");
            ID_LOGIN = map.get("id_login");
//            PASSWORD = map.get("password");
        }
    }

    private void kirimPenggunaKeHalamanLogin() {
        dbLocalHandler.deleteDB();
        LoginManager.getInstance().logOut();
        ServerYDIG serverYDIG = new ServerYDIG(this,"logout");
        List<String> list = new ArrayList<>();
        list.add(ID_LOGIN);
        synchronized (this){
            serverYDIG.sendArrayList(list);
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
