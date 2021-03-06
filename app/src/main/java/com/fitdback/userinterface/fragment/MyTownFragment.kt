package com.fitdback.userinterface.fragment


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.fitdback.database.DataBasket
import com.fitdback.database.datamodel.FriendDataModel
import com.fitdback.database.datamodel.UserInfoDataModel
import com.fitdback.posedetection.R
import com.fitdback.test.CustomDialog
import com.fitdback.test.barChartTest.BarChartVariables
import com.fitdback.test.barChartTest.MyBarChartGenerator
import com.fitdback.test.friendTest.FriendListAdapter
import com.fitdback.userinterface.CommunityActivity
import com.fitdback.userinterface.TutorialActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyTownFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyTownFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val database = Firebase.database

        // 뷰
        val view = inflater.inflate(R.layout.fragment_my_town, container, false)
        val context = context

        // 레이아웃
        val btnRunFriendMode = view.findViewById<Button>(R.id.town_friendList)

        btnRunFriendMode.setOnClickListener {

            // 다이얼로그
            val friendModeDialog =
                CustomDialog(context!!, R.layout.dialog_friend_mode, "")
            val friendModeAlertDialog = friendModeDialog.showDialog()
            friendModeAlertDialog?.setCancelable(false)

            // 레이아웃
            val btnShowFriendList =
                friendModeAlertDialog!!.findViewById<Button>(R.id.btnShowFriendList)
            val btnRegisterFriend =
                friendModeAlertDialog.findViewById<Button>(R.id.btnRegisterFriend)
            val btnCheckMyCode = friendModeAlertDialog.findViewById<Button>(R.id.btnCheckMyCode)
            val btnFriendModeConfirm =
                friendModeAlertDialog.findViewById<Button>(R.id.btnFriendModeConfirm)
            friendModeAlertDialog.setCancelable(false)

            // 친구 목록 보기
            btnShowFriendList?.setOnClickListener {

                // 다이얼로그
                val friendListDialog =
                    CustomDialog(context, R.layout.dialog_friend_list, "")
                val friendListAlertDialog = friendListDialog.showDialog()
                friendListAlertDialog?.setCancelable(false)

                // 레이아웃
                val btnFriendListConfirm =
                    friendListAlertDialog?.findViewById<Button>(R.id.btnFriendListConfirm)

                // 리스트뷰에 추가할 데이터 리스트
                val dataModelList = mutableListOf<FriendDataModel>()

                // 리스트뷰 연결
                val listView = friendListAlertDialog?.findViewById<ListView>(R.id.friendListLV)
                val adapterList = FriendListAdapter(dataModelList)
                listView?.adapter = adapterList

                // DB 읽어오기
                val dbPath = DataBasket.getDBPath("users", "friend_info", true)

                dbPath!!.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        dataModelList.clear() // 리스트뷰 덧씌워짐 방지

                        for (dataModel in snapshot.children) {
                            dataModelList.add(dataModel.getValue(FriendDataModel::class.java)!!)
                        }

                        adapterList.notifyDataSetChanged() // 비동기 -> 동기식 변경

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                // 친구 통계 보기
                listView?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->

                        val selectItem = parent.getItemAtPosition(position) as FriendDataModel
                        Toast.makeText(context, "${selectItem.friend_nickname}", Toast.LENGTH_SHORT)
                            .show()

                        // DB 불러오기
//                        var friendExData: DataSnapshot? = null
                        val friendExDataDBPath =
                            database.getReference("users").child(selectItem.friend_uid!!)
                                .child("ex_data")

                        friendExDataDBPath.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                DataBasket.friendExData = dataSnapshot
//                                Log.d("db_data", "friendExData: $dataSnapshot")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("db_data", "onCancelled")
                            }
                        })

                        // 친구 통계 다이얼로그
                        val friendSTDialog =
                            CustomDialog(
                                context,
                                R.layout.dialog_friend_chart,
                                "${selectItem.friend_nickname}님의 운동 통계"
                            )

                        Handler().postDelayed(
                            {
                                val friendSTAlertDialog = friendSTDialog.showDialog()
                                Log.d(
                                    "db_data",
                                    "friendExData: ${DataBasket.friendExData.toString()}"
                                )

                                // Bar chart Layout
                                val barChart: BarChart = friendSTAlertDialog!!.findViewById(R.id.f_barChart)!!

                                val selectedExTypeArea =
                                    friendSTAlertDialog.findViewById<TextView>(R.id.f_selectedExTypeArea)!!
                                val selectedDataArea =
                                    friendSTAlertDialog.findViewById<TextView>(R.id.f_selectedDataArea)
                                val yAxisTitleArea =
                                    friendSTAlertDialog.findViewById<TextView>(R.id.f_yAxisTitleArea)

                                val btnSetSquatChart =
                                    friendSTAlertDialog.findViewById<RadioButton>(R.id.f_btnSetSquatChart)
                                val btnSetPlankChart =
                                    friendSTAlertDialog.findViewById<RadioButton>(R.id.f_btnSetPlankChart)
                                val btnSetSideLateralRaiseChart =
                                    friendSTAlertDialog.findViewById<RadioButton>(R.id.f_btnSetSideLateralRaiseChart)

                                val btnSetExCalorieChart =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowExCalorieChart)
                                val btnSetExCountChart =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowExCountChart)
                                val btnSetExTimeChart =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowExTimeChart)

                                val btnShowThisWeek =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowThisWeek)
                                val btnShowPreviousWeek =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowPreviousWeek)
                                val btnShowNextWeek =
                                    friendSTAlertDialog.findViewById<Button>(R.id.f_btnShowNextWeek)

                                // 차트 그려주는 함수
                                fun showChart() {
                                    if (BarChartVariables.firstTargetData == null || BarChartVariables.secondTargetData == null) {

                                        Toast.makeText(
                                            context,
                                            "운동 종류와 데이터를 선택해주세요.",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()

                                    } else {
                                        // barChart 초기화
                                        BarChartVariables.clearBarChart(barChart)

                                        // yAxis Title
                                        BarChartVariables.setYAxisTitle(yAxisTitleArea!!)

                                        // 보고자 하는 날짜 리스트
                                        val dateListOfTargetWeek = BarChartVariables.dateListOfWeek
                                        BarChartVariables.updateBarChartData(dateListOfTargetWeek) // BarChartVariables 클래스의 전역변수 update

                                        Log.d(
                                            "BarChart",
                                            "BarChartVariables.lastDateOfXIndex: ${BarChartVariables.lastDateOfXIndex}"
                                        )

                                        // 이번 주 날짜별 Sum
                                        val dailySumBarEntry =
                                            BarChartVariables.getDailySumBarEntry(
                                                dateListOfTargetWeek,
                                                BarChartVariables.firstTargetData!!,
                                                BarChartVariables.secondTargetData!!,
                                                DataBasket.friendExData!!
                                            )

                                        // 실제 Bar Data Set 생성.
                                        val barDataSet = BarDataSet(dailySumBarEntry, "exDataList")
                                        BarChartVariables.setExpressedDataFormat(barDataSet)

                                        // Bar Chart 데이터 삽입
                                        val data = BarData(barDataSet)
                                        barChart.data = data
                                        Log.d(
                                            "BarChart",
                                            "BarChartVariables.lastDateOfXIndex: ${BarChartVariables.lastDateOfXIndex}"
                                        )

                                        // Bar Chart 실행
                                        MyBarChartGenerator().runBarChart(
                                            barChart,
                                            barDataSet.yMax + 1.0f
                                        )

                                    }
                                } // showChart()

                                val radioGroup_1 = friendSTAlertDialog.findViewById<RadioGroup>(R.id.f_radioGp_1)
                                val radioGroup_2 = friendSTAlertDialog.findViewById<RadioGroup>(R.id.f_radioGp_2)

                                radioGroup_1!!.setOnCheckedChangeListener { group, checkedId ->
                                    when (checkedId) {
                                        R.id.f_btnSetSquatChart -> btnSetSquatChart!!.setOnClickListener {
                                            BarChartVariables.setFirstTargetData("squat", null)
                                            selectedExTypeArea.text = "스쿼트"

                                            if (BarChartVariables.secondTargetData != null) {
                                                showChart()
                                            }
                                        }
                                        R.id.f_btnSetPlankChart -> btnSetPlankChart!!.setOnClickListener {
                                            BarChartVariables.setFirstTargetData("plank", null)
                                            selectedExTypeArea.text = "플랭크"

                                            if (BarChartVariables.secondTargetData != null) {
                                                showChart()
                                            }
                                        }
                                        R.id.f_btnSetSideLateralRaiseChart -> btnSetSideLateralRaiseChart!!.setOnClickListener {
                                            BarChartVariables.setFirstTargetData(
                                                "sideLateralRaise",
                                                null
                                            )
                                            selectedExTypeArea.text = "사이드 래터럴 레이즈"

                                            if (BarChartVariables.secondTargetData != null) {
                                                showChart()
                                            }
                                        }

                                    }
                                }

                                radioGroup_2!!.setOnCheckedChangeListener { group, checkedId ->
                                    when (checkedId) {
                                        R.id.f_btnShowExCalorieChart -> btnSetExCalorieChart!!.setOnClickListener {
                                            BarChartVariables.setSecondTargetData(
                                                "ex_calorie",
                                                null
                                            )
                                            selectedDataArea!!.text = "칼로리 소모량"

                                            checkIsChartInitialization()
                                            showChart()

                                        }
                                        R.id.f_btnShowExCountChart -> btnSetExCountChart!!.setOnClickListener {
                                            BarChartVariables.setSecondTargetData("ex_count", null)
                                            selectedDataArea!!.text = "운동 횟수"

                                            checkIsChartInitialization()
                                            showChart()
                                        }

                                        R.id.f_btnShowExTimeChart -> btnSetExTimeChart!!.setOnClickListener {
                                            BarChartVariables.setSecondTargetData("ex_time", null)
                                            selectedDataArea!!.text = "운동 시간"

                                            checkIsChartInitialization()
                                            showChart()

                                        }
                                    }
                                } // radioGroup_2

                                /*
                                    이번 주, 지난 주, 다음 주 차트 보기
                                */
                                btnShowThisWeek!!.setOnClickListener { // 이번 주(1주일 전 ~ 오늘) 차트보기

                                    BarChartVariables.dateListOfWeek =
                                        DataBasket.getDateListOfThisWeek()
                                    showChart()

                                }

                                btnShowPreviousWeek!!.setOnClickListener {

                                    // 마지막에 저장된 X Index를 이용하여 일주일 전의 dateListOfTargetWeek을 생성
                                    if (BarChartVariables.lastDateOfXIndex == null) {
                                        Toast.makeText(context, "운동 종류와 데이터를 선택해주세요.", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        val lastDateOfXIndex = BarChartVariables.lastDateOfXIndex
                                        var (year: Int, month: Int, date: Int) = BarChartVariables.getYearMonthDateOfLastDate(
                                            lastDateOfXIndex!!
                                        )

                                        val dateOneWeekBefore =
                                            DataBasket.getDateOfOneWeekBeforeOrTomorrow(year, month, date, "Before")

                                        val triple = BarChartVariables.getYearMonthDateOfLastDate(dateOneWeekBefore)
                                        year = triple.first
                                        month = triple.second
                                        date = triple.third

                                        val dateListOfTargetWeek =
                                            DataBasket.getOneWeekListFromDate(year, month, date, "Before")

                                        BarChartVariables.updateBarChartData(dateListOfTargetWeek)

                                        showChart()
                                    }

                                }

                                btnShowNextWeek!!.setOnClickListener {

                                    // 마지막에 저장된 X Index를 이용하여 일주일 후의 dateListOfTargetWeek을 생성
                                    if (BarChartVariables.lastDateOfXIndex == null) {
                                        Toast.makeText(context, "운동 종류와 데이터를 선택해주세요.", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        val lastDateOfXIndex = BarChartVariables.lastDateOfXIndex
                                        var (year: Int, month: Int, date: Int) = BarChartVariables.getYearMonthDateOfLastDate(
                                            lastDateOfXIndex!!
                                        )

                                        val dateOfTomorrow =
                                            DataBasket.getDateOfOneWeekBeforeOrTomorrow(year, month, date, "Tomorrow")

                                        val triple = BarChartVariables.getYearMonthDateOfLastDate(dateOfTomorrow)
                                        year = triple.first
                                        month = triple.second
                                        date = triple.third

                                        val dateListOfTargetWeek =
                                            DataBasket.getOneWeekListFromDate(year, month, date, "After")

                                        BarChartVariables.updateBarChartData(dateListOfTargetWeek)

                                        showChart()
                                    }

                                }

                            }, 1000
                        )
//                        friendSTAlertDialog?.setCancelable(false) // 뒤로 가기 버튼을 눌러 종료 가능 여부
//                        btnFriendSTConfirm?.setOnClickListener {
//                            friendSTAlertDialog.dismiss()
//                        }
                    } // listView?.onItemClickListener

                // 확인 버튼
                btnFriendListConfirm?.setOnClickListener {
                    friendListAlertDialog.dismiss()
                }

            } // btnShowFriendList

            // 친구 등록
            btnRegisterFriend?.setOnClickListener {
                val registerDialog =
                    CustomDialog(context, R.layout.dialog_friend_register, "")
                val registerAlertDialog = registerDialog.showDialog()
                registerAlertDialog?.setCancelable(false)

                val friendCodeArea =
                    registerAlertDialog?.findViewById<EditText>(R.id.friendCodeArea)
                val friendNicknameArea =
                    registerAlertDialog?.findViewById<TextView>(R.id.friendNicknameArea)
                val btnSearchFriend =
                    registerAlertDialog?.findViewById<Button>(R.id.btnSearchFriend)
                val btnRegisterFriendToDB =
                    registerAlertDialog?.findViewById<Button>(R.id.btnRegisterFriendToDB)
                val btnRegisterConfirm =
                    registerAlertDialog?.findViewById<Button>(R.id.btnRegisterConfirm)

                var friendCode: String? = null
                var friendNickname: String? = null

                // 검색 버튼
                btnSearchFriend?.setOnClickListener {
                    friendCode = friendCodeArea?.text?.toString()?.trim()

                    if (friendCode == firebaseAuth.currentUser?.uid) { // 자기 자신을 검색한 경우

                        Toast.makeText(context, "본인은 친구로 등록할 수 없습니다.", Toast.LENGTH_SHORT).show()

                    } else { // 자기 자신을 검색하지 않은 경우

                        if (DataBasket.individualFriendInfo?.value != null) { // friend_info 데이터를 만든적이 있다면

                            if (!checkIfFriendExists(friendCode)) { // 등록된 유저가 아니라면, 서버에서 검색

                                searchUserFromDB(
                                    database,
                                    friendCode,
                                    friendNickname,
                                    friendNicknameArea
                                )

                            }

                        } else { // friend_info 데이터를 만든적이 없다면, 서버에서 검색

                            searchUserFromDB(
                                database,
                                friendCode,
                                friendNickname,
                                friendNicknameArea
                            )

                        }

                    }
                }

                // 친구를 DB에 저장
                btnRegisterFriendToDB?.setOnClickListener {
                    if (friendNicknameArea?.text != null && friendNicknameArea.text != "회원 코드를 확인해주세요.") {
                        val friendNicknameAreaText = friendNicknameArea.text.toString().trim()
                        for (character in friendNicknameAreaText) {
                            if (character.toString() == "님") {
                                friendNickname = friendNicknameAreaText.slice(
                                    IntRange(
                                        0,
                                        friendNicknameAreaText.indexOf(character) - 1
                                    )
                                )
                            }
                        }
                    }

                    if (friendCode != null && friendNickname != null) {

                        val friendDataModel =
                            FriendDataModel(friendCode, friendNickname)  // data model 생성
                        val dbPath = database.getReference("users")
                            .child(firebaseAuth.currentUser?.uid!!).child("friend_info")

                        // data write
                        dbPath.push().setValue(friendDataModel)
                            .addOnSuccessListener {
                                Toast.makeText(context, "친구 등록 완료", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error: 친구 등록 실패", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    } else {
                        Toast.makeText(
                            context,
                            "검색을 먼저  해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                btnRegisterConfirm?.setOnClickListener {

                    registerAlertDialog.dismiss()

                }

            } // btnRegisterFriend

            // 내 친구 코드 확인
            btnCheckMyCode?.setOnClickListener {

                val myCodeDialog = CustomDialog(context, R.layout.dialog_friend_my_code, "")
                val myCodeAlertDialog = myCodeDialog.showDialog()
                val myCodeArea = myCodeAlertDialog?.findViewById<TextView>(R.id.myCodeArea)
                val btnMyCodeConfirm =
                    myCodeAlertDialog?.findViewById<Button>(R.id.btnMyCodeConfirm)
                myCodeAlertDialog?.setCancelable(false)

                myCodeArea?.text = firebaseAuth.currentUser?.uid

                // 클립보드 복사하기
                val clipboardManager: ClipboardManager =
                    requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData = ClipData.newPlainText("myCode", myCodeArea?.text)
                clipboardManager.primaryClip = clipData
                Toast.makeText(
                    context,
                    "회원코드 \"${myCodeArea?.text}\"이 클립보드에 저장되었습니다.",
                    Toast.LENGTH_LONG
                ).show()

                btnMyCodeConfirm?.setOnClickListener {
                    myCodeAlertDialog.dismiss()
                }

            } // btnCheckMyCode

            btnFriendModeConfirm?.setOnClickListener {

                friendModeAlertDialog.dismiss()

            }

        } // btnRunFriendMode

        // 커뮤니티 버튼을 눌러 커뮤니티 액티비티로 이동
        val btnRunCommunity = view.findViewById<Button>(R.id.town_community)
        btnRunCommunity.setOnClickListener {
            val toCommunityActivity = Intent(context, CommunityActivity::class.java)
            startActivity(toCommunityActivity)
        }

        return view
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun searchUserFromDB(
        database: FirebaseDatabase,
        friendCode: String?,
        friendNickname: String?,
        friendNicknameArea: TextView?
    ) {
        val dbPath =
            database.getReference("users").child(friendCode!!)
                .child("user_info")

        dbPath.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userInfoModel =
                    dataSnapshot.getValue(UserInfoDataModel::class.java)

                if (userInfoModel != null) {
                    friendNicknameArea?.text = "${userInfoModel.user_nickname}님을 찾았습니다."
                } else {
                    friendNicknameArea?.text = "회원 코드를 확인해주세요."
                }

                Log.d("db_data", dataSnapshot.toString())
                Log.d(
                    "db_data",
                    dataSnapshot.getValue(UserInfoDataModel::class.java)
                        .toString()
                )

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db_data", "onCancelled")
            }
        })
    }

    private fun checkIfFriendExists(friendCode: String?): Boolean {

        for (dataModel in DataBasket.individualFriendInfo!!.children) {
            val friendInfoDataModel =
                dataModel.getValue(FriendDataModel::class.java)

            if (friendInfoDataModel?.friend_uid == friendCode) {
                Toast.makeText(
                    context,
                    "${friendInfoDataModel!!.friend_nickname} 님은 이미 친구로 등록된 유저입니다",
                    Toast.LENGTH_SHORT
                ).show()

                return true
            }

        }

        return false
    }

    private fun checkIsChartInitialization() {

//        var returnValue = false

        if (BarChartVariables.isFirstBarChart) {
            BarChartVariables.isFirstBarChart = false
            BarChartVariables.dateListOfWeek = DataBasket.getDateListOfThisWeek()
//            returnValue = true
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyTownFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =

            MyTownFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}


