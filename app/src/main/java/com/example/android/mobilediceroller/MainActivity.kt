// Link To The Video
// https://drive.google.com/drive/folders/1iJVJZcdKaktPdo1v_3MOtjtOMAPhRsCu?usp=share_link

package com.example.android.mobilediceroller

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newGameButton = findViewById<Button>(R.id.new_game_button)
        newGameButton.setOnClickListener { newGame() }

        val aboutButton = findViewById<Button>(R.id.about_button)
        aboutButton.setOnClickListener { popUpWindow() }
    }


    private fun newGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun popUpWindow() {
        val dialogBinding = layoutInflater.inflate(R.layout.about_popup,null)

        val popDialog = Dialog(this)
        popDialog.setContentView(dialogBinding)

        popDialog.setCancelable(true)
        popDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popDialog.show()
    }
}