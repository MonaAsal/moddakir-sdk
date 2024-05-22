package com.moddakir.androidlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.moddakir.androidlibrary.ui.theme.AndroidLibraryTheme
import com.moddakir.call.view.agora.AgoraActivity.CallRandomTeacher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidLibraryTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                   CallRandomTeacher(
                        this@MainActivity,
                        "male",
                        "ahmed",
                        "+201112072662","mona@gmail.com","ar"
                    )

                }
            }
        }

    }
}
