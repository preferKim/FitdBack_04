package com.fitdback.userinterface

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fitdback.database.DataBasket
import com.fitdback.posedetection.CameraActivity
import com.fitdback.posedetection.R
import com.fitdback.test.barChartTest.BarChartTestActivity

// Firebase
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

//import com.google.firebase.auth.ktx.auth


class LoginActivity_old : AppCompatActivity() {

    //    private lateinit var auth: FirebaseAuth
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 해당 라인 'Firebase.' 뒤의 'auth'가 자동 import 되지 않고 빨간줄로 표시 됨. 팝업 메시지 : Unresolved reference: auth
//        auth = Firebase.auth

        firebaseAuth = FirebaseAuth.getInstance() // auth = Firebase.auth 대체

        // 레이아웃
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnJoin = findViewById<Button>(R.id.btnJoin)
        val btnAnonymousLogin = findViewById<Button>(R.id.btnAnonymousLogin)
        val btnDevLogin = findViewById<Button>(R.id.btnDevLogin)
        val btnRunBarChart = findViewById<Button>(R.id.btnRunBarChart)

        // Intent
        val toCameraAcitivityIntent = Intent(this, CameraActivity::class.java)
        val toLoginSuccessActivityIntent = Intent(this, LoginSuccessActivity::class.java)
        val toMainActivityIntent = Intent(this, MainActivity::class.java)
        val toLoadingActivity = Intent(this, LoadingActivity::class.java)

        // 로그인 버튼 클릭 시 동작
        btnLogin.setOnClickListener {

            // 레이아웃의 EditText 에서 email과 password를 읽어들인다.
            val email = findViewById<EditText>(R.id.areaID).text.toString().trim()
            val password = findViewById<EditText>(R.id.areaPassword).text.toString().trim()

            if (checkIfBothEmailAndPasswordAreNotNull(email, password)) {
                emailLoginAuth(
                    email,
                    password,
                    toMainActivityIntent
                )
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }

        }

        // 회원가입 버튼 클릭 시 동작
        btnJoin.setOnClickListener {

            // 레이아웃의 EditText 에서 email과 password를 읽어들인다.
            val email = findViewById<EditText>(R.id.areaID).text.toString().trim()
            val password = findViewById<EditText>(R.id.areaPassword).text.toString().trim()

            if (checkIfBothEmailAndPasswordAreNotNull(email, password)) {
                joinAuth(email, password, toMainActivityIntent)
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }

        }

        // Anonymous 버튼 클릭시 동작
        btnAnonymousLogin.setOnClickListener {

            anonymousAuth(toMainActivityIntent)

        }

        // Dev Login 버튼
        btnDevLogin.setOnClickListener {

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_dev_login, null)
            val mBuilder =
                AlertDialog.Builder(this).setView(mDialogView).setTitle("Dev Login")

            val mAlertDialog = mBuilder.show()

            val btnKshLogin =
                mAlertDialog.findViewById<Button>(R.id.btnKshLogin)

            val btnOmsLogin =
                mAlertDialog.findViewById<Button>(R.id.btnOmsLogin)

            val btnPjkLogin =
                mAlertDialog.findViewById<Button>(R.id.btnPjkLogin)

            btnKshLogin?.setOnClickListener {
                Handler().postDelayed({
                    emailLoginAuth("ksh@gmail.com", "123456", toMainActivityIntent)
                    getDB()
                }, 500)
            }

            btnOmsLogin?.setOnClickListener {
                Handler().postDelayed({
                    emailLoginAuth("oms@gmail.com", "123456", toMainActivityIntent)
                    getDB()
                }, 500)
            }

            btnPjkLogin?.setOnClickListener {
                emailLoginAuth("pjk@gmail.com", "123456", toMainActivityIntent)
            }
        }

        btnRunBarChart.setOnClickListener {
            startActivity(Intent(this, BarChartTestActivity::class.java))
        }
    } // end of onCreate()

    private fun getDB(){
        val dbPath = DataBasket.getDBPath("users", "ex_data", true)
        DataBasket.getDataFromFB(dbPath!!, "individualExData")
    }


    /*
        Login Function
     */

    // 사용자가 입력한 email과 password가 모두 null값이 아닌지 확인
    private fun checkIfBothEmailAndPasswordAreNotNull(email: String?, password: String?): Boolean {

        if (email.isNullOrEmpty() or password.isNullOrEmpty()) { // 둘 중 하나라도 null 이면
            return false
        }

        return true
    }

    // Firebase 로그인 과정에서 발생한 오류들에 대한 처리
    private fun checkTypesOfException(exception: Exception) {

        val exceptionToString = exception.toString()

        if (exceptionToString.contains("FirebaseAuthInvalidCredentialsException")) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show()
        } else if (exceptionToString.contains("FirebaseAuthUserCollisionException")) {
            Toast.makeText(this, "이미 가입된 이메일 입니다.", Toast.LENGTH_SHORT).show()
        } else if (exceptionToString.contains("FirebaseAuthWeakPasswordException")) {
            Toast.makeText(this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
        } else if (exceptionToString.contains("FirebaseAuthInvalidUserException")) {
            Toast.makeText(this, "가입된 계정이 없습니다. 회원가입을 먼저 하세요.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, exceptionToString, Toast.LENGTH_SHORT).show()
        }

    }

    private fun emailLoginAuth(email: String, password: String, intent: Intent) { // 이메일 로그인 인증

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "이메일 로그인 성공", Toast.LENGTH_SHORT).show()
                    getDB()
                    startActivity(intent)
                    finish() // 액티비티가 두개 존재하는 오류 수정!


                } else {

                    Log.w("TestLogin", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "이메일 로그인 실패", Toast.LENGTH_SHORT).show()
                    checkTypesOfException(task.exception!!)

                }
            }
//            .addOnSuccessListener {
//                Toast.makeText(this, "이메일 로그인 성공", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, firebaseAuth.currentUser?.uid.toString(), Toast.LENGTH_SHORT).show()
//                Toast.makeText(this, "이메일 로그인 실패", Toast.LENGTH_SHORT).show()
//            }

    }

    private fun joinAuth(email: String, password: String, intent: Intent) { // 회원가입하고 Intent로 이동

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 회원 가입 성공

                    Toast.makeText(this, "회원 가입 성공", Toast.LENGTH_SHORT).show()
                    startActivity(intent) // LoginSuccessActivity로 이동
                    finish() // 액티비티가 두개 존재하는 오류 수정!


                } else { // 실패

                    Log.w("TestLogin", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "회원 가입 실패", Toast.LENGTH_SHORT).show()
                    checkTypesOfException(task.exception!!)

                }
            }

    }

    private fun anonymousAuth(intent: Intent) { // 익명 로그인 인증

        firebaseAuth.signInAnonymously()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "익명 로그인 성공",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)
                finish() // 액티비티가 두개 존재하는 오류 수정!

            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "익명 로그인 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
} // end of LoginActivity_old
