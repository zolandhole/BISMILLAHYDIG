package com.notio.ydig;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.notio.ydig.database.DBLocalHandler;
import com.notio.ydig.database.ServerYDIG;
import com.notio.ydig.models.ModelUser;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private String ID,NAMA,EMAIL,JENKEL,ULTAH,LINK,LOKASI, SUMBERLOGIN, PASSWORD;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progressBar);
        loginButtonFacebook = findViewById(R.id.login_button_facebook);

        loginDenganFacebook();
    }

    private void loginDenganFacebook() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.notio.bismillahydig", PackageManager.GET_SIGNATURES
            );
            for (Signature signature: info.signatures){
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.e(TAG, "loginDenganFacebook: " + Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, "loginDenganFacebook: " + e);
        } catch (NoSuchAlgorithmException e){
            Log.e(TAG, "loginDenganFacebook: " + e);
        }
        
        callbackManager = CallbackManager.Factory.create();
        loginButtonFacebook.setReadPermissions(Arrays.asList("email","public_profile","user_gender","user_birthday","user_link","user_location"));
        // AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        //      @Override
        //      protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        //
        //      }
        // };
        //  ProfileTracker profileTracker = new ProfileTracker() {
        //      @Override
        //      protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
        //
        //      }
        //  };

        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            // Log.e(TAG, "onSuccess: USER ID" + loginResult.getAccessToken().getUserId()
            // + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                progressBar.setVisibility(View.VISIBLE);
                                // Log.e(TAG, "onCompleted: onSuccess: loginButtonFacebook: " + response.toString());
                                try {
                                    JSONObject jsonObject = object.getJSONObject("location");
                                    // Log.e(TAG, "onCompleted: location " + jsonObject.getString("name"));
                                    // Log.e(TAG, "onCompleted: ID " + object.getString("id"));
                                    // Log.e(TAG, "onCompleted: name " + object.getString("name"));
                                    // Log.e(TAG, "onCompleted: email " + object.getString("email"));
                                    // Log.e(TAG, "onCompleted: gender " + object.getString("gender"));
                                    // Log.e(TAG, "onCompleted: birthday " + object.getString("birthday"));
                                    // Log.e(TAG, "onCompleted: link " + object.getString("link"));

                                    ID = object.getString("id");
                                    NAMA = object.getString("name");
                                    EMAIL = object.getString("email");
                                    JENKEL = object.getString("gender");
                                    ULTAH = object.getString("birthday");
                                    LINK = object.getString("link");
                                    LOKASI = jsonObject.getString("name");
                                    SUMBERLOGIN = "facebook";
                                    PASSWORD = "";
                                    simpanKeServer();

                                } catch (JSONException e) {
                                    Log.e(TAG, "onCompleted: onSuccess: loginButtonFacebook: " + e);
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,link,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: loginButtonFacebook: ");
                Toast.makeText(getApplicationContext(), "Anda membatalkan masuk dengan Faceboook", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: loginButtonFacebook: " + error);
                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void simpanKeServer() {
        List<String> listDataPengguna = new ArrayList<>();
        listDataPengguna.add(ID);
        listDataPengguna.add(NAMA);
        listDataPengguna.add(EMAIL);
        listDataPengguna.add(JENKEL);
        listDataPengguna.add(ULTAH);
        listDataPengguna.add(LINK);
        listDataPengguna.add(LOKASI);
        listDataPengguna.add(SUMBERLOGIN);
        ServerYDIG serverYDIG = new ServerYDIG(this,"LoginData");
        synchronized (this){
            serverYDIG.sendArrayList(listDataPengguna);
        }
        simpanKeBDLocal(ID, SUMBERLOGIN, PASSWORD);
    }

    private void simpanKeBDLocal(String id, String sumberlogin, String password) {
        DBLocalHandler dbLocalHandler = new DBLocalHandler(this);
        dbLocalHandler.addUser(
                new ModelUser(1, sumberlogin, id, password)
        );
        dbLocalHandler.close();
        progressBar.setVisibility(View.GONE);
        kirimPenggunakeMainActivity();
    }

    private void kirimPenggunakeMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
