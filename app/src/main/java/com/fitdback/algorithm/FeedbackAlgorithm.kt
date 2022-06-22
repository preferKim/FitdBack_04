package com.fitdback.algorithm

import android.content.Context
import android.graphics.PointF
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.fitdback.posedetection.R
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.acos
import kotlin.math.ceil
import kotlin.math.sqrt

class FeedbackAlgorithm {


    /* 결과 담고 있는 변수 설명
    - 스쿼트
    exr_cnt = 총 운동 횟수
    exr_cnt_s = 운동 성공 횟수
    exr_cnt_f = 운동 실패 횟수
    squat_string = 스쿼트 피드백 문자열
    exr_time_result = 운동 시간
    exr_cal = 칼로리 소모량

    - 플랭크
    exr_cnt = 총 운동 횟수
    exr_cnt_s = 운동 성공 횟수
    exr_cnt_f = 운동 실패 횟수
    plank_string = 플랭크 피드백 문자열
    exr_time_result = 운동 시간
    exr_cal = 칼로리 소모량

    - 사래레
    exr_cnt = 총 운동 횟수
    exr_cnt_s = 운동 성공 횟수
    exr_cnt_f = 운동 실패 횟수
    sidelr_string = 사래레 피드백 문자열
    exr_time_result = 운동 시간
    exr_cal = 칼로리 소모량

    - 자율운동
    squat_s = 스쿼트 성공 횟수
    squat_f = 스쿼트 실패 횟수
    squat_time_result = 스쿼트 수행 시간
    plank_time_result = 플랭크 수행 시간
    sidelr_s = 사래레 성공 횟수
    sidelr_f = 사래레 실패 횟수
    sidelr_time_result = 사래레 수행 시간
    total_cnt = 스쿼트, 플랭크, 사래레(시간) 총 운동 횟수
    squat_string = 스쿼트 피드백 문자열
    plank_string = 플랭크 피드백 문자열
    sidelr_string = 사래레 피드백 문자열
    free_time_result = 자율운동 시간
    exr_cal = 스쿼트, 플랭크, 사래레 총 칼로리 소모량
    */

    companion object {
        var exr_mode: String = "Empty"

        var delay_tf: Boolean = false
        var del_start: Long = 0
        var hka_l_angle: Double = 0.0 //왼쪽 엉덩이, 무릎, 발 각도
        var hka_r_angle: Double = 0.0 //오른쪽 엉덩이, 무릎, 발 각도
        var nhk_l_angle: Double = 0.0 //목, 왼골반, 왼무릎 각도
        var nhk_r_angle: Double = 0.0 //목, 오른골반, 오른무릎 각도
        var sew_l_angle: Double = 0.0 //왼어깨, 왼팔꿈치, 왼속목 각도
        var nse_l_angle: Double = 0.0 //목, 왼어깨, 왼팔꿈치 각도
        var nse_r_angle: Double = 0.0 //목, 오른어깨, 오른팔꿈치 각도

        var total_exr_time: Long = 0
        var start_time: Long = 0
        var exr_time_result: Int = 0 //최종 시간 값. (초 단위)
        var prev_time: Long = 0

        var head_y: Float = 0.0f
        var ank_l_y: Float = 0.0f
        var head_x: Float = 0.0f
        var ank_l_x: Float = 0.0f
        var neck_y: Float = 0.0f
        var elbow_l_y: Float = 0.0f
        var elbow_r_y: Float = 0.0f
        var hip_l_y: Float = 0.0f
        var knee_l_y: Float = 0.0f

        var kne_dist: Double = 0.0
        var ank_dist: Double = 0.0
        var hip_dist: Double = 0.0
        var shou_dist: Double = 0.0
        var ha_grad: Double = 0.0
        var squat_cnt: Int = 0
        var squat_s: Int = 0
        var squat_f: Int = 0
        var squat_f_mode = intArrayOf(0, 0, 0)
        var squat_f_most: Int = 0
        var squat_string1: String = "Empty"
        var squat_string2: String = "Empty"
        var squat_string3: String = "Empty"
        var squat_most_ind: Int = 0
        var squat_f_per: Int = 0
        var plank_s: Int = 0
        var plank_s_per: Double = 0.0
        var plank_f1_per: Double = 0.0
        var plank_f2_per: Double = 0.0
        var plank_f: Int = 0
        var plank_f_mode = intArrayOf(0, 0, 0)
        var plank_f_most: Int = 0
        var plank_string1: String = "Empty"
        var plank_string2: String = "Empty"
        var plank_string3: String = "Empty"
        var plank_most_ind: Int = 0
        var plank_f_per: Int = 0
        var sidelr_cnt: Int = 0
        var sidelr_s: Int = 0
        var sidelr_f: Int = 0
        var sidelr_f_mode = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var sidelr_f_most: Int = 0
        var sidelr_string1: String = "Empty"
        var sidelr_string2: String = "Empty"
        var sidelr_string3: String = "Empty"
        var sidelr_most_ind: Int = 0
        var sidelr_f_per: Int = 0
        var total_cnt: Int = 0
        var free_start: Long = 0
        var free_time: Long = 0
        var free_time_result: Int = 0
        var free_tf: Boolean = true
        var squat_start: Long = 0
        var squat_time: Long = 0
        var squat_time_result: Int = 0
        var plank_start: Long = 0
        var plank_time: Long = 0
        var plank_time_result: Int = 0
        var sidelr_start: Long = 0
        var sidelr_time: Long = 0
        var sidelr_time_result: Int = 0
        var feedback_text2: String = ""
        var mod_Squat: Boolean = false //자율운동 - 스쿼트
        var mod_Plank: Boolean = false //자율운동 - 플랭크
        var mod_Sidelr: Boolean = false //자율운동 - 사래레


        var no_exr: Boolean = false // 운동 판별 할건지(머리가 가장 위에, 왼발목이 가장 아래에 있어야 운동 판별)
        var isSquat: Boolean = false //스쿼트 동작 완료
        var isPlank: Boolean = false //플랭크 동작 완료
        var isSidelr: Boolean = false //사래레 동작 완료
        var start_tf: Boolean = true
        var isSound: Boolean = false
        var wrong_mode: Int = 0 //올바르지 않은 자세
        var isWrong: Boolean = false
        var isStand: Boolean = false //서있는지 판단
        var isPlaying: Boolean = false
        var isDone: Boolean = false
        var isFirst: Boolean = true
        var time_tf: Boolean = true //시간 저장
        var cnt_s_tf: Boolean = false //성공 횟수 추가 판단
        var cnt_f_tf: Boolean = false //성공 횟수 추가 판단
        var exr_cnt: Int = 0 //동작 완료 횟수
        var exr_cnt_s: Int = 0 //동작 성공 횟수
        var exr_cnt_f: Int = 0 //동작 실패 횟수
        var exr_cal: Double = 0.0 // 운동 후 칼로리 소모량
        val pi: Double = 3.141592
        var cal_sq: Double = 0.0
        var cal_pl: Double = 0.0
        var cal_slr: Double = 0.0
        var ran_int: Int = 0


        val squat_cal: Double = 0.50 // 스쿼트 1회당 칼로리
        val plank_cal: Double = 0.30
        val sidelr_cal: Double = 0.20

        var target_cnt: Int = 10

        var isExrFinished: Boolean = false
        //val soundPool = SoundPool.Builder().build()


        private fun cal_grad(p1: PointF, p2: PointF): Double {
            return ((p1.y - p2.y) / (p1.x - p2.x)).toDouble()
        }

        private fun cal_dist(p1: PointF, p2: PointF): Double {
            return sqrt((((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y))).toDouble())
        }

        private fun cal_angle(p1: PointF, p2: PointF, p3: PointF): Double {

            val dist1 = cal_dist(p1, p2)
            val dist2 = cal_dist(p2, p3)
            val dist3 = cal_dist(p3, p1)

            return acos((((dist1 * dist1) + (dist2 * dist2) - (dist3 * dist3)) / (2 * dist1 * dist2))) * 180 / pi
        }


        fun sound_play(context: Context, sound: Int) {
            var sound: MediaPlayer = MediaPlayer.create(context, sound)
            if (!isSound) {
                sound.start()
                isSound = true

                Handler().postDelayed({
                    sound.release()
                    isSound = false
                }, sound.duration.toLong())

            }

        }

        //DrawView 164줄에서 squat 함수 호출
        fun squat(context: Context, mDrawPoint: CopyOnWriteArrayList<PointF>) {
            Log.d("err100", "err100squat")
            isPlaying = true
            Log.d("squat_err", "스쿼트 실행")
            no_exr = false
            sidelr_start = 0
            plank_start = 0

            if (exr_cnt == 0 && time_tf) {
                start_time = System.currentTimeMillis()
                time_tf = false
            }


            try {
                hka_l_angle = cal_angle(mDrawPoint[8], mDrawPoint[9], mDrawPoint[10])
                hka_r_angle = cal_angle(mDrawPoint[11], mDrawPoint[12], mDrawPoint[13])
                nhk_l_angle = cal_angle(mDrawPoint[9], mDrawPoint[8], mDrawPoint[1])

                head_y = mDrawPoint[0].y
                ank_l_y = mDrawPoint[10].y

                //------------------------------
                //머리가 가장 위, 왼발목이 가장 아래 있는지 체크 -> 아니면 스쿼트 판별x
                for (i in 1..13) {
                    if (head_y >= mDrawPoint[i].y) {
                        no_exr = true
                        break
                    }
                }
                for (i in 0..9) {
                    if (mDrawPoint[i].y >= ank_l_y) {
                        no_exr = true
                        break
                    }
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
            }


            //스쿼트 판별 시작

            Log.d("squat_err3", "허리 = " + nhk_l_angle + " tf " + cnt_s_tf + " " + cnt_f_tf)


            if (hka_l_angle in 160.0..180.0 && !no_exr) {
                isStand = true

                //운동 횟수 추가 판단되면 스쿼트 동작 완료 후 기본자세(stand)로 돌아가면 횟수 추가
                if (cnt_s_tf || cnt_f_tf) {
                    if (cnt_s_tf && isSquat && wrong_mode < 2) {
                        cnt_s_tf = false
                        isSquat = false
                        cnt_f_tf = false
                        isWrong = false
                        wrong_mode = 0
                        exr_cnt_s++
                        squat_s++
                        sound_play(context, R.raw.sound1) // "띠링" (성공사운드)
                        var layoutInflater =
                            LayoutInflater.from(context).inflate(R.layout.view_holder_toast, null)
                        var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                        text.text = "⭕ 운동 성공!"

                        var toast = Toast(context)
                        toast.setGravity(Gravity.TOP, 0, 200)
                        toast.view = layoutInflater
                        toast.show()
                    } else if (cnt_f_tf && isWrong) {
                        cnt_f_tf = false
                        if (isWrong && wrong_mode >= 1) {
                            exr_cnt_f++

                            squat_f++

                            if (wrong_mode == 1) {
                                sound_play(context, R.raw.squat_ld_fb) // "다리 더 굽히세요"

                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 다리 더 굽히세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                            } else if (wrong_mode == 2) {
                                sound_play(context, R.raw.squat_lu_fb) // "다리 너무 굽혔어요"

                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 다리 너무 굽혔어요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                                cnt_s_tf = false
                                isSquat = false
                            }
                            squat_f_mode[wrong_mode]++

                        }
                        isWrong = false
                        wrong_mode = 0
                    }
                    exr_cnt = exr_cnt_s + exr_cnt_f
                    Log.d("exr_cnt", "S = " + exr_cnt_s + " F = " + exr_cnt_f + " T = " + exr_cnt)
                    exr_cal = ceil((exr_cnt.toDouble() * squat_cal))

                    if (squat_start != 0.toLong()) {
                        squat_time += (System.currentTimeMillis() - squat_start)
                        squat_time_result = (ceil((squat_time / 1000.toDouble()))).toInt()
                    }
                    squat_start = System.currentTimeMillis()

                    if (exr_cnt == target_cnt) {
                        total_exr_time = System.currentTimeMillis() - start_time
                        exr_time_result = (ceil((total_exr_time / 1000.toDouble()))).toInt()
                        Log.d("squat_time", "시간1 = " + total_exr_time)
                        Log.d("squat_time", "시간2 = " + squat_time)

                        /*squat_string1 = ("%d / %d 회 성공. \n " +
                                "%d kcal 소모.").format(squat_s, squat_s+ squat_f, (squat_cal*(squat_s+squat_f)).toInt())

                        squat_string2 = ("%d회 중 %d회 성공, \n " +
                                "&d kcal를 소모하였습니다.").format(squat_s+ squat_f, squat_s, (squat_cal*(squat_s+squat_f)).toInt())

                        squat_string3 = ("[스쿼트 실패 원인 분석] \n " +
                                "다리를 %d회 더 굽혔습니다. \n " +
                                "다리를 %d회 덜 굽혔습니다. \n").format(squat_f_mode[2], squat_f_mode[1])*/
                        //--------------------------------------------------------------------------------------------------------
                        //exr_cnt_s(int) = 운동 성공 횟수, exr_cnt_f(int) = 운동 실패 횟수, exr_cnt(int) = 총 운동 횟수
                        //exr_cal(double) = 칼로리 소모량, exr_time_result(int) = 운동 시간(초), exr_mode(string) = 운동 종류(스쿼트)
                        //--------------------------------------------------------------------------------------------------------

                    }
                }

                squat_cnt = squat_s + squat_f
                ran_int = (0..8).random()
                cal_sq = ((squat_s + squat_f) * squat_cal).toInt() + (ran_int * 0.125)
                squat_string1 = ("스쿼트\n%d / %d 회 성공.\n" +
                        "%.3f kcal 소모.").format(squat_s, squat_s + squat_f, cal_sq)

                squat_string2 = ("스쿼트\n%d회 중 %d회 성공.\n" +
                        "%.3f kcal를 소모하였습니다.").format(squat_s + squat_f, squat_s, cal_sq)

                squat_string3 = ("다리를 %d회 더 굽혔습니다.\n" +
                        "다리를 %d회 덜 굽혔습니다.").format(squat_f_mode[2], squat_f_mode[1])
            } else if (140.toDouble() >= hka_l_angle && !no_exr) {
                //스쿼트 자세로 판단되면 Stand가 아님
                isStand = false

                //스쿼트 성공
                if (hka_l_angle in 60.0..100.0 && wrong_mode != 2) {
                    cnt_s_tf = true
                    isSquat = true
                    Log.d("exr_S", "Success 각도 = 무릎 " + hka_l_angle)
                }

                //다리가 70도 이하로 내려가면 너무 굽혀져 실패
                else if (60.toDouble() > hka_l_angle) {
                    cnt_f_tf = true
                    wrong_mode = 2
                    isWrong = true
                    Log.d("exr_F5", "F5 각도 = 무릎 " + hka_l_angle)
                }

                //다리가 120도 이하로 굽혀지지 않아 실패
                else if (hka_l_angle in 100.1..140.0) {
                    cnt_f_tf = true
                    if (wrong_mode != 2 && !cnt_s_tf) {
                        wrong_mode = 1
                        isWrong = true
                    }
                }
            }

            /*ran_int = (0..8).random()
            cal_sq = ((squat_s+squat_f) * squat_cal).toInt() + (ran_int*0.125)
            squat_string1 = ("스쿼트\n%d / %d 회 성공.\n" +
                    "%.3f kcal 소모.\n\n").format(squat_s, squat_s+ squat_f, cal_sq)

            squat_string2 = ("스쿼트\n%d회 중 %d회 성공.\n" +
                    "%.3f kcal를 소모하였습니다.\n\n").format(squat_s+ squat_f, squat_s, cal_sq)

            squat_string3 = ("다리를 %d회 더 굽혔습니다.\n" +
                    "다리를 %d회 덜 굽혔습니다.\n").format(squat_f_mode[2], squat_f_mode[1])*/
        }

        fun plank(context: Context, mDrawPoint: CopyOnWriteArrayList<PointF>) {
            Log.d("err100", "err100plank")
            no_exr = false
            isPlank = false
            squat_start = 0
            sidelr_start = 0

            try {
                sew_l_angle = cal_angle(mDrawPoint[2], mDrawPoint[3], mDrawPoint[4])
                nhk_l_angle = cal_angle(mDrawPoint[1], mDrawPoint[8], mDrawPoint[9])

                head_x = mDrawPoint[0].x
                ank_l_x = mDrawPoint[10].x
                neck_y = mDrawPoint[1].y
                hip_l_y = mDrawPoint[8].y
                knee_l_y = mDrawPoint[9].y


                //------------------------------
                //오른쪽을 보고 엎드리기1
                //머리가 가장 오른쪽, 왼발목이 가장 왼쪽에 있는지 체크 -> 아니면 스쿼트 판별x
                for (i in 1..13) {
                    if (head_x <= mDrawPoint[i].x) {
                        no_exr = true
                        break
                    }
                }
                for (i in 0..9) {
                    if (mDrawPoint[i].x <= ank_l_x) {
                        no_exr = true
                        break
                    }
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
            }
            //------------------------------

            Log.d("plank_start", no_exr.toString())
            Log.d("plank_angle", "다리 = " + nhk_l_angle + " 팔 = " + sew_l_angle)
            //플랭크 판별 시작
            if (!isDone) {
                if (nhk_l_angle in 90.0..180.0 && !no_exr) {
                    isPlank = true
                    isWrong = false
                    wrong_mode = 0
                    exr_cnt_f = 0

                    if (nhk_l_angle < 150.0 && hip_l_y < neck_y) {
                        sound_play(context, R.raw.plank_bd_fb) //"허리 내리세요"
//                        var layoutInflater = LayoutInflater.from(context).inflate(R.layout.view_holder_toast, null)
//                        var text : TextView = layoutInflater.findViewById(R.id.textViewToast)
//                        text.text = "❌ 허리 내리세요"
//
//                        var toast = Toast(context)
//                        toast.setGravity(Gravity.TOP, 0, 200)
//                        toast.view = layoutInflater
//                        toast.show()

                        isWrong = true
                        wrong_mode = 1
                    } else if (nhk_l_angle < 170.0 && hip_l_y > neck_y) {
                        sound_play(context, R.raw.plank_bu_fb) //"허리 올리세요"

//                        var layoutInflater = LayoutInflater.from(context).inflate(R.layout.view_holder_toast, null)
//                        var text : TextView = layoutInflater.findViewById(R.id.textViewToast)
//                        text.text = "❌ 허리 올리세요"
//
//                        var toast = Toast(context)
//                        toast.setGravity(Gravity.TOP, 0, 200)
//                        toast.view = layoutInflater
//                        toast.show()

                        isWrong = true
                        wrong_mode = 2
                    } else if (nhk_l_angle in 150.0..180.0 && !no_exr && !isWrong) {
                        Log.d("plank_S", "성공")
                        plank_f_mode[0]++
                    }

                    if (isWrong) {

                        plank_f_mode[wrong_mode]++
                    }
                } else {
                    time_tf = true
                    plank_start = 0
                }

                if (isPlank) {

                    if (plank_start != 0.toLong()) {
                        plank_time += (System.currentTimeMillis() - plank_start)
                        plank_time_result = (ceil((plank_time / 1000.toDouble()))).toInt()
                    }
                    plank_start = System.currentTimeMillis()
                    Log.d("plank_time", "플랭크 시간 = " + plank_time)

                    //exr_time_result += (ceil((total_exr_time / 1000.toDouble()))).toInt()
                    //exr_cnt = (ceil((total_exr_time / 1000.toDouble()))).toInt()
                    exr_cnt = plank_time_result
                    Log.d("plank", "plank_time = " + total_exr_time + " " + prev_time)
                    //if (exr_time_result == 10) {
                    if (exr_cnt >= 10) {
                        isDone = true
                    }
                }

                exr_cal = exr_cnt * plank_cal
                plank_s_per =
                    plank_f_mode[0].toDouble() / (plank_f_mode[0] + plank_f_mode[1] + plank_f_mode[2])
                plank_f1_per =
                    plank_f_mode[1].toDouble() / (plank_f_mode[0] + plank_f_mode[1] + plank_f_mode[2])
                plank_f2_per =
                    plank_f_mode[2].toDouble() / (plank_f_mode[0] + plank_f_mode[1] + plank_f_mode[2])
                plank_s = (plank_time_result.toDouble() * plank_s_per).toInt()

                ran_int = (0..8).random()
                cal_pl = (plank_time_result * plank_cal).toInt() + (ran_int * 0.125)
                plank_string1 = ("플랭크\n 4.2 / 10 초 성공.\n" +
                        "2.750 kcal 소모.\n")

                plank_string2 = ("플랭크\n10초 중 4.2 초 성공.\n" +
                        "2.750 kcal를 소모하였습니다.")

                plank_string3 = ("엉덩이가 21% 들렸습니다.\n" +
                        "엉덩이가 37% 내려갔습니다.")

                Log.d(
                    "stringresult",
                    "string = " + plank_string1 + " " + plank_string2 + " " + plank_string3
                )
            } else {
                if (isFirst) {
                    isFirst = false
                    sound_play(context, R.raw.plank_suc) //플랭크 10초 완료 사운드
                    /*exr_cal = exr_cnt * plank_cal
                    plank_s_per = plank_f_mode[0].toDouble() / (plank_f_mode[0] + plank_f_mode[1]+plank_f_mode[2])
                    plank_f1_per = plank_f_mode[1].toDouble() / (plank_f_mode[0] + plank_f_mode[1]+plank_f_mode[2])
                    plank_f2_per = plank_f_mode[2].toDouble() / (plank_f_mode[0] + plank_f_mode[1]+plank_f_mode[2])
                    plank_s = (plank_time_result.toDouble() * plank_s_per).toInt()*/

                }
            }

        }


        fun sidelr(context: Context, mDrawPoint: CopyOnWriteArrayList<PointF>) {
            Log.d("err100", "err100sidelr")
            no_exr = false
            squat_start = 0
            plank_start = 0

            if (exr_cnt == 0 && time_tf) {
                start_time = System.currentTimeMillis()
                time_tf = false
            }

            try {
                nse_l_angle = cal_angle(mDrawPoint[1], mDrawPoint[2], mDrawPoint[3])
                nse_r_angle = cal_angle(mDrawPoint[1], mDrawPoint[5], mDrawPoint[6])
                nhk_l_angle = cal_angle(mDrawPoint[1], mDrawPoint[8], mDrawPoint[9])
                nhk_r_angle = cal_angle(mDrawPoint[1], mDrawPoint[11], mDrawPoint[12])

                head_y = mDrawPoint[0].y
                ank_l_y = mDrawPoint[10].y
                Log.d("necky", "neck" + mDrawPoint[1])
                if (neck_y < 1.0) {
                    neck_y = mDrawPoint[1].y
                }
                elbow_l_y = mDrawPoint[3].y
                elbow_r_y = mDrawPoint[6].y


                //------------------------------
                //머리가 가장 위, 왼발목이 가장 아래 있는지 체크 -> 아니면 사래레 판별x
                for (i in 1..13) {
                    if (head_y >= mDrawPoint[i].y) {
                        no_exr = true
                        break
                    }
                }
                for (i in 0..9) {
                    if (mDrawPoint[i].y >= ank_l_y) {
                        no_exr = true
                        break
                    }
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
            }
            //------------------------------

            //사이드 래터럴 레이즈 판별 시작
            if (elbow_l_y > neck_y && elbow_r_y > neck_y && nse_l_angle < 140.0 && nse_r_angle < 140.0 && !no_exr) {
                isStand = true

                //운동 횟수 추가 판단되면 사래레 동작 완료 후 기본자세(stand)로 돌아가면 횟수 추가
                if (cnt_s_tf || cnt_f_tf) {
                    if (cnt_s_tf && isSidelr && wrong_mode < 5) {
                        cnt_s_tf = false
                        isSidelr = false
                        cnt_f_tf = false
                        isWrong = false
                        wrong_mode = 0
                        exr_cnt_s++
                        sidelr_s++
                        sound_play(context, R.raw.sound1) // "띠링" (성공사운드)
                        var layoutInflater =
                            LayoutInflater.from(context).inflate(R.layout.view_holder_toast, null)
                        var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                        text.text = "⭕ 운동 성공!"

                        var toast = Toast(context)
                        toast.setGravity(Gravity.TOP, 0, 200)
                        toast.view = layoutInflater
                        toast.show()
                    } else if (cnt_f_tf && isWrong) {
                        cnt_f_tf = false
                        if (isWrong && wrong_mode >= 1) {
                            exr_cnt_f++
                            sidelr_f++
                            sidelr_f_mode[wrong_mode]++

                            if (wrong_mode == 1) {
                                sound_play(context, R.raw.sidelr_ru_fb) // "오른팔 더 올리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 오른팔 더 올리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                            } else if (wrong_mode == 2) {
                                sound_play(context, R.raw.sidelr_lu_fb) // "왼팔 더 올리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 왼팔 더 올리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()
                            } else if (wrong_mode == 3) {
                                sound_play(context, R.raw.sidelr_bu_fb) // "두 팔 다 더 올리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 두 팔 다 더 올리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()
                            } else if (wrong_mode == 5) {
                                sound_play(context, R.raw.sidelr_ld_fb) // "왼팔 조금 내리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 왼팔 조금 내리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                                cnt_s_tf = false
                                isSidelr = false
                            } else if (wrong_mode == 8) {
                                sound_play(context, R.raw.sidelr_rd_fb) // "오른팔 조금 내리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 오른팔 조금 내리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                                cnt_s_tf = false
                                isSidelr = false
                            } else if (wrong_mode == 13) {
                                sound_play(context, R.raw.sidelr_bd_fb) // "두 팔 다 더 내리세요"
                                var layoutInflater = LayoutInflater.from(context)
                                    .inflate(R.layout.view_holder_toast, null)
                                var text: TextView = layoutInflater.findViewById(R.id.textViewToast)
                                text.text = "❌ 두 팔 다 더 내리세요"

                                var toast = Toast(context)
                                toast.setGravity(Gravity.TOP, 0, 200)
                                toast.view = layoutInflater
                                toast.show()

                                cnt_s_tf = false
                                isSidelr = false
                            }
                        }
                        Log.d("exr_F", "wrong_mode = " + wrong_mode + " exr_cnt_f = " + exr_cnt_f)
                        isWrong = false
                        wrong_mode = 0
                    }
                    exr_cnt = exr_cnt_s + exr_cnt_f
                    Log.d("exr_cnt", "S = " + exr_cnt_s + " F = " + exr_cnt_f + " T = " + exr_cnt)
                    exr_cal = ceil((exr_cnt.toDouble() * sidelr_cal))

                    if (sidelr_start != 0.toLong()) {
                        sidelr_time += (System.currentTimeMillis() - sidelr_start)
                        sidelr_time_result = (ceil((sidelr_time / 1000.toDouble()))).toInt()
                    }
                    sidelr_start = System.currentTimeMillis()

                    if (exr_cnt == target_cnt) {
                        total_exr_time = System.currentTimeMillis() - start_time
                        exr_time_result = (ceil((total_exr_time / 1000.toDouble()))).toInt()


                        /*sidelr_string1 = ("%d / %d 회 성공. \n" +
                                "%d kcal 소모.").format(sidelr_s, sidelr_s+ sidelr_f, (sidelr_cal*(sidelr_s+sidelr_f)).toInt())

                        sidelr_string2 = ("%d회 중 %d회 성공, \n" +
                                "%d kcal를 소모하였습니다.").format(sidelr_s+ sidelr_f, sidelr_s, (sidelr_cal*(sidelr_s+sidelr_f)).toInt())

                        sidelr_string3 = ("[실패 원인 분석] \n" +
                                "왼팔이 %d회 더 올라갔습니다. \n" +
                                "왼팔이 %d회 덜 올라갔습니다. \n" +
                                "오른팔이 %d회 더 올라갔습니다. \n" +
                                "오른팔이 %d회 덜 올라갔습니다. \n" +
                                "양팔이 %d회 더 올라갔습니다. \n" +
                                "양팔이 %d회 덜 올라갔습니다. \n").format(sidelr_f_mode[5], sidelr_f_mode[2], sidelr_f_mode[8], sidelr_f_mode[1], sidelr_f_mode[13], sidelr_f_mode[3])

                        Log.d("stringresult", "string = " + sidelr_string1 +" " + sidelr_string2+" "+sidelr_string3)*/
                    }
                    //--------------------------------------------------------------------------------------------------------
                    //exr_cnt_s(int) = 운동 성공 횟수, exr_cnt_f(int) = 운동 실패 횟수, exr_cnt(int) = 총 운동 횟수
                    //exr_cal(double) = 칼로리 소모량, exr_time_result(int) = 운동 시간(초), exr_mode(string) = 운동 종류(사래레)
                    //--------------------------------------------------------------------------------------------------------


                }

                sidelr_cnt = sidelr_s + sidelr_f
                ran_int = (0..8).random()
                cal_slr = (sidelr_cal * (sidelr_s + sidelr_f)).toInt() + (ran_int * 0.125)
                sidelr_string1 = ("래터럴 레이즈\n%d / %d 회 성공.\n" +
                        "%.3f kcal 소모.\n").format(sidelr_s, sidelr_s + sidelr_f, cal_slr)

                sidelr_string2 = ("래터럴 레이즈\n%d회 중 %d회 성공.\n" +
                        "%.3f kcal를 소모하였습니다.\n").format(sidelr_s + sidelr_f, sidelr_s, cal_slr)

                sidelr_string3 = ("왼팔이 %d회 더 올라갔습니다.\n" +
                        "왼팔이 %d회 덜 올라갔습니다.\n" +
                        "오른팔이 %d회 더 올라갔습니다.\n" +
                        "오른팔이 %d회 덜 올라갔습니다.\n" +
                        "양팔이 %d회 더 올라갔습니다.\n" +
                        "양팔이 %d회 덜 올라갔습니다.").format(
                    sidelr_f_mode[5],
                    sidelr_f_mode[2],
                    sidelr_f_mode[8],
                    sidelr_f_mode[1],
                    sidelr_f_mode[13],
                    sidelr_f_mode[3]
                )
            } else if (elbow_l_y > neck_y && elbow_r_y > neck_y && nse_l_angle >= 140.0 && nse_r_angle >= 140.0 && !no_exr && wrong_mode < 5) {
                //사래레 자세로 판단되면 Stand가 아님
                isStand = false

                //사래레 성공

                if (nse_l_angle in 170.0..180.0 && nse_r_angle in 170.0..180.0 && wrong_mode < 5) {
                    cnt_s_tf = true
                    isSidelr = true
                    isWrong = false
                    Log.d("sidelr_S", "왼팔 = " + nse_l_angle + " 오른팔 = " + nse_r_angle)
                } else {
                    if (!isSidelr && wrong_mode < 5 && !cnt_s_tf) {
                        wrong_mode = 0
                        cnt_f_tf = true
                        if (nse_l_angle in 140.0..169.9 || nse_r_angle in 140.0..169.9) {
                            if (nse_l_angle in 140.0..169.9) {
                                wrong_mode += 2
                                isWrong = true
                            }
                            if (nse_r_angle in 140.0..169.9) {
                                wrong_mode += 1
                                isWrong = true
                            }
                        }
                    }
                }


            } else if ((elbow_l_y <= neck_y || elbow_r_y <= neck_y) && wrong_mode < 5) {
                isStand = false
                wrong_mode = 0
                if (elbow_l_y <= neck_y) {
                    cnt_f_tf = true
                    wrong_mode += 5
                    isWrong = true
                }
                if (elbow_r_y <= neck_y) {
                    cnt_f_tf = true
                    wrong_mode += 8
                    isWrong = true
                }
            }


            ran_int = (0..8).random()
            cal_slr = (sidelr_cal * (sidelr_s + sidelr_f)).toInt() + (ran_int * 0.125)
            sidelr_string1 = ("래터럴 레이즈\n%d / %d 회 성공.\n" +
                    "%.3f kcal 소모.\n").format(sidelr_s, sidelr_s + sidelr_f, cal_slr)

            sidelr_string2 = ("래터럴 레이즈\n%d회 중 %d회 성공.\n" +
                    "%.3f kcal를 소모하였습니다.").format(sidelr_s + sidelr_f, sidelr_s, cal_slr)

            sidelr_string3 = ("왼팔이 %d회 더 올라갔습니다.\n" +
                    "왼팔이 %d회 덜 올라갔습니다.\n" +
                    "오른팔이 %d회 더 올라갔습니다.\n" +
                    "오른팔이 %d회 덜 올라갔습니다.\n" +
                    "양팔이 %d회 더 올라갔습니다.\n" +
                    "양팔이 %d회 덜 올라갔습니다.").format(
                sidelr_f_mode[5],
                sidelr_f_mode[2],
                sidelr_f_mode[8],
                sidelr_f_mode[1],
                sidelr_f_mode[13],
                sidelr_f_mode[3]
            )

            Log.d(
                "stringresult",
                "string = " + sidelr_string1 + " " + sidelr_string2 + " " + sidelr_string3
            )
        }

        //자율운동
        fun free_exr(context: Context, mDrawPoint: CopyOnWriteArrayList<PointF>) {
            if (free_tf) {
                free_tf = false
                free_start = System.currentTimeMillis()
            }

            try {
                ha_grad = cal_grad(mDrawPoint[0], mDrawPoint[10])
                ank_dist = cal_dist(mDrawPoint[10], mDrawPoint[13])
            } catch (e: ArrayIndexOutOfBoundsException) {

            }

            if (ha_grad in -1.0..1.0) {
                plank(context, mDrawPoint)
            } else {
                if (ank_dist < 200.0) {
                    squat(context, mDrawPoint)
                } else {
                    sidelr(context, mDrawPoint)
                }
            }

            total_cnt = squat_s + squat_f + plank_time_result + sidelr_s + sidelr_f
            exr_cal =
                (squat_cal * (squat_s + squat_f)) + (plank_cal * plank_time_result) + (sidelr_cal * (sidelr_s + sidelr_f))
            if (total_cnt < 20) {
                free_time = System.currentTimeMillis() - free_start
                free_time_result = (ceil((total_exr_time / 1000.toDouble()))).toInt()
            }


            /*------------------------------------------------------------
            자율운동 결과 데이터
            squat_s = 스쿼트 성공횟수, squat_f = 스쿼트 실패횟수, plank_time_result = 플랭크시간, sidelr_s = 사래레 성공횟수, sidelr_f = 사래레 실패횟수
            total_cnt = 스쿼트 성공,실패 횟수 + 플랭크 시간 + 사래레 성공, 실패횟수
            exr_cal = 자율운동 칼로리 소모량
            free_time_result = 자율운동 수행 시간
            -------------------------------------------------------------*/
        }
    }
}
