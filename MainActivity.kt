package com.elza.boardfunhelper

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.text.InputType

data class Tile(val a: Int, val b: Int) {
    val pips: Int get() = a + b
    override fun toString() = "$a-$b"
}

data class Move(val tile: Tile, val side: String, val score: Int, val explanation: String)

class MainActivity : Activity() {
    private lateinit var handInput: EditText
    private lateinit var leftInput: EditText
    private lateinit var rightInput: EditText
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(20, 70, 50)
        window.navigationBarColor = Color.rgb(20, 70, 50)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 24)
            setBackgroundColor(Color.rgb(244, 248, 245))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        content.addView(TextView(this).apply {
            text = "BOARD FUN HELPER"
            textSize = 24f
            setTextColor(Color.rgb(20, 70, 50))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 6)
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        content.addView(TextView(this).apply {
            text = "Asisten langkah domino offline"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 20)
        })

        handInput = field(content, "Kartu di tangan (contoh: 6-5, 3-2, 0-0)")
        leftInput = field(content, "Angka ujung kiri papan")
        rightInput = field(content, "Angka ujung kanan papan")

        val analyze = Button(this).apply {
            text = "CARI LANGKAH TERBAIK"
            setOnClickListener { analyze() }
        }
        content.addView(analyze, matchWrap())
        result = TextView(this).apply {
            text = "Masukkan kartu dan angka ujung papan, lalu tekan tombol."
            textSize = 17f
            setTextColor(Color.rgb(25, 45, 35))
            setPadding(14, 18, 14, 18)
            setBackgroundColor(Color.WHITE)
        }
        content.addView(result, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = 16
        })
        content.addView(TextView(this).apply {
            text = "\nFormat kartu: angka-angka, pisahkan dengan koma. Contoh 6-5, 2-2, 0-3.\n\nCatatan: ini alat bantu strategi. Kartu dan ujung papan perlu dimasukkan manual; aplikasi ini belum membaca layar atau mengendalikan Board Fun otomatis."
            textSize = 13f
            setTextColor(Color.DKGRAY)
        })
        setContentView(root)
    }

    private fun field(parent: LinearLayout, hintText: String): EditText {
        val label = TextView(this).apply {
            text = hintText
            textSize = 14f
            setTextColor(Color.rgb(35, 55, 45))
            setPadding(0, 12, 0, 5)
        }
        parent.addView(label)
        val edit = EditText(this).apply {
            textSize = 16f
            singleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(12, 10, 12, 10)
            setBackgroundColor(Color.WHITE)
        }
        parent.addView(edit, matchWrap())
        return edit
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        topMargin = 4
    }

    private fun parseHand(raw: String): List<Tile> {
        if (raw.isBlank()) throw IllegalArgumentException("Isi kartu yang ada di tangan.")
        return raw.split(",", ";").map { token ->
            val nums = token.trim().split("-", ":", "/").mapNotNull { it.trim().toIntOrNull() }
            if (nums.size != 2 || nums.any { it !in 0..6 }) {
                throw IllegalArgumentException("Format kartu '$token' salah. Gunakan angka 0 sampai 6, misalnya 6-5.")
            }
            Tile(nums[0], nums[1])
        }
    }

    private fun analyze() {
        try {
            val hand = parseHand(handInput.text.toString())
            val left = leftInput.text.toString().trim().toIntOrNull()
                ?: throw IllegalArgumentException("Isi angka ujung kiri (0–6).")
            val right = rightInput.text.toString().trim().toIntOrNull()
                ?: throw IllegalArgumentException("Isi angka ujung kanan (0–6).")
            if (left !in 0..6 || right !in 0..6) throw IllegalArgumentException("Ujung papan harus 0 sampai 6.")

            val moves = mutableListOf<Move>()
            hand.forEach { tile ->
                if (tile.a == left || tile.b == left) {
                    val exposed = if (tile.a == left) tile.b else tile.a
                    val score = tile.pips * 2 + if (tile.a == tile.b) 3 else 0
                    moves += Move(tile, "KIRI", score, "Cocok dengan ujung kiri $left; ujung baru menjadi $exposed.")
                }
                if (tile.a == right || tile.b == right) {
                    val exposed = if (tile.a == right) tile.b else tile.a
                    val score = tile.pips * 2 + if (tile.a == tile.b) 3 else 0
                    moves += Move(tile, "KANAN", score, "Cocok dengan ujung kanan $right; ujung baru menjadi $exposed.")
                }
            }

            if (moves.isEmpty()) {
                result.text = "BELUM ADA LANGKAH YANG COCOK\nTidak ada kartu yang cocok dengan ujung kiri $left atau kanan $right. Jika aturan game memungkinkan, pertimbangkan ambil kartu atau lewat."
            } else {
                val best = moves.sortedWith(compareByDescending<Move> { it.score }.thenBy { it.tile.a }.thenBy { it.tile.b }).first()
                result.text = "REKOMENDASI\n\nMainkan kartu ${best.tile} di sisi ${best.side}.\n\n${best.explanation}\nNilai prioritas: ${best.score}\n\nKartu legal lain:\n" +
                    moves.distinctBy { "${it.tile}|${it.side}" }
                        .sortedByDescending { it.score }
                        .joinToString("\n") { "• ${it.tile} — ${it.side} (nilai ${it.score})" } +
                    "\n\nStrategi dasar: kartu bernilai pip tinggi diprioritaskan agar jumlah pip tersisa di tangan berkurang."
            }
        } catch (e: Exception) {
            result.text = "Periksa input:\n${e.message ?: "Input tidak valid."}"
        }
    }
}
