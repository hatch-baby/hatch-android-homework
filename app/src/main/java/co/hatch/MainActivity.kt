package co.hatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.hatch.deviceClientLib.connectivity.ConnectivityClient

class MainActivity : AppCompatActivity() {

    private val connectivityClient = ConnectivityClient.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}