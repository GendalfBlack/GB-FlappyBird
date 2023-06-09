package com.example.game1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import org.w3c.dom.Text
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerSprite : ImageView = findViewById(R.id.birdView)
        val startButton : ImageView = findViewById(R.id.startButtonView)
        val top1 : ImageView = findViewById(R.id.tubeTopView1)
        val bottom1 : ImageView = findViewById(R.id.tubeBottomView1)
        val score : TextView = findViewById(R.id.score)

        val gameObjects = GameObjects(playerSprite, startButton, top1, bottom1, score)
        gameObjects.player_pos = Vector(playerSprite.marginLeft.toFloat(), playerSprite.marginTop.toFloat())
        gameObjects.top_pos = Vector(top1.marginLeft.toFloat(), top1.marginTop.toFloat())
        gameObjects.bottom_pos = Vector(bottom1.marginLeft.toFloat(), bottom1.marginTop.toFloat())
        gameObjects.start_player_pos = Vector(playerSprite.marginLeft.toFloat(), playerSprite.marginTop.toFloat())
        gameObjects.start_top_pos = Vector(top1.marginLeft.toFloat(), top1.marginTop.toFloat())
        gameObjects.start_bottom_pos = Vector(bottom1.marginLeft.toFloat(), bottom1.marginTop.toFloat())

        playerSprite.doOnLayout {
            gameObjects.player_size = Vector(playerSprite.width.toFloat()-45, playerSprite.height.toFloat()-45)
        }
        top1.doOnLayout {
            gameObjects.tube_size = Vector(top1.width.toFloat(), top1.height.toFloat())
        }


        val update = Timer()
        val isRun = IsRun(false)

        startButton.setOnClickListener {
            startButton.visibility = View.GONE
            update.scheduleAtFixedRate(Update(gameObjects, isRun), 0, 1000/60)
            isRun.value = true
        }

        val layout : ConstraintLayout = findViewById(R.id.screen)
        layout.setOnClickListener {
            if (isRun.value){
                playerSprite.translationY -= 200

                gameObjects.player_pos.y -= 200

                playerSprite.rotation = -40F
            }
        }

    }
}
data class IsRun(var value : Boolean)

data class Vector(var x : Float, var y : Float){
    operator fun minus(v : Vector) : Float{
        return sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y))
    }
    operator fun plus(v : Vector) : Vector {
        return Vector(v.x + x, v.y + y)
    }

    override fun toString(): String {
        return "$x, $y"
    }
}

data class GameObjects(var player :ImageView, var button: ImageView,
                       var top1: ImageView, var bottom1: ImageView, var scoreView: TextView){
    var player_pos :Vector = Vector(0F,0F)
    var top_pos : Vector = Vector(0F,0F)
    var bottom_pos : Vector = Vector(0F,0F)
    var start_player_pos :Vector = Vector(0F,0F)
    var start_top_pos : Vector = Vector(0F,0F)
    var start_bottom_pos : Vector = Vector(0F,0F)

    var player_size : Vector = Vector(0F,0F)
    var tube_size : Vector = Vector(0F,0F)

    var score : Int = 0
}

class Update(val gameObjects: GameObjects, var isRun : IsRun) : TimerTask()
{
    override fun run() {
        if(!isRun.value ){
            randomTubePos(gameObjects.top1, gameObjects.bottom1)
        }

        updateTubes(gameObjects.top1, gameObjects.bottom1)
        playerUpdate()
        endGame(false)
    }
    private fun updateTubes(tubeTop : ImageView, tubeBottom : ImageView){
        tubeTop.translationX -= 10
        tubeBottom.translationX -=10

        gameObjects.top_pos.x -= 10
        gameObjects.bottom_pos.x -= 10

        if (tubeTop.translationX < -1400){
            tubeTop.translationX = 0F
            tubeBottom.translationX = 0F
            randomTubePos(tubeTop, tubeBottom)

            gameObjects.top_pos.x = gameObjects.start_top_pos.x
            gameObjects.bottom_pos.x = gameObjects.bottom_pos.x
        }
    }

    private fun randomTubePos(tubeTop : ImageView, tubeBottom : ImageView){
        val r1 = Random.nextInt(-3, 3)
        tubeTop.translationY = 0F
        tubeBottom.translationY = 0F
        tubeTop.translationY += r1 * 100
        tubeBottom.translationY += r1 * 100

        gameObjects.top_pos.y = gameObjects.start_top_pos.y
        gameObjects.bottom_pos.y = gameObjects.start_bottom_pos.y
        gameObjects.top_pos.y += r1 * 100
        gameObjects.bottom_pos.y += r1 * 100
    }

    private fun playerUpdate(){
        gameObjects.player.translationY += 10
        gameObjects.player.rotation += 1

        gameObjects.player_pos.y += 10

        Log.d("playerpos.x :", (gameObjects.player_pos.x).toString())
        Log.d("playerpos.x :", (gameObjects.player_pos.x + gameObjects.player_size.x).toString())
        Log.d("tubepos.x :", (gameObjects.top_pos.x).toString())
        Log.d("tubepos.x+w :", (gameObjects.top_pos.x+ gameObjects.tube_size.x).toString())
        Log.d("playerpos.y :", (gameObjects.player_pos.y).toString())
        Log.d("tubepos.y+h :", (gameObjects.top_pos.y + gameObjects.tube_size.y).toString())


        if ( gameObjects.player_pos.x + gameObjects.player_size.x > gameObjects.top_pos.x &&
            gameObjects.player_pos.x + gameObjects.player_size.x < gameObjects.top_pos.x+ gameObjects.tube_size.x  &&
            (gameObjects.player_pos.y + 45 < gameObjects.top_pos.y + gameObjects.tube_size.y ||
                gameObjects.player_pos.y + gameObjects.player_size.y > gameObjects.bottom_pos.y))
        {
            endGame(true)
        }

        if (abs(gameObjects.player_pos.x - gameObjects.top_pos.x+ gameObjects.tube_size.x/2) < 5){
            gameObjects.score += 1
            gameObjects.scoreView.post{ gameObjects.scoreView.text = "${gameObjects.score}"}
        }
    }

    private fun endGame(flag : Boolean){
        if (flag || gameObjects.player.translationY > 1000 || gameObjects.player.translationY < -1000)
        {
            gameObjects.player.translationY = 0F
            gameObjects.player_pos.y = gameObjects.start_player_pos.y
            gameObjects.player.rotation = 0F
            gameObjects.button.post { gameObjects.button.visibility = View.VISIBLE }
            isRun.value = false
            randomTubePos(gameObjects.top1, gameObjects.bottom1)
            gameObjects.top1.translationX = 0F
            gameObjects.bottom1.translationX = 0F
            randomTubePos(gameObjects.top1, gameObjects.bottom1)

            gameObjects.top_pos.x = gameObjects.start_top_pos.x
            gameObjects.bottom_pos.x = gameObjects.bottom_pos.x
            gameObjects.score = 0
            gameObjects.scoreView.post{ gameObjects.scoreView.text = "${gameObjects.score}"}
            this.cancel()
        }
    }
}