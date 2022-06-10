package jp.techacademy.shintaro.nakagawa.finalapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.navigation.NavigationView
import jp.techacademy.shintaro.nakagawa.finalapp.*

class HomeFragment : AndroidFragmentApplication() {
//class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private var fragmentCallback : Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            fragmentCallback = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

//        for (i in 0 until 100 step 10) {
//            Log.d("kotlintest", i.toString())
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val r: RelativeLayout = root.findViewById(R.id.home_gdx_view)
        val drawListener = CubeSolve()
        val view: View = initializeForView(drawListener)
        r.addView(view)

        val solving_button: Button = root.findViewById(R.id.solving_button)
        solving_button.setOnClickListener {
            val intent = Intent(activity, ImageRecognitionActivity::class.java)
            startActivity(intent)
        }

        val time_attack_button: Button = root.findViewById(R.id.time_attack_button)
        time_attack_button.setOnClickListener {
            val intent = Intent(activity, TimeAttackActivity::class.java)
            startActivity(intent)
        }

        val vs_cpu_button: Button = root.findViewById(R.id.vs_cpu_button)
        vs_cpu_button.setOnClickListener {
            val intent = Intent(activity, vsCpuActivity::class.java)
            startActivity(intent)
        }

        return root
    }
}