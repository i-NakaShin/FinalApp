package jp.techacademy.shintaro.nakagawa.finalapp

import android.util.Log
import jp.techacademy.shintaro.nakagawa.finalapp.ui.home.HomeFragment
import kotlinx.coroutines.*
import java.lang.Math.max
import kotlin.system.measureTimeMillis

class Search(val initial_state: State, moves_arg: MutableMap<String, State>, move_names: String) {
    var state = initial_state
    var current_solution_ph1 = mutableListOf<String>()
    var current_solution_ph2 = mutableListOf<String>()
    var max_solution_length = 9999
    var co_move_table: Array<Array<Int>>
    var eo_move_table: Array<Array<Int>>
    var e_combination_table: Array<Array<Int>>
    var cp_move_table: Array<Array<Int>>
    var ud_ep_move_table: Array<Array<Int>>
    var e_ep_move_table: Array<Array<Int>>
    var co_eec_prune_table: Array<Array<Int>>
    var eo_eec_prune_table: Array<Array<Int>>
    var cp_eep_prune_table: Array<Array<Int>>
    var udep_eep_prune_table: Array<Array<Int>>
    var distance = 0
    var num_filled = 1
    var moves = moves_arg
    var move_namesList = mutableListOf<String>()
    var move_names_to_index = mutableMapOf<String, Int>()
    var move_names_to_index_ph2 = mutableMapOf<String, Int>()
    val move_names_ph2 = arrayOf("U", "U2", "U'", "D", "D2", "D'", "L2", "R2", "F2", "B2")
    var inv_face = mapOf<Char, Char>(
            'U' to 'D',
            'D' to 'U',
            'L' to 'R',
            'R' to 'L',
            'F' to 'B',
            'B' to 'F'
    )
    var num_face = mapOf<Char, Int>(
            'U' to 2,
            'D' to 1,
            'L' to 4,
            'R' to 3,
            'F' to 6,
            'B' to 5
    )

    init {
        for (move_name in move_names.split(" ")) {
            move_namesList.add(move_name)
        }
        move_namesList.removeLast()
        for ((i, move_name) in move_namesList.withIndex()) {
            move_names_to_index[move_name] = i
        }
        for ((i, move_name) in move_names_ph2.withIndex()) {
            move_names_to_index_ph2[move_name] = i
        }
        co_move_table = Array<Array<Int>>(NUM_CO) { Array<Int>(move_namesList.size) { 0 } }
        for (i in 0 until NUM_CO) {
            var state = State(
                    Array<Int>(8) { 0 },
                    index_to_co(i),
                    Array<Int>(12) { 0 },
                    Array<Int>(12) { 0 }
            )

            for (i_move in 0 until move_namesList.size) {
                var new_state = state.apply_move(moves[move_namesList[i_move]]!!)
                co_move_table[i][i_move] = co_to_index(new_state.co)
            }
        }
        eo_move_table = Array<Array<Int>>(NUM_EO) { Array<Int>(move_namesList.size) { 0 } }
        for (i in 0 until NUM_EO) {
            var state = State(
                    Array<Int>(8) { 0 },
                    Array<Int>(8) { 0 },
                    Array<Int>(12) { 0 },
                    index_to_eo(i)
            )
            for (i_move in 0 until move_namesList.size) {
                var new_state = state.apply_move(moves[move_namesList[i_move]]!!)
                eo_move_table[i][i_move] = eo_to_index(new_state.eo)
            }
        }
        e_combination_table = Array<Array<Int>>(NUM_E_COMBINATIONS) { Array<Int>(move_namesList.size) { 0 } }
        for (i in 0 until NUM_E_COMBINATIONS) {
            var state = State(
                    Array<Int>(8) { 0 },
                    Array<Int>(8) { 0 },
                    index_to_e_combination(i),
                    Array<Int>(12) { 0 }
            )
            for (i_move in 0 until move_namesList.size) {
                var new_state = state.apply_move(moves[move_namesList[i_move]]!!)
                e_combination_table[i][i_move] = e_combination_to_index(new_state.ep)
            }
        }
        cp_move_table = Array<Array<Int>>(NUM_CP) { Array<Int>(move_names_ph2.size) { 0 } }
        for (i in 0 until NUM_CP) {
            var state = State(
                    index_to_cp(i),
                    Array<Int>(8) { 0 },
                    Array<Int>(12) { 0 },
                    Array<Int>(12) { 0 }
            )
            for (i_move in 0 until move_names_ph2.size) {
                var new_state = state.apply_move(moves[move_names_ph2[i_move]]!!)
                cp_move_table[i][i_move] = cp_to_index(new_state.cp)
            }
        }
        ud_ep_move_table = Array<Array<Int>>(NUM_UD_EP) { Array<Int>(move_names_ph2.size) { 0 } }
        for (i in 0 until NUM_UD_EP) {
            var ep = mutableListOf<Int>(0, 0, 0, 0)
            ep.addAll(index_to_ud_ep(i))
            var state = State(
                    Array<Int>(8) { 0 },
                    Array<Int>(8) { 0 },
                    ep.stream().toArray { arrayOfNulls<Int>(it) },
                    Array<Int>(12) { 0 }
            )
            for (i_move in move_names_ph2.indices) {
                var new_state = state.apply_move(moves[move_names_ph2[i_move]]!!)
                ud_ep_move_table[i][i_move] = ud_ep_to_index(new_state.ep.slice(4 until new_state.ep.size).stream().toArray { arrayOfNulls<Int>(it) })
            }
        }
        e_ep_move_table = Array<Array<Int>>(NUM_E_EP) { Array<Int>(move_names_ph2.size) { 0 } }
        for (i in 0 until NUM_E_EP) {
            var ep = index_to_e_ep(i).toMutableList()
            ep.addAll(listOf(0, 0, 0, 0, 0, 0, 0, 0))
            var state = State(
                    Array<Int>(8) { 0 },
                    Array<Int>(8) { 0 },
                    ep.stream().toArray { arrayOfNulls<Int>(it) },
                    Array<Int>(12) { 0 }
            )
            for (i_move in move_names_ph2.indices) {
                var new_state = state.apply_move(moves[move_names_ph2[i_move]]!!)
                e_ep_move_table[i][i_move] = e_ep_to_index(new_state.ep.slice(0 until 4).stream().toArray { arrayOfNulls<Int>(it) })
            }
        }
        co_eec_prune_table = Array<Array<Int>>(NUM_CO) { Array<Int>(NUM_E_COMBINATIONS) { -1 } }
        co_eec_prune_table[0][0] = 0
        while (num_filled != NUM_CO * NUM_E_COMBINATIONS) {
            for (i_co in 0 until NUM_CO) {
                for (i_eec in 0 until NUM_E_COMBINATIONS) {
                    if (co_eec_prune_table[i_co][i_eec] == distance) {
                        for (i_move in 0 until move_namesList.size) {
                            val next_co = co_move_table[i_co][i_move]
                            val next_eec = e_combination_table[i_eec][i_move]
                            if (co_eec_prune_table[next_co][next_eec] == -1) {
                                co_eec_prune_table[next_co][next_eec] = distance + 1
                                num_filled += 1
                            }
                        }
                    }
                }
            }
            distance += 1
        }
        eo_eec_prune_table = Array<Array<Int>>(NUM_EO) { Array<Int>(NUM_E_COMBINATIONS) { -1 } }
        eo_eec_prune_table[0][0] = 0
        distance = 0
        num_filled = 1
        while (num_filled != NUM_EO * NUM_E_COMBINATIONS) {
            for (i_eo in 0 until NUM_EO) {
                for (i_eec in 0 until NUM_E_COMBINATIONS) {
                    if (eo_eec_prune_table[i_eo][i_eec] == distance) {
                        for (i_move in 0 until move_namesList.size) {
                            val next_eo = eo_move_table[i_eo][i_move]
                            val next_eec = e_combination_table[i_eec][i_move]
                            if (eo_eec_prune_table[next_eo][next_eec] == -1) {
                                eo_eec_prune_table[next_eo][next_eec] = distance + 1
                                num_filled += 1
                            }
                        }
                    }
                }
            }
            distance += 1
        }
        cp_eep_prune_table = Array<Array<Int>>(NUM_CP) { Array<Int>(NUM_E_EP) { -1 } }
        cp_eep_prune_table[0][0] = 0
        distance = 0
        num_filled = 1
        while (num_filled != NUM_CP * NUM_E_EP) {
            for (i_cp in 0 until NUM_CP) {
                for (i_eep in 0 until NUM_E_EP) {
                    if (cp_eep_prune_table[i_cp][i_eep] == distance) {
                        for (i_move in 0 until move_names_ph2.size) {
                            val next_cp = cp_move_table[i_cp][i_move]
                            val next_eep = e_ep_move_table[i_eep][i_move]
                            if (cp_eep_prune_table[next_cp][next_eep] == -1) {
                                cp_eep_prune_table[next_cp][next_eep] = distance + 1
                                num_filled += 1
                            }
                        }
                    }
                }
            }
            distance += 1
        }
        udep_eep_prune_table = Array<Array<Int>>(NUM_UD_EP) { Array<Int>(NUM_E_EP) { -1 } }
        udep_eep_prune_table[0][0] = 0
        distance = 0
        num_filled = 1
        while (num_filled != NUM_UD_EP * NUM_E_EP) {
            for (i_udep in 0 until NUM_UD_EP) {
                for (i_eep in 0 until NUM_E_EP) {
                    if (udep_eep_prune_table[i_udep][i_eep] == distance) {
                        for (i_move in 0 until move_names_ph2.size) {
                            val next_udep = ud_ep_move_table[i_udep][i_move]
                            val next_eep = e_ep_move_table[i_eep][i_move]
                            if (udep_eep_prune_table[next_udep][next_eep] == -1) {
                                udep_eep_prune_table[next_udep][next_eep] = distance + 1
                                num_filled += 1
                            }
                        }
                    }
                }
            }
            distance += 1
        }
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
        var co = Array<Int>(8) { 0 }
        var sum_co = 0
        for (i in 6 downTo 0) {
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
        var eo = Array<Int>(12) { 0 }
        var sum_eo = 0
        for (i in 10 downTo 0) {
            eo[i] = index_tmp % 2
            index_tmp /= 2
            sum_eo += eo[i]
        }
        eo[11] = (2 - sum_eo % 2) % 2

        return eo
    }

    fun calc_combination(n: Int, r: Int): Int {
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
        var combination = Array<Int>(12) { 0 }
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
            for (j in i + 1 until 8) {
                if (cp[i] > cp[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_cp(index: Int): Array<Int> {
        var index_tmp = index
        var cp = Array<Int>(8) { 0 }
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
            for (j in i + 1 until 8) {
                if (ep[i] > ep[j]) {
                    index += 1
                }
            }
        }
        return index
    }

    fun index_to_ud_ep(index: Int): Array<Int> {
        var index_tmp = index
        var ep = Array<Int>(8) { 0 }
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
        var eep = Array<Int>(4) { 0 }
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

    fun is_move_available(prev_move: String?, move: String): Boolean {
        if (prev_move.isNullOrEmpty()) return true
        val prev_face = prev_move[0]
        val move_face = move[0]
        if (prev_face == move_face) return false
        if (inv_face[prev_face] == move_face) {
            return num_face[prev_face]!! < num_face[move_face]!!
        }
        return true
    }

    fun depth_limited_search_ph1(co_index: Int, eo_index: Int, e_comb_index: Int, depth: Int): Boolean {
        if (depth == 0 && co_index == 0 && eo_index == 0 && e_comb_index == 0) {
            state = initial_state
            for (move_name in current_solution_ph1) {
                state = state.apply_move(moves[move_name]!!)
            }
            return start_phase2(state)
        }
        if (depth == 0) {
            return false
        }
        if (max(co_eec_prune_table[co_index][e_comb_index], eo_eec_prune_table[eo_index][e_comb_index]) > depth) {
            return false
        }
        var prev_move = if (!current_solution_ph1.isNullOrEmpty()) current_solution_ph1.last() else ""
        for (move_name in move_namesList) {
            if (!is_move_available(prev_move, move_name)) continue
            current_solution_ph1.add(move_name)
            var move_index = move_names_to_index[move_name]!!
            var next_co_index = co_move_table[co_index][move_index]
            var next_eo_index = eo_move_table[eo_index][move_index]
            var next_e_comb_index = e_combination_table[e_comb_index][move_index]
            if (depth_limited_search_ph1(next_co_index, next_eo_index, next_e_comb_index, depth - 1)) return true
            current_solution_ph1.removeLast()
        }
        return false
    }

    fun depth_limited_search_ph2(cp_index: Int, udep_index: Int, eep_index: Int, depth: Int): Boolean {
        if (depth == 0 && cp_index == 0 && udep_index == 0 && eep_index == 0) {
            return true
        }
        if (depth == 0) {
            return false
        }
        if (max(cp_eep_prune_table[cp_index][eep_index], udep_eep_prune_table[udep_index][eep_index]) > depth) {
            return false
        }
        var prev_move = ""
        if (!current_solution_ph2.isNullOrEmpty()) {
            prev_move = current_solution_ph2.last()
        } else if (!current_solution_ph1.isNullOrEmpty()) {
            prev_move = current_solution_ph1.last()
        }
        for (move_name in move_names_ph2) {
            if (!is_move_available(prev_move, move_name)) continue
            current_solution_ph2.add(move_name)
            var move_index = move_names_to_index_ph2[move_name]!!
            var next_cp_index = cp_move_table[cp_index][move_index]
            var next_udep_index = ud_ep_move_table[udep_index][move_index]
            var next_eep_index = e_ep_move_table[eep_index][move_index]
            if (depth_limited_search_ph2(next_cp_index, next_udep_index, next_eep_index, depth - 1)) return true
            current_solution_ph2.removeLast()
        }
        return false
    }

    fun start_search(max_length: Int = 30): List<String>? {
        max_solution_length = max_length
        var co_index = co_to_index(initial_state.co)
        var eo_index = eo_to_index(initial_state.eo)
        var e_combination = Array<Int>(initial_state.ep.size) {
            if (initial_state.ep[it] in 0..3) {
                1
            } else {
                0
            }
        }
        var e_comb_index = e_combination_to_index(e_combination)
        var depth = 0
        while (depth <= max_solution_length) {
            if (depth_limited_search_ph1(co_index, eo_index, e_comb_index, depth)) {
                val current_solution = current_solution_ph1 + current_solution_ph2
                return current_solution
            }
            depth += 1
        }
        return null
    }

    fun start_phase2(state: State): Boolean {
        val cp_index = cp_to_index(state.cp)
        val udep_index = ud_ep_to_index(state.ep.sliceArray(4 until state.ep.size))
        val eep_index = e_ep_to_index(state.ep.sliceArray(0 until 4))
        var depth = 0
        while (depth <= max_solution_length - current_solution_ph1.size) {
            if (depth_limited_search_ph2(cp_index, udep_index, eep_index, depth)) {
                return true
            }
            depth += 1
        }
        return false
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
    }
}