package com.fitdback.userinterface.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fitdback.database.DataBasket
import com.fitdback.database.datamodel.UserInfoDataModel
import com.fitdback.posedetection.R
import com.fitdback.test.DevModeActivity
import com.fitdback.userinterface.FeedbackActivity
import com.fitdback.userinterface.LoginActivity_new
import com.fitdback.userinterface.TutorialActivity
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        arguments?.let {
        }
    }

    private fun signOut(intent: Intent) {

        firebaseAuth.signOut() // 로그아웃 처리
        DataBasket.googleSignInClient?.signOut()

        startActivity(intent)
        activity?.finish()
    }


    // TODO:버튼 관련 함수 여기에
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val greetingArea = view.findViewById<TextView>(R.id.home_greeting)
        greetingArea.text = getGreetingMessage()

        // 스쿼트 버튼
        val squatBtn: Button = view.findViewById(R.id.squatBtn)
        squatBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val toTutorialActivity = Intent(context, TutorialActivity::class.java)
                toTutorialActivity.putExtra("exr_mod", "squat") // 모드 설정
                startActivity(toTutorialActivity)
            }
        })

        // 플랭크 버튼
        val plankBtn: Button = view.findViewById(R.id.plkBtn)
        plankBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val toTutorialActivity = Intent(context, TutorialActivity::class.java)
                toTutorialActivity.putExtra("exr_mod", "plank") // 모드 설정
                startActivity(toTutorialActivity)
            }
        })

        // 푸시업 버튼
        val puBtn: Button = view.findViewById(R.id.puBtn)
        puBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val toTutorialActivity = Intent(context, TutorialActivity::class.java)
                toTutorialActivity.putExtra("exr_mod", "sidelr")
                startActivity(toTutorialActivity)
            }
        })

        val ftBtn: Button = view.findViewById(R.id.freeExBtn)
        ftBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val toTutorialActivity = Intent(context, TutorialActivity::class.java)
                toTutorialActivity.putExtra("exr_mod", "free_exr")
                startActivity(toTutorialActivity)
            }
        })


        // 로그아웃
        val btnSignOut: Button = view.findViewById(R.id.btnSignOut)
        btnSignOut.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val toLoginActivity = Intent(context, LoginActivity_new::class.java)
                signOut(toLoginActivity)
            }
        })

        // 개발자 모드
        val btnDevMode: Button = view.findViewById(R.id.btnDevMode)
        btnDevMode.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                activity?.finish()
                val toDevActivity = Intent(context, DevModeActivity::class.java)
                toDevActivity.putExtra("exr_mode", "squat")
                startActivity(toDevActivity)
            }
        })


        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("LogNotTimber")
    private fun getGreetingMessage(): String {
        val userInfoDataModel: UserInfoDataModel? =
            DataBasket.individualUserInfo?.getValue(UserInfoDataModel::class.java)

        return if (userInfoDataModel == null) {
            "mangoo님 안녕하세요!"
        } else {
            "${userInfoDataModel.user_nickname}님 안녕하세요!"
        }
    }

    private fun getDB() {
        Log.d("DB", "진입")
        val dbPath = DataBasket.getDBPath("users", "ex_data", true)
        DataBasket.getDataFromFB(dbPath!!, "individualExData")

        Log.d("DB", "완료")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =

            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}