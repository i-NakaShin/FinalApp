package jp.techacademy.shintaro.nakagawa.finalapp

import java.io.Serializable

data class State(var cp: Array<Int>,
                 var co: Array<Int>,
                 var ep: Array<Int>,
                 var eo: Array<Int>) : Serializable {

    fun apply_move(move: State): State {
        val new_cp: Array<Int> = Array(move.cp.size) { it -> cp[move.cp[it]] }
        val new_co: Array<Int> = Array(move.cp.size) { it -> (co[move.cp[it]] + move.co[it]) % 3 }
        val new_ep: Array<Int> = Array(move.ep.size) { it -> ep[move.ep[it]] }
        val new_eo: Array<Int> = Array(move.ep.size) { it -> (eo[move.ep[it]] + move.eo[it]) % 2 }
        return State(new_cp, new_co, new_ep, new_eo)
    }
}