package jp.techacademy.shintaro.nakagawa.finalapp

import jp.techacademy.shintaro.nakagawa.finalapp.ui.home.HomeFragment

data class State(var cp: Array<Int> = arrayOf(0, 1, 2, 3, 4, 5, 6, 7),
                 var co: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
                 var ep: Array<Int> = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                 var eo: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)) {

    fun apply_move(move: State): State {
        val new_cp: Array<Int> = Array(move.cp.size){ it -> cp[move.cp[it]]}
        val new_co: Array<Int> = Array(move.cp.size){ it -> (co[move.cp[it]] + move.co[it]) % 3}
        val new_ep: Array<Int> = Array(move.ep.size){ it -> ep[move.ep[it]]}
        val new_eo: Array<Int> = Array(move.ep.size){ it -> (eo[move.ep[it]] + move.eo[it]) % 2}
        return State(new_cp, new_co, new_ep, new_eo)
    }

//    fun is_solved(state: State): Boolean {
//        return state.eo == Array(12){0} && state.co == Array(8){0} && state.ep == Array(12){it} && state.cp == Array(8){it}
//    }

//    fun is_move_available(prev_move, move)
}