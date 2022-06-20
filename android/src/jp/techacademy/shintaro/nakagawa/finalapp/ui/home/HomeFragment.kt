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

//        var i: Int = 0
//        i /= 3 * 3
//        Log.d("kotlintest", i.toString())
//        var move = arrayOf(3,5,1,4,2,6,0)
//        var cp = arrayOf(1,3,2,5,4,6,0)
//        var new_cp: Array<Int> = Array(move.size){it -> cp[move[it]]}
//        Log.d("kotlintest", new_cp.contentToString())
        var move_names = String()
        var faces = moves.keys.toList()
        for (face_name in faces) {
            move_names += "$face_name, ${face_name}2, ${face_name}' "
            moves[face_name + '2'] = moves[face_name]!!.apply_move(moves[face_name]!!)
            moves[face_name + '\''] = moves[face_name]!!.apply_move(moves[face_name]!!).apply_move(moves[face_name]!!)
        }
        var solved_state = State(
                arrayOf(0, 1, 2, 3, 4, 5, 6, 7),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )
        val scramble = "L D2 R U2 L F2 U2 L F2 R2 B2 R U' R' U2 F2 R' D B' F2"
        var scrambled_state = solved_state
        for (move_name in scramble.split(" ")) {
            var move_state = moves[move_name]
            scrambled_state = scrambled_state.apply_move(move_state!!)
        }
        Log.d("kotlintest", scrambled_state.cp.contentToString())
        Log.d("kotlintest", scrambled_state.co.contentToString())
        Log.d("kotlintest", scrambled_state.ep.contentToString())
        Log.d("kotlintest", scrambled_state.eo.contentToString())

//        Log.d("kotlintest", move_names)
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

    companion object {
        val moves = mutableMapOf<String, State>(
                "U" to State(arrayOf(3, 0, 1, 2, 4, 5, 6, 7),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
                             arrayOf(0, 1, 2, 3, 7, 4, 5, 6, 8, 9, 10, 11),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),

                "D" to State(arrayOf(0, 1, 2, 3, 5, 6, 7, 4),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
                             arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 8),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),

                "L" to State(arrayOf(4, 1, 2, 0, 7, 5, 6, 3),
                             arrayOf(2, 0, 0, 1, 1, 0, 0, 2),
                             arrayOf(11, 1, 2, 7, 4, 5, 6, 0, 8, 9, 10, 3),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),

                "R" to State(arrayOf(0, 2, 6, 3, 4, 1, 5, 7),
                             arrayOf(0, 1, 2, 0, 0, 2, 1, 0),
                             arrayOf(0, 5, 9, 3, 4, 2, 6, 7, 8, 1, 10, 11),
                             arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),

                "F" to State(arrayOf(0, 1, 3, 7, 4, 5, 2, 6),
                             arrayOf(0, 0, 1, 2, 0, 0, 2, 1),
                             arrayOf(0, 1, 6, 10, 4, 5, 3, 7, 8, 9, 2, 11),
                             arrayOf(0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0)),

                "B" to State(arrayOf(1, 5, 2, 3, 0, 4, 6, 7),
                             arrayOf(1, 2, 0, 0, 2, 1, 0, 0),
                             arrayOf(4, 8, 2, 3, 1, 5, 6, 7, 0, 9, 10, 11),
                             arrayOf(1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0)),
        )
    }
}