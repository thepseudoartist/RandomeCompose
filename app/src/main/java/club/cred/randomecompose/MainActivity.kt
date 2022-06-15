package club.cred.randomecompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import club.cred.randomecompose.widgets.FancyGrid
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainActivityContent()
        }
    }

    @Composable
    fun MainActivityContent() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var data by remember { mutableStateOf<SensorData?>(null) }

        DisposableEffect(Unit) {
            val dataManager = SensorDataManager(context)
            dataManager.init()

            val job = scope.launch {
                dataManager.data.receiveAsFlow()
                    .onEach { data = it }
                    .collect()
            }

            onDispose {
                job.cancel()
                dataManager.cancel()
            }
        }

        FancyGrid(data = data)
    }
}