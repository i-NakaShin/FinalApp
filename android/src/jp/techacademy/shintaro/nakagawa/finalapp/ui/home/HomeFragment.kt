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
//        var list: Array<Array<Array<Cubelet?>>> = arrayOf(arrayOf(arrayOf()))
//        Log.d("kotlintest", list!!.size.toString())
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
//        drawListener.cube!!.rotateColumn(0)
        var view: View = initializeForView(drawListener)
        r.addView(view)
//        drawListener.cube = Cube(4, false)

        val solvingButton: Button = root.findViewById(R.id.solving_button)
        solvingButton.setOnClickListener {
            val intent = Intent(activity, ImageRecognitionActivity::class.java)
            startActivity(intent)
        }

        val timeAttackButton: Button = root.findViewById(R.id.time_attack_button)
        timeAttackButton.setOnClickListener {
//            val intent = Intent(activity, TimeAttackActivity::class.java)
//            startActivity(intent)
            view = initializeForView(CubeSolve(true))
            r.removeAllViews()
            r.addView(view)
        }

        val vsCpuButton: Button = root.findViewById(R.id.vs_cpu_button)
        vsCpuButton.setOnClickListener {
//            val intent = Intent(activity, vsCpuActivity::class.java)
//            startActivity(intent)
            view = initializeForView(CubeSolve())
            r.removeAllViews()
            r.addView(view)
        }

        return root
    }

}