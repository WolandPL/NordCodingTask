package com.wolandpl.nordcodingtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wolandpl.nordcodingtask.ui.compose.MainScreen
import com.wolandpl.nordcodingtask.ui.compose.theme.NordCodingTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NordCodingTaskTheme {
                MainScreen()
            }
        }
    }
}
