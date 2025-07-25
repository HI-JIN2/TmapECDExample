package com.example.tmap.ecd


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tmap.ecd.ui.theme.TmapECDExampleTheme


class SampleActivity : ComponentActivity() {

    private val viewModel: SampleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TmapECDExampleTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.finishSdk() // Ensure SDK is finished on destroy
    }
}

@Composable
fun MainScreen(viewModel: SampleViewModel) {
    val context = LocalContext.current

    val clickedText by viewModel.clickedText.collectAsState()
    val callbackText by viewModel.callbackText.collectAsState()
    val contentsText by viewModel.contentsText.collectAsState()
    val isTargetTest by viewModel.isTargetTest.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Target Button (equivalent to app:layout_constraintTop_toTopOf="parent")
        Button(
            onClick = { viewModel.toggleTargetTest() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Add some padding below the button
        ) {
            Text(text = "Target: ${if (isTargetTest) "SAMPLE HOST" else "TMAP"}")
        }

        // Contents ScrollView (equivalent to app:layout_constraintTop_toBottomOf="@id/targetButton")
        // Adjusted height for better responsiveness, using weight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Fixed height as per XML
                .verticalScroll(rememberScrollState())
                .padding(10.dp) // Padding for the inner content
        ) {
            Text(
                text = "Clicked Button : $clickedText",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth() // Matches match_parent
            )
            Text(
                text = "Callback: $callbackText",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp)) // Equivalent to android:layout_marginTop="10dp"
            Text(
                text = "Contents: $contentsText",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Buttons ScrollView (equivalent to app:layout_constraintBottom_toBottomOf="parent"
        // and app:layout_constraintTop_toBottomOf="@id/contentsScrollView")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Fills remaining height, similar to layout_height="0dp" with constraints
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between buttons
        ) {
            // Group 1: Single Buttons
            Button(
                onClick = { viewModel.initializeSdk(context = context) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Initialized") }

            Button(
                onClick = { viewModel.getVersion() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Version") }

            Button(
                onClick = { viewModel.toggleRegistration() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Regist Callback") }

            Button(
                onClick = { viewModel.getTmapInfo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Tmap Info") }

            Button(
                onClick = { viewModel.getRunningInfo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Running Info") }

            Button(
                onClick = { viewModel.getBlackBoxInfo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get BlackBox Info") }

            Button(
                onClick = { viewModel.getDriveModeInfo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Drive Mode Info") }

            Button(
                onClick = { viewModel.getRouteInfo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Route Info") }

            Button(
                onClick = { viewModel.getAddressInfo(126.9871482074634, 37.56504594206883) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Get Address") }

            // Group 2: Horizontal Buttons (Display Status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Spacing between buttons in a row
            ) {
                Button(
                    onClick = { viewModel.setDisplayForeground() },
                    modifier = Modifier.weight(1f) // Equivalent to layout_weight="1"
                ) { Text("Set Display Status FG") }

                Button(
                    onClick = { viewModel.setDisplayBackground() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Display Status BG") }
            }

            // Group 3: Horizontal Buttons (Audio Status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { viewModel.setAudioOn() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Audio Status On") }

                Button(
                    onClick = { viewModel.setAudioOff() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Audio Status Off") }
            }

            // Group 4: Single Button (Reroute)
            Button(
                onClick = { viewModel.setReroute() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Set Reroute") }

            // Group 5: Horizontal Buttons (Zoom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { viewModel.setZoomIn() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Zoom In") }

                Button(
                    onClick = { viewModel.setZoomOut() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Zoom Out") }
            }

            // Group 6: Horizontal Buttons (Volume)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { viewModel.setVolumeDown() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Volume Down") }

                Button(
                    onClick = { viewModel.setVolumeUp() },
                    modifier = Modifier.weight(1f)
                ) { Text("Set Volume Up") }
            }

            // Group 7: Single Buttons (Go Home/Company/Ando)
            Button(
                onClick = { viewModel.goHome() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Go Home") }

            Button(
                onClick = { viewModel.goCompany() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Go Company") }

            Button(
                onClick = { viewModel.goAndo() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("안심주행") }
        }
    }
}