package com.moddakir.androidlibrary.ui;

import static com.moddakir.call.view.agora.AgoraActivity.CallRandomTeacher;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.moddakir.call.R;
import com.moddakir.call.utils.Perference;

import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class NusukTest extends LocalizationActivity implements EasyPermissions.PermissionCallbacks {
    Button callTeacher;
    EditText name,gender,phone,email,language;
    String lang="";
    String perm= Manifest.permission.RECORD_AUDIO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nusuk_test);
        addOverlay();
        callTeacher=findViewById(R.id.callTeacher_btn);
        name=findViewById(R.id.name);
        gender=findViewById(R.id.gender);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        language=findViewById(R.id.language);
        callTeacher.setOnClickListener(view -> {

            if (EasyPermissions.hasPermissions(NusukTest.this, perm)) {
                lang=language.getText().toString().toLowerCase();
                if(lang.equals("ar")||lang.equals("en")||lang.equals("fr")||lang.equals("in")||lang.equals("ur")){
                    setLanguageWithoutNotification( language.getText().toString());
                    Perference.setLang(this,  language.getText().toString());
                    CallRandomTeacher(NusukTest.this,
                            gender.getText().toString(),
                            name.getText().toString(),
                            phone.getText().toString(),
                            email.getText().toString(),
                            language.getText().toString());
                }
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) NusukTest.this).requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
                }
            }
        });
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (EasyPermissions.hasPermissions(NusukTest.this, perm)) {
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) NusukTest.this).requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
            }
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, getResources().getString(R.string.permission_request), Toast.LENGTH_LONG).show();
    }
    public void addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                askDrawPermission();
            }
        }
    }
    @TargetApi(23)
    public void askDrawPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }
}