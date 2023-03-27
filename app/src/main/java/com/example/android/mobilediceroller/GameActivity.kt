package com.example.android.mobilediceroller

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import kotlin.system.exitProcess

class GameActivity : AppCompatActivity() {

    var targetScore = 101
    var rollCountHuman = 0
    var rerollComputerScores = 0
    var humanPlayerScore = 0
    var computerPlayerScore = 0
    var humanPlayerTotalScore = 0
    var computerPlayerTotalScore = 0
    var humanWins = 0
    var computerWins = 0
    var tieScore = false
    var firstAttempt = true
    var totalComputerScore = true
    val humanPlayerDiceFaces = mutableListOf<Int>()
    val computerPlayerDiceFaces = mutableListOf<Int>()
    val clickedImagesIds = mutableListOf<Int>()
    val rerollComputer = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_main)

        val targetScoreButton = findViewById<Button>(R.id.set_score_button)
        targetScoreButton.setOnClickListener { setTargetScore() }

        val resetTargetScoreButton = findViewById<Button>(R.id.reset_score_button)
        resetTargetScoreButton.setOnClickListener { restSetTargetScore() }

        val throwButton = findViewById<Button>(R.id.throw_button)
        throwButton.setOnClickListener { throwDice() }

        val scoreButton = findViewById<Button>(R.id.score_button)
        scoreButton.setOnClickListener { gameScore() }
    }


    class Dice(val numSides: Int) {                         // < --- Generate random numbers for dice
        fun roll(): Int {
            return (1..numSides).random()
        }
    }

    private fun throwDice() {

        val targetScoreButton = findViewById<Button>(R.id.set_score_button)         //To avoid setting target during the game
        targetScoreButton.setOnClickListener(null)

        val resetTargetScoreButton = findViewById<Button>(R.id.reset_score_button) //To avoid resetting target during the game
        resetTargetScoreButton.setOnClickListener(null)

        throwDiceForHuman()

        if (tieScore || (rollCountHuman == 2)) {     //When winning score is a tie || Last reroll
            gameScore()

        } else {
            getClickedDice()                         //Dice get clickable after the first attempt of a roll
            val throwButton = findViewById<Button>(R.id.throw_button)
            throwButton.text = "Reroll"
            rollCountHuman++
        }
    }

    private fun throwDiceForHuman() {

        humanPlayerScore = 0

        if (firstAttempt) {                                 // <--- Initial throw in every round

            throwDiceForComputer()

            for (i in 1..5) {
                val dice = Dice(6)
                val roll = dice.roll()
                humanPlayerDiceFaces.add(roll)              //Adds die no. Array will later used to assign face images to die no
                humanPlayerScore += roll                    //Adds each generated die no. To update score of human
            }

            firstAttempt = false

        } else {                                            // < --- For 1st & 2nd rerolls
            val humanDiceImgViews = listOf(
                findViewById<ImageView>(R.id.die1),
                findViewById<ImageView>(R.id.die2),
                findViewById<ImageView>(R.id.die3),
                findViewById<ImageView>(R.id.die4),
                findViewById<ImageView>(R.id.die5))

            var index = 0
            for (humanDiceImgView in humanDiceImgViews) {   // < --- Reroll the dice that were not selected for keeping
                if (humanDiceImgView.id !in clickedImagesIds) {
                    val dice = Dice(6)
                    val roll = dice.roll()
                    humanPlayerDiceFaces.set(index, roll)   //Updates the die face
                }
                index++
            }

            for (i in humanPlayerDiceFaces) {               // < --- Updates the human's score for this reroll
                humanPlayerScore += i
            }
        }

        displayScores(humanPlayerScore, computerPlayerScore)
        updateDiceFaces(humanPlayerDiceFaces, computerPlayerDiceFaces)
    }

    private fun throwDiceForComputer() {
        if (firstAttempt) {                                 // < --- Initial throw in every round
            for (i in 1..5) {
                val dice = Dice(6)
                val roll = dice.roll()
                computerPlayerDiceFaces.add(roll)           //Adds die no. Array will later used to assign face images to die no
                computerPlayerScore += roll                 //Adds each generated die no. To update score of computer
            }
            rerollComputerScores = computerPlayerScore
        }

        var reroll = true
        for (i in 1..2) {
            val rollAgain = (0..1).random()   //rollAgain = 0 computer decides not to reroll again, rollAgain = 1, it will reroll again

            if (rollAgain == 0) {
                reroll = false
            }
            if (reroll) {
                val computerDiceImgViews = listOf(
                    findViewById<ImageView>(R.id.die6),
                    findViewById<ImageView>(R.id.die7),
                    findViewById<ImageView>(R.id.die8),
                    findViewById<ImageView>(R.id.die9),
                    findViewById<ImageView>(R.id.die10)
                )

                val noDice = (1..5).random()            //No of dice to roll
                rerollComputerScores = 0

                for (i in 1..noDice) {                  //Randomly get a diceId to generate a random number
                    val randomComputerId = computerDiceImgViews.random()
                    rerollComputer.add(randomComputerId.id)
                }
                var index = 0
                for (computerDiceImage in computerDiceImgViews) {
                    if (computerDiceImage.id in rerollComputer) {
                        val dice = Dice(6)
                        val roll = dice.roll()
                        computerPlayerDiceFaces.set(index, roll) //Updates the die face
                    }
                    index++
                }

                for (j in computerPlayerDiceFaces) {             // < --- Updates the computers's score for this reroll
                    rerollComputerScores += j
                }
            }
        }
    }

    private fun displayScores(humanPlayerScore: Int, computerPlayerScore: Int) {
        val humanPlayerScoreView = findViewById<TextView>(R.id.human_score)
        val computerPlayerScoreView = findViewById<TextView>(R.id.computer_score)

        humanPlayerScoreView.text = "$humanPlayerScore"
        computerPlayerScoreView.text = "$computerPlayerScore"
    }

    private fun displayTotalScores(humanPlayerTotalScore: Int, computerPlayerTotalScore: Int) {
        val humanTotalScoreView = findViewById<TextView>(R.id.human_total_score)
        val computerTotalScoreView = findViewById<TextView>(R.id.computer_total_score)

        humanTotalScoreView.text = "$humanPlayerTotalScore"
        computerTotalScoreView.text = "$computerPlayerTotalScore"
    }

    private fun updateTotalScore(humanPlayerScore: Int, computerPlayerScore: Int) {

        humanPlayerTotalScore += humanPlayerScore
        computerPlayerTotalScore += computerPlayerScore

        checkWinner(humanPlayerTotalScore, computerPlayerTotalScore)
    }

    private fun checkWinner(humanPlayerTotalScore: Int, computerPlayerTotalScore: Int) {

        if (humanPlayerTotalScore >= targetScore || computerPlayerTotalScore >= targetScore) {

            if (humanPlayerTotalScore == computerPlayerTotalScore) {
                tieScore = true
                Toast.makeText(applicationContext, "Tie! Roll Again!", Toast.LENGTH_SHORT).show()

            } else if (humanPlayerTotalScore >= computerPlayerTotalScore) {

                humanWins++                                 //No of wins for human

                val winsView = findViewById<TextView>(R.id.total_wins)
                winsView.text = "H : $humanWins  /  C : $computerWins"

                val dialogBinding = layoutInflater.inflate(R.layout.user_wins_popup, null)
                val popDialog = Dialog(this)
                popDialog.setContentView(dialogBinding)

                popDialog.setCancelable(true)
                popDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                popDialog.show()

                Handler().postDelayed({
                    popDialog.dismiss()
                    exitProcess(0)
                }, 900)

            } else {

                computerWins++                              //No of wins for computer

                val winsView = findViewById<TextView>(R.id.total_wins)
                winsView.text = "H : $humanWins  /  C : $computerWins"

                val dialogBinding = layoutInflater.inflate(R.layout.user_loss_popup, null)
                val popDialog = Dialog(this)
                popDialog.setContentView(dialogBinding)

                popDialog.setCancelable(true)
                popDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                popDialog.show()

                Handler().postDelayed({
                    popDialog.dismiss()
                    exitProcess(0)
                }, 900)
            }
        }
    }

    private fun setTargetScore() {
        val targetScoreView = findViewById<TextView>(R.id.win_score)
        val targetScoreInput = findViewById<EditText>(R.id.target_score_input)
        val inputText = targetScoreInput.text.toString()

        if (inputText.isNotBlank()) {
            targetScore = inputText.toInt()
            targetScoreView.text = "$targetScore"
        } else {
            Toast.makeText(this, "Enter A Target Score", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restSetTargetScore() {
        val targetScoreView = findViewById<TextView>(R.id.win_score)
        targetScore = 101
        targetScoreView.text = "$targetScore"
    }

    private fun getClickedDice() {                          // < --- Get the ids & set borders to clicked dice for rerolls

        val humanDiceImgViews = listOf(
            findViewById<ImageView>(R.id.die1),
            findViewById<ImageView>(R.id.die2),
            findViewById<ImageView>(R.id.die3),
            findViewById<ImageView>(R.id.die4),
            findViewById<ImageView>(R.id.die5)
        )

        for (humanDiceImgView in humanDiceImgViews) {
            humanDiceImgView.setOnClickListener {
                humanDiceImgView.setBackgroundResource(R.drawable.border)
                clickedImagesIds.add(humanDiceImgView.id)
            }
        }
    }

    private fun setUnclickable() {                          // < --- Set Dice Images Unclickable for initial throw in every round

        val humanDiceImgViews = listOf(
            findViewById<ImageView>(R.id.die1),
            findViewById<ImageView>(R.id.die2),
            findViewById<ImageView>(R.id.die3),
            findViewById<ImageView>(R.id.die4),
            findViewById<ImageView>(R.id.die5))

        for (humanDiceImgView in humanDiceImgViews) {
            humanDiceImgView.setOnClickListener(null)
        }
    }

    private fun removeClickedDice() {                       // < --- Removes the borders & ids of clicked dice for a new round
        for (id in clickedImagesIds) {
            val dieImage = findViewById<View>(id)
            dieImage?.setBackgroundResource(R.drawable.default_background_resource)
        }
        clickedImagesIds.clear()
    }

    private fun gameScore() {
        totalComputerScore = true
        firstAttempt = true
        rollCountHuman = 0
        computerPlayerScore = 0
        humanPlayerDiceFaces.clear()
        computerPlayerDiceFaces.clear()

        val throwButton = findViewById<Button>(R.id.throw_button)
        throwButton.text = "Throw"

        removeClickedDice()
        setUnclickable()

        updateTotalScore(humanPlayerScore, rerollComputerScores)
        displayScores(humanPlayerScore, rerollComputerScores)
        displayTotalScores(humanPlayerTotalScore,computerPlayerTotalScore)
    }

    private fun updateDiceFaces(humanDiceValues: MutableList<Int>, computerDiceValues: MutableList<Int>) {

        val humanDiceImgViews = listOf(
            findViewById<ImageView>(R.id.die1),
            findViewById<ImageView>(R.id.die2),
            findViewById<ImageView>(R.id.die3),
            findViewById<ImageView>(R.id.die4),
            findViewById<ImageView>(R.id.die5))

        val computerDiceImgViews = listOf(
            findViewById<ImageView>(R.id.die6),
            findViewById<ImageView>(R.id.die7),
            findViewById<ImageView>(R.id.die8),
            findViewById<ImageView>(R.id.die9),
            findViewById<ImageView>(R.id.die10))

        // Update human player dice images
        for (i in humanDiceValues.indices) {
            val roll = humanDiceValues[i]
            if (i < humanDiceImgViews.size) {
                val diceImage = humanDiceImgViews[i]
                diceImage.setImageResource(when (roll) {
                    1 -> R.drawable.face1
                    2 -> R.drawable.face2
                    3 -> R.drawable.face3
                    4 -> R.drawable.face4
                    5 -> R.drawable.face5
                    else -> R.drawable.face6
                })
            }
        }
        // Update computer player dice images
        for (i in computerDiceValues.indices) {
            val roll = computerDiceValues[i]
            if (i < computerDiceImgViews.size) {
                val diceImage = computerDiceImgViews[i]
                diceImage.setImageResource(when (roll) {
                    1 -> R.drawable.face1
                    2 -> R.drawable.face2
                    3 -> R.drawable.face3
                    4 -> R.drawable.face4
                    5 -> R.drawable.face5
                    else -> R.drawable.face6
                })
            }
        }
    }
}