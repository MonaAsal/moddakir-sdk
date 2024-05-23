package com.moddakir.androidlibrary.ui;

import static com.moddakir.call.view.agora.AgoraActivity.makeCall;
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

public class NusukTest extends LocalizationActivity {
    Button callTeacher;
    EditText name,gender,phone,email,language;
    String lang="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nusuk_test);
        callTeacher=findViewById(R.id.callTeacher_btn);
        name=findViewById(R.id.name);
        gender=findViewById(R.id.gender);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        language=findViewById(R.id.language);
        callTeacher.setOnClickListener(view -> {
            lang=language.getText().toString().toLowerCase();
            if(lang.equals("ar")||lang.equals("en")||lang.equals("fr")||lang.equals("in")||lang.equals("ur")){
                setLanguageWithoutNotification( language.getText().toString());
                Perference.setLang(this,  language.getText().toString());
                makeCall(NusukTest.this,
                        gender.getText().toString(),
                        name.getText().toString(),
                        phone.getText().toString(),
                        email.getText().toString(),
                        language.getText().toString());

            }
        });
    }



}