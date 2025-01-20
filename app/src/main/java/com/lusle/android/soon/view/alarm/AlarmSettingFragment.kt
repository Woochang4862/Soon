package com.lusle.android.soon.view.alarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lusle.android.soon.R
import com.lusle.android.soon.model.schema.Alarm
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource
import com.squareup.picasso.Picasso
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmSettingFragment : Fragment(), OnDateSetListener, OnTimeSetListener,
    View.OnClickListener {

    companion object {
        const val KEY_ALARM_ID = "alarm_id"
        val TAG: String = AlarmSettingFragment::class.java.simpleName
    }

    private lateinit var poster: ImageView
    private lateinit var closeBtn: ImageView
    private lateinit var deleteBtn: ImageView
    private lateinit var aSwitch: SwitchMaterial
    private lateinit var title: TextView
    private lateinit var releaseDate: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var dateSection: RelativeLayout
    private lateinit var timeSection: RelativeLayout
    private lateinit var hiddenSection: RelativeLayout
    private lateinit var saveBtn: Button
    private lateinit var weekBtn: Button
    private lateinit var mTenDayBtn: Button
    private lateinit var pTenDayBtn: Button
    private lateinit var mOneDayBtn: Button
    private lateinit var pOneDayBtn: Button
    private lateinit var mOneHourBtn: Button
    private lateinit var pOneHourBtn: Button
    private lateinit var mOneMinBtn: Button
    private lateinit var pOneMinBtn: Button
    private lateinit var AMPMBtn: Button
    private val dateFormat =
        SimpleDateFormat("yyyy년 MM월 dd일",  /*Locale.getDefault()*/Locale.KOREAN)
    private val timeFormat = SimpleDateFormat("HH시 mm분")
    private val currentCal = Calendar.getInstance()
    private var active = true
    private var movieData: Movie? = null
    private var alarmData: Alarm? = null
    private lateinit var mPrefs: SharedPreferences
    private lateinit var am: AlarmManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alarm_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*// 백그라운드에서 실행되는 리시번느 따로 등록이 필요함
        val receiver = AlarmReceiver()
        requireActivity().registerReceiver(receiver, IntentFilter("com.lusle.android.soon.ALARM_START"))*/
        super.onViewCreated(view, savedInstanceState)
        mPrefs = requireContext().getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
        am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        closeBtn = view.findViewById(R.id.close_btn)
        saveBtn = view.findViewById(R.id.save_btn)
        poster = view.findViewById(R.id.poster)
        title = view.findViewById(R.id.title)
        releaseDate = view.findViewById(R.id.release_date)
        dateSection = view.findViewById(R.id.alarm_date_section)
        dateTextView = view.findViewById(R.id.alarm_date)
        timeSection = view.findViewById(R.id.alarm_time_section)
        timeTextView = view.findViewById(R.id.alarm_time)
        weekBtn = view.findViewById(R.id.weekBtn)
        mTenDayBtn = view.findViewById(R.id.mTenDayBtn)
        pTenDayBtn = view.findViewById(R.id.pTenDayBtn)
        mOneDayBtn = view.findViewById(R.id.mOneDayBtn)
        pOneDayBtn = view.findViewById(R.id.pOneDayBtn)
        mOneHourBtn = view.findViewById(R.id.mOneHourBtn)
        pOneHourBtn = view.findViewById(R.id.pOneHourBtn)
        mOneMinBtn = view.findViewById(R.id.mOneMinBtn)
        pOneMinBtn = view.findViewById(R.id.pOneMinBtn)
        AMPMBtn = view.findViewById(R.id.AMPMBtn)
        hiddenSection = view.findViewById(R.id.hidden_section)
        aSwitch = view.findViewById(R.id.alarm_switch)
        deleteBtn = view.findViewById(R.id.delete_btn)
        movieData = requireArguments().getSerializable("movie_info") as Movie?
        alarmData = requireArguments().getSerializable("alarm_info") as Alarm?
        if (alarmData != null)
            movieData = alarmData?.movie
        binding()
        saveBtn.setOnClickListener { save() }
        closeBtn.setOnClickListener { activity?.finish() }
        dateSection.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(
                    requireContext(),
                    android.R.style.Theme_DeviceDefault_Dialog_Alert,
                    this,
                    currentCal[Calendar.YEAR],
                    currentCal[Calendar.MONTH],
                    currentCal[Calendar.DAY_OF_MONTH]
                )
            datePickerDialog.show()
        }
        timeSection.setOnClickListener {
            val timePickerDialog =
                TimePickerDialog(
                    requireContext(),
                    android.R.style.Theme_DeviceDefault_Dialog_Alert,
                    this,
                    currentCal[Calendar.HOUR_OF_DAY],
                    currentCal[Calendar.MINUTE],
                    false
                )
            timePickerDialog.show()
        }
        weekBtn.setOnClickListener(this)
        mTenDayBtn.setOnClickListener(this)
        pTenDayBtn.setOnClickListener(this)
        mOneDayBtn.setOnClickListener(this)
        pOneDayBtn.setOnClickListener(this)
        mOneHourBtn.setOnClickListener(this)
        pOneHourBtn.setOnClickListener(this)
        mOneMinBtn.setOnClickListener(this)
        pOneMinBtn.setOnClickListener(this)
        AMPMBtn.setOnClickListener(this)
    }

    private fun save() {
        val tz = currentCal.timeZone
        val jodaTz = DateTimeZone.forID(tz.id)
        val dateTime = DateTime(currentCal.timeInMillis, jodaTz)
        val now = DateTime.now()
        val diff = now.millis - dateTime.millis
        if (diff >= 0) {
            Toast.makeText(requireContext(), "현재 시간 이후로 설정해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        alarmData?.let {
            alarmData = Alarm(movieData, currentCal.timeInMillis, it.pendingIntentID, active)
            Log.d(TAG, "save: data $it")
            val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                action = "com.lusle.android.soon.ALARM_START"
                putExtra(KEY_ALARM_ID, it.pendingIntentID) // 리시버로 들어가는 데이터는 커스텀 클래스는 안됨
                addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            }
            var pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                it.pendingIntentID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                am.cancel(pendingIntent)
            }
            pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                it.pendingIntentID,
                intent,
                PendingIntent.FLAG_IMMUTABLE // TODO: UPDATE_CURRENT
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        currentCal.timeInMillis,
                        pendingIntent
                    )
                } else {
                    Intent().also { intent ->
                        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        requireContext().startActivity(intent)
                    }
                }
            } else {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    currentCal.timeInMillis,
                    pendingIntent
                )
            }

            val pref = ReleaseAlarmDataSource(requireContext())
            val alarms = pref.alarms
            if (alarms.contains(alarmData))
                alarms.remove(alarmData)
            alarms.add(alarmData!!)
            pref.alarms = alarms
            Toast.makeText(requireContext(), "알림이 설정되었습니다", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        } ?: run {
            alarmData = Alarm(movieData, currentCal.timeInMillis, movieData.hashCode(), active)
            save()
        }
    }

    private fun binding() {
        alarmData?.let {
            hiddenSection.visibility = View.VISIBLE
            active = it.isActive
            Log.d(TAG, "binding: " + it.isActive)
            aSwitch.isChecked = active
            aSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                active = isChecked
                Log.d(TAG, "setOnCheckedChangeListener: $active")
            }
            deleteBtn.setOnClickListener { delete() }
        }
        movieData?.let {
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + it.posterPath).centerInside()
                .fit().error(R.drawable.ic_broken_image).into(poster)
            if (it.title != "") title.text = it.title
            val formatD = SimpleDateFormat("yyyy-MM-dd")
            if (it.releaseDate != "") {
                try {
                    formatD.parse(it.releaseDate)?.let { date ->
                        releaseDate.text = dateFormat.format(date)
                        alarmData?.let { alarmData ->
                            currentCal.timeInMillis = alarmData.milliseconds
                            dateTextView.text = dateFormat.format(currentCal.time)
                        } ?: run {
                            dateTextView.text = dateFormat.format(date)
                            currentCal.time = date
                            currentCal[Calendar.HOUR_OF_DAY] =
                                Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                            currentCal[Calendar.MINUTE] = Calendar.getInstance()[Calendar.MINUTE]
                        }
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
        timeTextView.text = timeFormat.format(currentCal.time)
    }

    private fun delete() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("삭제")
        alert.setMessage("정말 삭제하시겠습니까?")
        alert.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            alarmData?.let {
                val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    action = "com.lusle.android.soon.ALARM_START"
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    requireContext(), it.pendingIntentID, intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                am.cancel(pendingIntent)
                val pref = ReleaseAlarmDataSource(requireContext())
                val alarms = pref.alarms
                if (alarms.contains(it))
                    alarms.remove(it)
                pref.alarms = alarms
                Toast.makeText(requireContext(), "알림이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                requireActivity().finish()
            }
        }
        alert.setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alert.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        currentCal[year, month] = dayOfMonth
        dateTextView.text = dateFormat.format(currentCal.time)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        currentCal[Calendar.HOUR_OF_DAY] = hourOfDay
        currentCal[Calendar.MINUTE] = minute
        timeTextView.text = timeFormat.format(currentCal.time)
    }

    private fun changeDate(day: Int) {
        currentCal.add(Calendar.DAY_OF_MONTH, day)
        dateTextView.text = dateFormat.format(currentCal.time)
    }

    private fun changeTime(hour: Int, min: Int) {
        currentCal.add(Calendar.HOUR_OF_DAY, hour)
        currentCal.add(Calendar.MINUTE, min)
        timeTextView.text = timeFormat.format(currentCal.time)
    }

    override fun onClick(v: View) {
        var day = 0
        var hour = 0
        var min = 0
        when (v.id) {
            R.id.weekBtn -> day = -7
            R.id.mTenDayBtn -> day = -10
            R.id.pTenDayBtn -> day = 10
            R.id.mOneDayBtn -> day = -1
            R.id.pOneDayBtn -> day = 1
            R.id.mOneHourBtn -> hour = -1
            R.id.pOneHourBtn -> hour = 1
            R.id.mOneMinBtn -> min = -1
            R.id.pOneMinBtn -> min = 1
            R.id.AMPMBtn -> hour = if (currentCal[Calendar.HOUR_OF_DAY] in 0..11) 12 else -12
        }
        changeDate(day)
        changeTime(hour, min)
    }
}