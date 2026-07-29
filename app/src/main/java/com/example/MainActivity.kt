package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.HayInfoMainScreen
import com.example.ui.WebViewModel
import com.example.ui.theme.HayInfoTheme

class MainActivity : ComponentActivity() {
  private val webViewModel: WebViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HayInfoTheme {
        HayInfoMainScreen(viewModel = webViewModel)
      }
    }
  }
}

