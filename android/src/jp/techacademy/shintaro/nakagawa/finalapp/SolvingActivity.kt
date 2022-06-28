package jp.techacademy.shintaro.nakagawa.finalapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.*
import com.badlogic.gdx.backends.android.AndroidApplication

class SolvingActivity : AppCompatActivity() {
    val symbolList = hashMapOf<Char, Int>(
            'U' to 0,
            'D' to 1,
            'L' to 2,
            'R' to 3,
            'F' to 4,
            'B' to 5
    )
    val description = hashMapOf<Int, String>(
            0 to "時計回りに90°回転",
            1 to "反時計回りに90°回転",
            2 to "180°回転"
    )
    lateinit var sAdapter: SolveAdapter
    var faceColor = mutableMapOf<Int, String>()
    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solving)
        supportActionBar!!.hide()

        val cubelets = intent.extras!!.getSerializable("cubelets") as Array<Array<Array<Cubelet?>>>
        var solveArray = mutableListOf<Int>()
        val state = cubelet_to_state(cubelets)

        val prevButton: Button = findViewById(R.id.prev_button)
        val nextButton: Button = findViewById(R.id.next_button)
//        val r: RelativeLayout = findViewById(R.id.solving_gdx_view)

        val solutionList: ListView = findViewById(R.id.symbol_list)
        var data = mutableListOf<String>()

        val moves_tmp = moves.keys.toSet()
        for (k in moves_tmp) {
            move_names += "$k ${k}2 ${k}' "
            moves[k + '2'] = moves[k]!!.apply_move(moves[k]!!)
            moves[k + '\''] = moves[k]!!.apply_move(moves[k]!!).apply_move(moves[k]!!)
        }

        if (state != null) {
            val search = Search(state, moves, move_names)
            val solution = search.start_search()
            if (!solution.isNullOrEmpty()) {
                Log.d("kotlintest", "Solution: $solution")
                data.add("〜 Solution 〜")
                for (i in solution.indices) {
                    var symbol = ""
                    if (solution[i].length == 1) {
                        symbol = "  ：センター色が${faceColor[symbolList[solution[i][0]]!!]}の面を${description[0]}"
                        solveArray.add(symbolList[solution[i][0]]!!)
                    } else {
                        if (solution[i][1] == '\'') {
                            symbol = " ：センター色が${faceColor[symbolList[solution[i][0]]!!]}の面を${description[1]}"
                            solveArray.add(symbolList[solution[i][0]]!! + 10)
                        } else {
                            symbol = "：センター色が${faceColor[symbolList[solution[i][0]]!!]}の面を${description[2]}"
                            solveArray.add(symbolList[solution[i][0]]!! + 20)
                        }
                    }
                    val revolution = if (i + 1 < 10) {
                        "  ${i + 1}. ${solution[i]}$symbol"
                    } else {
                        "${i + 1}. ${solution[i]}$symbol"
                    }
                    data.add(revolution)
                }
                data.add("〜 Resolved 〜")
            } else {
                Log.d("kotlintest", "Solution not found.")
                data.add("Solution not found.")
            }
        } else {
            Log.d("kotlintest", "Solution not found.")
            data.add("Solution not found.")
        }
        sAdapter = SolveAdapter(this, data, num)
        solutionList.adapter = sAdapter

//        val drawListener = CubeSolve(isSolving = true, colorArray = cubelets, solveArray = solveArray.slice(0 until num).toMutableList())
//        val view: View = initializeForView(drawListener)
//        r.addView(view)

        prevButton.setOnClickListener {
            if (num == 0) {
                num = data.size
            }
            num--
            sAdapter.update(num)
            sAdapter.notifyDataSetChanged()

            if (num > 0) {
//                val drawListener = CubeSolve(isSolving = true, colorArray = cubelets, solveArray = solveArray.slice(0 until num).toMutableList())
//                val view: View = initializeForView(drawListener)
//                r.removeAllViews()
//                r.addView(view)
            }
        }
        nextButton.setOnClickListener {
            if (num == data.size) {
                num = 0
            }
            num++
            sAdapter.update(num)
            sAdapter.notifyDataSetChanged()

            if (num < data.size - 1) {
//                val drawListener = CubeSolve(isSolving = true, colorArray = cubelets, solveArray = solveArray.slice(0 until num).toMutableList())
//                val view: View = initializeForView(drawListener)
//                r.removeAllViews()
//                r.addView(view)
            }
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

    fun cubelet_to_state(cubelet: Array<Array<Array<Cubelet?>>>): State? {
        val default_cp = arrayOf(arrayOf(0, 2, 5), arrayOf(0, 5, 3), arrayOf(0, 3, 4), arrayOf(0, 4, 2),
                arrayOf(1, 5, 2), arrayOf(1, 3, 5), arrayOf(1, 4, 3), arrayOf(1, 2, 4))
        val default_ep = arrayOf(arrayOf(5, 2), arrayOf(5, 3), arrayOf(4, 3), arrayOf(4, 2), arrayOf(0, 5), arrayOf(0, 3),
                arrayOf(0, 4), arrayOf(0, 2), arrayOf(1, 5), arrayOf(1, 3), arrayOf(1, 4), arrayOf(1, 2))
        val faceList = mutableMapOf(0 to PlainCubelet.CubeletSide.TOP, 1 to PlainCubelet.CubeletSide.BOTTOM,
                2 to PlainCubelet.CubeletSide.WEST, 3 to PlainCubelet.CubeletSide.EAST,
                4 to PlainCubelet.CubeletSide.SOUTH, 5 to PlainCubelet.CubeletSide.NORTH)
        val colorList = mutableMapOf(cubelet[1][2][1]!!.getColor(faceList[0]) to 0, cubelet[1][0][1]!!.getColor(faceList[1]) to 1,
                cubelet[0][1][1]!!.getColor(faceList[2]) to 2, cubelet[2][1][1]!!.getColor(faceList[3]) to 3,
                cubelet[1][1][2]!!.getColor(faceList[4]) to 4, cubelet[1][1][0]!!.getColor(faceList[5]) to 5)
        var cp = Array<Int>(8){0}
        var co = Array<Int>(8){0}
        var ep = Array<Int>(12){0}
        var eo = Array<Int>(12){0}
        var c = Array<Array<Int>>(default_cp.size) { Array<Int>(default_cp[0].size) {0} }
        var e = Array<Array<Int>>(default_ep.size) { Array<Int>(default_ep[0].size) {0} }

        for ((k, v) in colorList) {
            when (k) {
                PlainCubelet.CubeletColor.WHITE  -> faceColor[v] = "白"
                PlainCubelet.CubeletColor.RED    -> faceColor[v] = "赤"
                PlainCubelet.CubeletColor.GREEN  -> faceColor[v] = "緑"
                PlainCubelet.CubeletColor.BLUE   -> faceColor[v] = "青"
                PlainCubelet.CubeletColor.ORANGE -> faceColor[v] = "橙"
                PlainCubelet.CubeletColor.YELLOW -> faceColor[v] = "黄"
                else -> return null
            }
        }

        for (i in c.indices) {
            for (j in c[i].indices) {
                val x = (i + 1) / 2 % 2 * 2
                val z = i / 2 % 2 * 2
                if (i < c.size / 2) {
                    c[i][j] = colorList[cubelet[x][2][z]!!.getColor(faceList[default_cp[i][j]])] ?: return null
                } else {
                    c[i][j] = colorList[cubelet[x][0][z]!!.getColor(faceList[default_cp[i][j]])] ?: return null
                }
            }
        }

        val e_num = arrayOf(1, 2, 1, 0)
        for (i in e.indices) {
            for (j in e[i].indices) {
                var x = e_num[i % 4]
                var z = e_num[3 - i % 4]
                if (i < e.size / 3) {
                    x = (i + 1) / 2 % 2 * 2
                    z = i / 2 % 2 * 2
                    e[i][j] = colorList[cubelet[x][1][z]!!.getColor(faceList[default_ep[i][j]])] ?: return null
                } else if (i < e.size / 3 * 2) {
                    e[i][j] = colorList[cubelet[x][2][z]!!.getColor(faceList[default_ep[i][j]])] ?: return null
                } else {
                    e[i][j] = colorList[cubelet[x][0][z]!!.getColor(faceList[default_ep[i][j]])] ?: return null
                }
            }
        }

        for (i in c.indices) {
            val c_sorted = c[i].sorted()
            var num = 0
            for (j in default_cp.indices) {
                val default_cp_sorted = default_cp[j].sorted()
                for (k in default_cp[j].indices)
                    if (default_cp_sorted[k] != c_sorted[k]) {
                        break
                    } else {
                        if (c[i][k] == default_cp[j][0]) num = k
                        if (k == 2) {
                            cp[i] = j
                            co[i] = num
                        }
                    }
            }
        }

        for (i in e.indices) {
            val e_sorted = e[i].sorted()
            var num = 0
            for (j in default_ep.indices) {
                val default_ep_sorted = default_ep[j].sorted()
                for (k in default_ep[j].indices)
                    if (default_ep_sorted[k] != e_sorted[k]) {
                        break
                    } else {
                        if (e[i][k] == default_ep[j][0]) num = k
                        if (k == 1) {
                            ep[i] = j
                            eo[i] = num
                        }
                    }
            }
        }

        return State(cp, co, ep, eo)
    }
}