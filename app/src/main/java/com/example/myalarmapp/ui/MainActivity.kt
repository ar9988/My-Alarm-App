package com.example.myalarmapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myalarmapp.ui.theme.MyAlarmAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAlarmAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MyApp(name: String, modifier: Modifier = Modifier) {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxHeight(0.3f).fillMaxWidth(1.0f)){
            Text(
                text = "",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp).fillMaxHeight(0.3f).align(Alignment.Center)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.5f) // 전체 화면의 절반만 차지
                .padding(16.dp)
        ) {
            items(items) { item ->
                Text(text = item, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAlarmAppTheme {
        MyApp("Android")
    }
}