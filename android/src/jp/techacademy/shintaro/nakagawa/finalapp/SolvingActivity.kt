package jp.techacademy.shintaro.nakagawa.finalapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.badlogic.gdx.backends.android.AndroidApplication

class SolvingActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solving)

        val state = intent.extras!!.getSerializable("state") as State
        val r: RelativeLayout = findViewById(R.id.solving_gdx_view)
        val drawListener = CubeSolve()
        val view: View = initializeForView(drawListener)
        r.addView(view)
        val solutionText: TextView = findViewById(R.id.symbol_list)

        val moves_tmp = moves.keys.toSet()
        for (k in moves_tmp) {
            move_names += "$k ${k}2 ${k}' "
            moves[k + '2'] = moves[k]!!.apply_move(moves[k]!!)
            moves[k + '\''] = moves[k]!!.apply_move(moves[k]!!).apply_move(moves[k]!!)
        }
        val search = Search(state, moves, move_names)
        val solution = search.start_search()
        if (!solution.isNullOrEmpty()) {
            Log.d("kotlintest", "Solution: $solution")
            var revolution = ""
            for (i in solution.indices) {
                revolution += solution[i]
                if (i != solution.size - 1){
                    revolution += " "
                }
            }
            solutionText.text = revolution
        } else {
            Log.d("kotlintest", "Solution not found.")
        }
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

        var solved_state = State(
                arrayOf(0, 1, 2, 3, 4, 5, 6, 7),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )

        var move_names = String()
    }
}