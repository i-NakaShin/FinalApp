package jp.techacademy.shintaro.nakagawa.finalapp

class Search(initial_state: State) {
    var current_solution_ph1 = arrayOf<String>()
    var co_move_table: Array<Array<Int>>
    var eo_move_table: Array<Array<Int>>
    var e_combination_table: Array<Array<Int>>
    var cp_move_table: Array<Array<Int>>
    var ud_ep_move_table: Array<Array<Int>>
    var e_ep_move_table: Array<Array<Int>>
    var co_eec_prune_table: Array<Array<Int>>
    var distance = 0
    var num_filled = 1
    var move_names = mutableListOf<String>()
    var move_name_ph2 = arrayOf("U", "U2", "U'", "D", "D2", "D'", "L2", "R2", "F2", "B2")

    init {
        for (move_name in initial_state.move_names.split(" ")) {
            move_names.add(move_name)
        }
        co_move_table = Array<Array<Int>>(NUM_CO){ Array<Int>(move_names.length){0} }
        for (i in 0 until NUM_CO) {
            var state = State(
                    Array<Int>(8){0},
                    index_to_co(i),
                    Array<Int>(12){0},
                    Array<Int>(12){0}
            )
            for (i_move in 0 until move_names.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                co_move_table[i][i_move] = co_to_index(new_state.co)
            }
        }
        eo_move_table = Array<Array<Int>>(NUM_EO){ Array<Int>(move_names.length){0} }
        for (i in 0 until NUM_EO) {
            var state = State(
                    Array<Int>(8){0},
                    Array<Int>(8){0},
                    Array<Int>(12){0},
                    index_to_eo(i)
            )
            for (i_move in 0 until move_names.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                eo_move_table[i][i_move] = eo_to_index(new_state.eo)
            }
        }
        e_combination_table = Array<Array<Int>>(NUM_E_COMBINATIONS){ Array<Int>(move_names.length){0} }
        for (i in 0 until NUM_E_COMBINATIONS) {
            var state = State(
                    Array<Int>(8){0},
                    Array<Int>(8){0},
                    index_to_e_combination(i),
                    Array<Int>(12){0}
            )
            for (i_move in 0 until move_names.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                e_combination_table[i][i_move] = e_combination_to_index(new_state.ep)
            }
        }
        cp_move_table = Array<Array<Int>>(NUM_CP){ Array<Int>(move_names_ph2.length){0} }
        for (i in 0 until NUM_CP) {
            var state = State(
                    index_to_cp(i),
                    Array<Int>(8){0},
                    Array<Int>(12){0},
                    Array<Int>(12){0}
            )
            for (i_move in 0 until move_names_ph2.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                cp_move_table[i][i_move] = cp_to_index(new_state.cp)
            }
        }
        ud_ep_move_table = Array<Array<Int>>(NUM_UD_EP){ Array<Int>(move_names_ph2.length){0} }
        for (i in 0 until NUM_UD_EP) {
            var state = State(
                    Array<Int>(8){0},
                    Array<Int>(8){0},
                    mutableListOf<Int>(0,0,0,0).addAll(index_to_ud_ep(i)),
                    Array<Int>(12){0}
            )
            for (i_move in 0 until move_names_ph2.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                ud_ep_move_table[i][i_move] = ud_ep_to_index(new_state.ep.slice(4))
            }
        }
        e_ep_move_table = Array<Array<Int>>(NUM_E_EP){ Array<Int>(move_names_ph2.length){0} }
        for (i in 0 until NUM_E_EP) {
            var state = State(
                    Array<Int>(8){0},
                    Array<Int>(8){0},
                    index_to_e_ep(i).toMutableList().addAll(listOf(0, 0, 0, 0, 0, 0, 0, 0)),
                    Array<Int>(12){0}
            )
            for (i_move in 0 until move_names_ph2.size) {
                var new_state = state.apply_move(initial_state.moves[move_names[i_move]]!!)
                e_ep_move_table[i][i_move] = e_ep_to_index(new_state.ep.slice(0, 4))
            }
        }
        co_eec_prune_table = Array<Array<Int>>(NUM_E_EP){ Array<Int>(NUM_E_COMBINATIONS){-1}
    }

    fun co_to_index(co: Array<Int>): Int {
        var index = 0
        for (co_i in 0 until co.size - 1) {
            index *= 3
            index += co[co_i]
        }
        return index
    }

    fun index_to_co(index: Int): Array<Int> {
        var index_tmp = index
        var co = Array<Int>(8){0}
        var sum_co = 0
        for(i in 6 downTo 0) {
            co[i] = index_tmp % 3
            index_tmp /= 3
            sum_co += co[i]
        }
        co[7] = (3 - sum_co % 3) % 3

        return co
    }

    fun eo_to_index(eo: Array<Int>): Int {
        var index = 0
        for (eo_i in 0 until eo.size - 1) {
            index *= 2
            index += eo[eo_i]
        }
        return index
    }

    fun index_to_eo(index: Int): Array<Int> {
        var index_tmp = index
        var eo = Array<Int>(12){0}
        var sum_eo = 0
        for(i in 10 downTo 0) {
            eo[i] = index_tmp % 2
            index_tmp /= 2
            sum_eo += eo[i]
        }
        eo[11] = (2 - sum_eo % 2) % 2

        return eo
    }

    fun calc_combination(n: Int, r:Int): Int {
        var ret = 1
        for (i in 0 until r) ret *= n - i
        for (i in 0 until r) ret /= r - i
        return ret
    }

    fun e_combination_to_index(comb: Array<Int>): Int {
        var index = 0
        var r = 4
        for (i in 12 - 1 downTo 0) {
            if (comb[i] == 1) {
                index += calc_combination(i, r)
                r -= 1
            }
        }
        return index
    }

    fun index_to_e_combination(index: Int): Array<Int> {
        var index_tmp = index
        var combination = Array<Int>(12){0}
        var r = 4
        for (i in 12 - 1 downTo 0) {
            if (index_tmp >= calc_combination(i, r)) {
                combination[i] = 1
                index_tmp -= calc_combination(i, r)
                r -= 1
            }
        }
        return combination
    }

    fun cp_to_index(cp: Array<Int>): Int {
        var index = 0
        for (i in cp.indices) {
            index *= 8 - i
            for (j in i + 1 until 8){
                if (cp[i] > cp[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_cp(index: Int): Array<Int> {
        var index_tmp = index
        var cp = Array<Int>(8){0}
        for (i in 6 downTo 0) {
            cp[i] = index_tmp % (8 - i)
            index_tmp /= 8 - i
            for (j in i + 1 until 8) {
                if (cp[j] >= cp[i]) {
                    cp[j] += 1
                }
            }
        }
        return cp
    }

    fun ud_ep_to_index(ep: Array<Int>): Int {
        var index = 0
        for (i in ep.indices) {
            index *= 8 - i
            for (j in i + 1 until 8){
                if (ep[i] > ep[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_ud_ep(index: Int): Array<Int> {
        var index_tmp = index
        var ep = Array<Int>(8){0}
        for (i in 6 downTo 0) {
            ep[i] = index_tmp % (8 - i)
            index_tmp /= 8 - i
            for (j in i + 1 until 8) {
                if (ep[j] >= ep[i]) {
                    ep[j] += 1
                }
            }
        }
        return ep
    }

    fun e_ep_to_index(eep: Array<Int>): Int {
        var index = 0
        for (i in eep.indices) {
            index *= 4 - i
            for (j in i + 1 until 4) {
                if (eep[i] > eep[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_e_ep(index: Int): Array<Int> {
        var index_tmp = index
        var eep = Array<Int>(4){0}
        for (i in 4 - 2 downTo 0) {
            eep[i] = index_tmp % (4 - i)
            index_tmp /= 4 - i
            for (j in i + 1 until 4) {
                if (eep[j] >= eep[i]) {
                    eep[j] += 1
                }
            }
        }
        return eep
    }

    fun depth_limited_search_ph1(co_index: Int, eo_index: Int, e_comb_index: Int, depth: Int): Boolean {
        if (depth == 0 && co_index == 0 && eo_index == 0 && e_comb_index == 0) {
            return true
        }
        if (depth == 0) {
            return false
        }
//        if (max(c))
        return true
    }

    companion object {
        val NUM_CORNERS = 8
        val NUM_EDGES = 12

        val NUM_CO = 2187  // 3 ** 7
        val NUM_EO = 2048  // 2 ** 11
        val NUM_E_COMBINATIONS = 495  // 12C4

        val NUM_CP = 40320  // 8!
        //        val NUM_EP = 479001600  // 12! # これは使わない
        val NUM_UD_EP = 40320  // 8!
        val NUM_E_EP = 24  // 4!

        val move_names_ph2 = arrayOf("U", "U2", "U'", "D", "D2", "D'", "L2", "R2", "F2", "B2")
    }
}