package jp.techacademy.shintaro.nakagawa.finalapp

import jp.techacademy.shintaro.nakagawa.finalapp.ui.home.HomeFragment

data class State(var cp: Array<Int>,
                 var co: Array<Int>,
                 var ep: Array<Int>,
                 var eo: Array<Int>) {

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
    var inv_face = mutableMapOf<String, String>(
            "U" to "D",
            "D" to "U",
            "L" to "R",
            "R" to "L",
            "F" to "B",
            "B" to "F"
    )
    var solved_state = State(
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )
    var move_names = String()
    var faces = moves.keys.toList()

    init {
        for (face_name in faces) {
            move_names += "$face_name, ${face_name}2, ${face_name}' "
            moves[face_name + '2'] = moves[face_name]!!.apply_move(moves[face_name]!!)
            moves[face_name + '\''] = moves[face_name]!!.apply_move(moves[face_name]!!).apply_move(moves[face_name]!!)
        }
    }

    fun getMoveNames(): String {
        return move_names
    }

    fun apply_move(move: State): State {
        var new_cp: Array<Int> = Array(move.cp.size){it -> cp[move.cp[it]]}
        var new_co: Array<Int> = Array(move.co.size){it -> (co[move.cp[it]] + move.co[it]) % 3}
        var new_ep: Array<Int> = Array(move.ep.size){it -> ep[move.ep[it]]}
        var new_eo: Array<Int> = Array(move.eo.size){it -> (eo[move.ep[it]] + move.eo[it]) % 2}
        return State(new_cp, new_co, new_ep, new_eo)
    }

    fun scramble2state(scramble: String) {
        var scrambled_state = solved_state
        for (move_name in scramble.split(" ")) {
            var move_state = moves[move_name]
            scrambled_state = scrambled_state.apply_move(move_state!!)
        }
    }

    fun is_solved(state: State): Boolean {
        return state.eo == Array(12){0} && state.co == Array(8){0} && state.ep == Array(12){it} && state.cp == Array(8){it}
    }

//    fun is_move_available(prev_move, move)
}