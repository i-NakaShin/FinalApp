package jp.techacademy.shintaro.nakagawa.finalapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProviders
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import jp.techacademy.shintaro.nakagawa.finalapp.*

class HomeFragment : AndroidFragmentApplication() {

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

        var i: Int = 2
        i = i / 3 * 3
//        Log.d("kotlintest", i.toString())
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
        var view: View = initializeForView(drawListener)
        r.addView(view)

        val solvingButton: Button = root.findViewById(R.id.solving_button)
        solvingButton.setOnClickListener {
            val intent = Intent(activity, ImageRecognitionActivity::class.java)
            startActivity(intent)
        }

        val timeAttackButton: Button = root.findViewById(R.id.time_attack_button)
        timeAttackButton.setOnClickListener {
            val intent = Intent(activity, TimeAttackActivity::class.java)
            startActivity(intent)
        }

        val vsCpuButton: Button = root.findViewById(R.id.vs_cpu_button)
        vsCpuButton.setOnClickListener {
            val intent = Intent(activity, vsCpuActivity::class.java)
            startActivity(intent)
        }

        return root
    }

}