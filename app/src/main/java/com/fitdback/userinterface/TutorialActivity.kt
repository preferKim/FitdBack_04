package com.fitdback.userinterface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fitdback.algorithm.FeedbackAlgorithm
import com.fitdback.posedetection.CameraActivity
import com.fitdback.posedetection.R
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class TutorialActivity : YouTubeBaseActivity() {

    val api_key = "AIzaSyD-RJXgEjAgWZsO1LAO5sIgrpFob8k2qEk"
    var videoId:String = ""
    companion object{
        var cameramode : String = "back"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        // 운동정보 넘기기
        val exr = intent.getStringExtra("exr_mod")

        val set_cnt = findViewById<TextView>(R.id.set_cnt)
        val np = findViewById<NumberPicker>(R.id.num_pick)

        np.minValue = 1
        np.maxValue = 50
        np.value = 10
        np.wrapSelectorWheel = false

        if (exr == "squat"){
            set_cnt.setText("스쿼트 " + np.value + "회 실시")
        }
        else if (exr == "plank"){
            set_cnt.setText("플랭크 " + np.value + "초 실시")
        }
        else if (exr == "sidelr"){
            set_cnt.setText("래터럴레이즈 " + np.value + "회 실시")
        }
        else {
            set_cnt.setText("자율운동 " + np.value + "회 실시")
        }
        np.setOnValueChangedListener { numberPicker, i, i2 ->
            if(exr == "squat") {
                set_cnt.setText("스쿼트 " + i2 + "회 실시")
            }
            else if (exr == "plank") {
                set_cnt.setText("플랭크 " + i2 + "초 실시")
            }
            else if (exr == "sidelr"){
                set_cnt.setText("래터럴레이즈 " + i2 + "회 실시")
            }
            else{
                set_cnt.setText("자율운동 " + i2 + "회 실시")

            }
        }



        // 유튜브
        val youtubeView = findViewById<YouTubePlayerView>(R.id.youtubePlayer)
        youtubeView.initialize(api_key, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider,
                player: YouTubePlayer,
                wasRestored: Boolean
            ) {
                if (exr == "squat") {
                    videoId = "Fk9j6pQ6ej8"
                    player.cueVideo(videoId)
                } else if (exr == "plank") {
                    videoId = "B--6YfhmBGc"
                    player.cueVideo(videoId)
                } else if (exr == "sidelr") {
                    videoId = "YdhHnZxcpgY"
                    player.cueVideo(videoId)
                }
                else{
                    videoId = "cgsqsVxd5xc"
                    player.cueVideo(videoId)
                }
            }


            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                result: YouTubeInitializationResult?
            ) {
                TODO("Not yet implemented")
            }
        })

        // 레이아웃
        val btnFitnessStart = findViewById<Button>(R.id.btnFitnessStart)

        val imgView = findViewById<ImageView>(R.id.tt_img)
        val txtView = findViewById<TextView>(R.id.description)

        if (exr == "squat") {
            Glide.with(this).load(R.raw.squat_gif).into(imgView)
            txtView.setText("엉덩이 - 무릎 - 발목이 이루는 각이 70~90도여야 합니다.")
        }
        else if(exr == "plank") {
            Glide.with(this).load(R.raw.plank).into(imgView)
            txtView.setText("목 - 허리 - 무릎이 이루는 각이 170~190도여야 합니다.")
        }
        else if (exr == "sidelr") {
            Glide.with(this).load(R.raw.slr_gif).into(imgView)
            txtView.setText("양팔이 이루는 각이 170~190도여야 합니다.")
        }
        // 인텐트
        val intent = Intent(this, CameraActivity::class.java)

        // btnFitnessStart 버튼을 클릭하여 CameraActivity 실행
        btnFitnessStart.setOnClickListener {
            FeedbackAlgorithm.target_cnt = np.value
            intent.putExtra("exr_mod", exr)
            cameramode = "back"
            startActivity(intent)
            finish()
        }

        val btnFitnessStartFront = findViewById<Button>(R.id.btnFitnessStartBack)

        btnFitnessStartFront.setOnClickListener {
            FeedbackAlgorithm.target_cnt = np.value
            intent.putExtra("exr_mod", exr)
            cameramode = "front"
            startActivity(intent)
            finish()
        }

    }
}