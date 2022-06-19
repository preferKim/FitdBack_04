package com.fitdback.userinterface

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dinuscxj.progressbar.CircleProgressBar
import com.fitdback.algorithm.FeedbackAlgorithm
import com.fitdback.database.DataBasket
import com.fitdback.posedetection.R
import com.fitdback.test.barChartTest.BarChartVariables
import com.fitdback.test.barChartTest.MyBarChartGenerator
import com.fitdback.test.feedbackTest.FeedbackHandler
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FeedbackActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val database = Firebase.database

        // 레이아웃
        val barChart: BarChart = findViewById(R.id.feedbackBarChart)
        val feedbackYAxisTitleArea = findViewById<TextView>(R.id.feedbackYAxisTitleArea)
        val btnShowExCalorieChart = findViewById<Button>(R.id.fb_btnShowExCalorieChart)
        val btnShowExCountChart = findViewById<Button>(R.id.fb_btnShowExCountChart)
        val btnShowExTimeChart = findViewById<Button>(R.id.fb_btnShowExTimeChart)

        val barChartLayout = findViewById<RelativeLayout>(R.id.barChartLayout)
        val btnLinearLayout = findViewById<LinearLayout>(R.id.btnLinearLayout)


        val achievementRate = findViewById<TextView>(R.id.fb_achivementRate)
        val feedbackTitleSq = findViewById<TextView>(R.id.fb_sq_title)
        val feedbackMsgSq = findViewById<TextView>(R.id.fb_msg_sq)
        val feedbackMsgPl = findViewById<TextView>(R.id.fb_msg_pl)
        val feedbackMsgSlr = findViewById<TextView>(R.id.fb_msg_slr)
        val feedbackTitlePl = findViewById<TextView>(R.id.fb_pl_title)
        val feedbackTitleSlr = findViewById<TextView>(R.id.fb_slr_title)
        // 그리드 레이아웃
        val gridSq = findViewById<LinearLayout>(R.id.grid_sq)
        val gridPl = findViewById<LinearLayout>(R.id.grid_pl)
        val gridSlr = findViewById<LinearLayout>(R.id.grid_slr)

        val rtn_home = findViewById<Button>(R.id.return_home)
        val toMainActivity = Intent(this, MainActivity::class.java)

        rtn_home.setOnClickListener {
            startActivity(toMainActivity)
            finish()
        }

        // 피드백 액티비티 다른 운동시 UI 공간 차지 방지
        if (FeedbackAlgorithm.exr_mode == "squat") {
            feedbackMsgPl.visibility = View.GONE
            feedbackMsgSlr.visibility = View.GONE
            feedbackTitlePl.visibility = View.GONE
            feedbackTitleSlr.visibility = View.GONE
            gridPl.visibility = View.GONE
            gridSlr.visibility = View.GONE
        } else if (FeedbackAlgorithm.exr_mode == "plank") {
            feedbackMsgSq.visibility = View.GONE
            feedbackMsgSlr.visibility = View.GONE
            feedbackTitleSq.visibility = View.GONE
            feedbackTitleSlr.visibility = View.GONE
            gridSq.visibility = View.GONE
            gridSlr.visibility = View.GONE
        } else if (FeedbackAlgorithm.exr_mode == "sidelr") {
            feedbackMsgSq.visibility = View.GONE
            feedbackMsgPl.visibility = View.GONE
            feedbackTitleSq.visibility = View.GONE
            feedbackTitlePl.visibility = View.GONE
            gridPl.visibility = View.GONE
            gridSq.visibility = View.GONE
        }

        // BarChart Layout 비활성화
        if (FeedbackAlgorithm.exr_mode == "free_exr") {
            barChartLayout.visibility = View.GONE
            btnLinearLayout.visibility = View.GONE
        }


        // Data Write
        val dataModel = DataBasket.tempExrModel

        val dbPath =
            DataBasket.getDBPath("users", "ex_data", true)

        dbPath!!.push().setValue(dataModel)
            .addOnSuccessListener {
//                Toast.makeText(this, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
//                mAlertDialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "데이터 저장실패", Toast.LENGTH_SHORT).show()
            }

        // 최신화된 데이터 불러오기
        val dbPathForReading = DataBasket.getDBPath("users", "ex_data", true)
        DataBasket.getDataFromFB(dbPathForReading!!, "individualExData")

        // 다이얼로그
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_exercise_data_write, null)
        val mBuilder =
            AlertDialog.Builder(this).setView(mDialogView).setTitle("")
        mBuilder.setCancelable(false) // 배경 터치시 종료 방지
        val mAlertDialog = mBuilder.show() // 다이얼로그 띄우기

        // 다이얼로그 운동 결과 텍스트
        val exerciseResultArea = mAlertDialog.findViewById<TextView>(R.id.exerciseResultArea)
        exerciseResultArea?.text = when(FeedbackAlgorithm.exr_mode) {
            "squat" -> FeedbackAlgorithm.squat_string1
            "plank" -> FeedbackAlgorithm.plank_string1
            "sideLateralRaise" -> FeedbackAlgorithm.sidelr_string1
            else -> "ex_count"
        }

        val btnDataWrite =
            mAlertDialog.findViewById<Button>(R.id.btnDataWrite)

        // 차트 그려주는 함수
        fun showChart() {

            // barChart 초기화
            BarChartVariables.clearBarChart(barChart)

            // yAxis Title
            BarChartVariables.setYAxisTitle(feedbackYAxisTitleArea)

            // 보고자 하는 날짜 리스트
            val dateListOfTargetWeek = DataBasket.getDateListOfThisWeek()
            BarChartVariables.updateBarChartData(dateListOfTargetWeek) // BarChartVariables 클래스의 전역변수 update

            // 이번 주 날짜별 Sum
            val dailySumBarEntry =
                BarChartVariables.getDailySumBarEntry(
                    dateListOfTargetWeek,
                    BarChartVariables.firstTargetData!!,
                    BarChartVariables.secondTargetData!!,
                    DataBasket.individualExData!!
                )

            // 실제 Bar Data Set 생성.
            val barDataSet = BarDataSet(dailySumBarEntry, "exDataList")
            BarChartVariables.setExpressedDataFormat(barDataSet)

            // Bar Chart 데이터 삽입
            val data = BarData(barDataSet)
            barChart.data = data

            // Bar Chart 실행
            MyBarChartGenerator().runBarChart(barChart, barDataSet.yMax + 1.0f)

            mAlertDialog.dismiss()
        }

        // 다이얼로그의 버튼 클릭
        btnDataWrite?.setOnClickListener {

            Toast.makeText(this, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()

            // TODO: 레이아웃 채우기
            val feedbackHandler = FeedbackHandler()


            var feedbacktext_list3: List<String> = listOf(
                FeedbackAlgorithm.squat_string3,
                FeedbackAlgorithm.plank_string3,
                FeedbackAlgorithm.sidelr_string3
            )
            var feedbacktext_list2: List<String> = listOf(
                FeedbackAlgorithm.squat_string2,
                FeedbackAlgorithm.plank_string2,
                FeedbackAlgorithm.sidelr_string2
            )

            if (FeedbackAlgorithm.squat_string3 == "Empty") {
                feedbackMsgSq.visibility = View.GONE
                feedbackTitleSq.visibility = View.GONE
                if (FeedbackAlgorithm.plank_string3 == "Empty") {
                    feedbackMsgPl.visibility = View.GONE
                    feedbackTitlePl.visibility = View.GONE
                }
                if (FeedbackAlgorithm.sidelr_string3 == "Empty") {
                    feedbackMsgSlr.visibility = View.GONE
                    feedbackTitleSlr.visibility = View.GONE
                }
            } else {
                if (FeedbackAlgorithm.plank_string3 == "Empty") {
                    feedbackMsgPl.visibility = View.GONE
                    feedbackTitlePl.visibility = View.GONE
                }
                if (FeedbackAlgorithm.sidelr_string3 == "Empty") {
                    feedbackMsgSlr.visibility = View.GONE
                    feedbackTitleSlr.visibility = View.GONE
                }
            }
            /*for( i in feedbacktext_list3){
                if(i=="Empty"){
                    if(i==FeedbackAlgorithm.squat_string3){
                        feedbackMsgSq.visibility = View.GONE
                        feedbackTitleSq.visibility = View.GONE
                        Log.d("string_err", "squat_x")
                    }
                    else if(i==FeedbackAlgorithm.plank_string3){
                        feedbackMsgPl.visibility = View.GONE
                        feedbackTitlePl.visibility = View.GONE
                        Log.d("string_err", "plank_x")
                    }
                    else if(i==FeedbackAlgorithm.sidelr_string3){
                        feedbackMsgSlr.visibility = View.GONE
                        feedbackTitleSlr.visibility = View.GONE
                        Log.d("string_err", "sidelr_x")
                    }
                }
            }*/
            for (i in feedbacktext_list2) {
                if (i != "Empty") {
                    FeedbackAlgorithm.feedback_text2 += i
                }
            }

            achievementRate.text = FeedbackAlgorithm.feedback_text2
            feedbackTitleSq.text = "[스쿼트 실패 원인]"
            feedbackTitlePl.text = "[플랭크 실패 원인]"
            feedbackTitleSlr.text = "[래터럴 레이즈 실패 원인]"
            feedbackMsgSq.text = FeedbackAlgorithm.squat_string3
            feedbackMsgPl.text = FeedbackAlgorithm.plank_string3
            feedbackMsgSlr.text = FeedbackAlgorithm.sidelr_string3

            //feedbackArea.text = feedbackHandler.getFeedback()


            /*if(FeedbackAlgorithm.exr_mode == "squat"){
                feedbackArea.text = FeedbackAlgorithm.squat_string3
            }
            else if(FeedbackAlgorithm.exr_mode == "plank"){
                feedbackArea.text = FeedbackAlgorithm.plank_string3
            }
            else if(FeedbackAlgorithm.exr_mode == "sidelr"){
                feedbackArea.text = FeedbackAlgorithm.sidelr_string3
            }
            else if(FeedbackAlgorithm.exr_mode == "free_exr"){
                feedbackArea.text = FeedbackAlgorithm.squat_string3 + FeedbackAlgorithm.plank_string3 + FeedbackAlgorithm.sidelr_string3
            }*/
            /*for (i in feedbacktext_list){
                if(i!="Empty"){
                    FeedbackAlgorithm.feedback_text3 += i
                }
            }
            feedbackArea.text = FeedbackAlgorithm.feedback_text3*/

            // 프로그레스 바
            val prgBar = findViewById<CircleProgressBar>(R.id.complete_PrgBar)
            val tar_cnt = FeedbackAlgorithm.target_cnt
            val prg = FeedbackAlgorithm.exr_cnt_s

            // 완료 / 목표치
            prgBar.max = 100
            prgBar.progress = (prg.toFloat() / tar_cnt.toFloat() * 100).toInt()


            // 성공 / 완료


            // bar chart 그리기
            Handler().postDelayed({

                BarChartVariables.firstTargetData = FeedbackAlgorithm.exr_mode
                BarChartVariables.secondTargetData = when (BarChartVariables.firstTargetData) {
                    "squat" -> "ex_count"
                    "plank" -> "ex_time"
                    "sideLateralRaise" -> "ex_count"
                    else -> "ex_count"
                }

                showChart()
                mAlertDialog.dismiss()

            }, 1000)

        } // btnDataWrite

        btnShowExCalorieChart.setOnClickListener {
            BarChartVariables.secondTargetData = "ex_calorie"
            showChart()
        }
        btnShowExCountChart.setOnClickListener {
            BarChartVariables.secondTargetData = "ex_count"
            showChart()
        }
        btnShowExTimeChart.setOnClickListener {
            BarChartVariables.secondTargetData = "ex_time"
            showChart()
        }


    }

}

