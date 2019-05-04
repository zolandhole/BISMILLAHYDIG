package com.notio.bismillahydig;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.notio.bismillahydig.database.ServerYDIG;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private String ID,NAMA,EMAIL,JENKEL,ULTAH,LINK,LOKASI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButtonFacebook = findViewById(R.id.login_button_facebook);

        loginDenganFacebook();
    }

    private void loginDenganFacebook() {
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
                Toast.makeText(getApplicationContext(), "Anda membatalkan lgoin dengan Faceboook", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: loginButtonFacebook: " + error);
                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
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
        ServerYDIG serverYDIG = new ServerYDIG(this,"LoginData");
        synchronized (this){
            serverYDIG.sendArrayList(listDataPengguna);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
