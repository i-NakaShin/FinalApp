package jp.techacademy.shintaro.nakagawa.finalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AndroidApplication() , AndroidFragmentApplication.Callbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val r: RelativeLayout = findViewById(R.id.home_gdx_view)
        val drawListener = CubeSolve()
        var view: View = initializeForView(drawListener)
        r.addView(view)

        val solvingButton: Button = findViewById(R.id.solving_button)
        solvingButton.setOnClickListener {
            val intent = Intent(this, ImageRecognitionActivity::class.java)
            startActivity(intent)
        }

        val timeAttackButton: Button = findViewById(R.id.time_attack_button)
        timeAttackButton.setOnClickListener {
            val intent = Intent(this, TimeAttackActivity::class.java)
            startActivity(intent)
        }

        val vsCpuButton: Button = findViewById(R.id.vs_cpu_button)
        vsCpuButton.setOnClickListener {
            val intent = Intent(this, vsCpuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun exit() {
        finish()
    }
}