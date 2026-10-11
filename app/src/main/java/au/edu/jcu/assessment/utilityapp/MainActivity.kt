package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import au.edu.jcu.assessment.utilityapp.data.SampleEnvironmentRepository
import au.edu.jcu.assessment.utilityapp.ui.RideReadyApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val environment = SampleEnvironmentRepository().getEnvironment()

        setContent {
            RideReadyApp(environment = environment)
        }
    }
}
