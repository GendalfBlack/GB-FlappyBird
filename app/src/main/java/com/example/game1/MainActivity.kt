package com.example.game1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerSprite : ImageView = findViewById(R.id.birdView)
        val startButton : ImageView = findViewById(R.id.startButtonView)
        val top1 : ImageView = findViewById(R.id.tubeTopView1)
        val top2 : ImageView = findViewById(R.id.tubeTopView2)
        val top3 : ImageView = findViewById(R.id.tubeTopView3)
        val bottom1 : ImageView = findViewById(R.id.tubeBottomView1)
        val bottom2 : ImageView = findViewById(R.id.tubeBottomView2)
        val bottom3 : ImageView = findViewById(R.id.tubeBottomView3)

        val gameObjects = GameObjects(playerSprite, startButton,
            top1,top2, top3, bottom1,bottom2,bottom3)
        val update = Timer()
        val isRun = IsRun(false)

        startButton.setOnClickListener {
            startButton.visibility = View.GONE
            update.scheduleAtFixedRate(Update(gameObjects, isRun), 0, 1000/60)
        }

        val layout : ConstraintLayout = findViewById(R.id.screen)
        layout.setOnClickListener {
            if (isRun.value){
                playerSprite.translationY -= 250
                playerSprite.rotation = -40F
            }
        }

    }
}
data class IsRun(var value : Boolean)
data class GameObjects(var player :ImageView, var button: ImageView,
                       var top1: ImageView, var top2: ImageView,var top3: ImageView,
                       var bottom1: ImageView,var bottom2: ImageView,var bottom3: ImageView)

class Update(val gameObjects: GameObjects, var isRun : IsRun) : TimerTask()
{
    override fun run() {
        if(!isRun.value ){
            randomTubePos(gameObjects.top1, gameObjects.bottom1)
            randomTubePos(gameObjects.top2, gameObjects.bottom2)
            randomTubePos(gameObjects.top3, gameObjects.bottom3)
        }
        isRun.value = true
        updateTubes(gameObjects.top1, gameObjects.bottom1, 0)
        updateTubes(gameObjects.top2, gameObjects.bottom2, 200)
        updateTubes(gameObjects.top3, gameObjects.bottom3, 400)
        playerUpdate()
        endGame()
    }
    private fun updateTubes(tubeTop : ImageView, tubeBottom : ImageView, deltaX : Int){
        tubeTop.translationX -= 10
        tubeBottom.translationX -=10
        if (tubeTop.translationX < -1300 - deltaX){
            tubeTop.translationX = 0F + deltaX
            tubeBottom.translationX = 0F + deltaX
        }
    }

    private fun randomTubePos(tubeTop : ImageView, tubeBottom : ImageView){
        val r1 = Random.nextInt(-300, 300)
        tubeTop.translationY += r1
        tubeBottom.translationY += r1
    }

    private fun playerUpdate(){
        gameObjects.player.translationY += 10
        gameObjects.player.rotation += 1
    }
    private fun endGame(){
        if (gameObjects.player.translationY > 1000 || gameObjects.player.translationY < -1000)
        {
            gameObjects.player.translationY = 0F
            gameObjects.player.rotation = 0F
            gameObjects.button.post { gameObjects.button.visibility = View.VISIBLE }
            isRun.value = false
            this.cancel()
        }
    }
}