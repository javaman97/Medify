package com.geekymusketeers.medify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.geekymusketeers.medify.databinding.ActivityAppointmentBookingBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ncorti.slidetoact.SlideToActView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AppointmentBooking : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentBookingBinding
    private lateinit var sharedPreference : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreference = baseContext.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val diseaseValue = HashMap<String, Int>()
        val conditionValue = HashMap<String, Int>()
        var TotalPoint = 0

        conditionValue["Critical"] = 10
        conditionValue["Urgent"] = 8
        conditionValue["Under-control"] = 5

        diseaseValue["Not sure"] = 10
        diseaseValue["Fever"] = 7
        diseaseValue["Cold & Flu"] = 6
        diseaseValue["Diarrhea"] = 6
        diseaseValue["Allergies"] = 5
        diseaseValue["Stomach Aches"] = 6
        diseaseValue["Conjunctivitis"] = 5
        diseaseValue["Dehydration"] = 4
        diseaseValue["Tooth ache"] = 6
        diseaseValue["Ear ache"] = 5
        diseaseValue["Food poisoning"] = 4







        val doctorUid = intent.extras!!.getString("Duid")
        val doctorName = intent.extras!!.getString("Dname")
        val doctorEmail = intent.extras!!.getString("Demail")
        val doctorPhone = intent.extras!!.getString("Dphone")

        // Date Picker
        binding.selectDate.setOnClickListener {
            // Initiation date picker with
            // MaterialDatePicker.Builder.datePicker()
            // and building it using build()
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                // formatting date in dd-mm-yyyy format.
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
                val date = dateFormatter.format(Date(it))
                binding.selectDate.setText(date)

            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }

        // Time Picker
        binding.selectTime.setOnClickListener {

            // instance of MDC time picker
            val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
                // set the title for the alert dialog
                .setTitleText("SELECT YOUR TIMING")
                // set the default hour for the
                // dialog when the dialog opens
                .setHour(12)
                // set the default minute for the
                // dialog when the dialog opens
                .setMinute(10)
                // set the time format
                // according to the region
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build()

            materialTimePicker.show(supportFragmentManager, "MainActivity")

            // on clicking the positive button of the time picker
            // dialog update the TextView accordingly
            materialTimePicker.addOnPositiveButtonClickListener {

                val pickedHour: Int = materialTimePicker.hour
                val pickedMinute: Int = materialTimePicker.minute

                // check for single digit hour hour and minute
                // and update TextView accordingly
                val formattedTime: String = when {
                    pickedHour > 12 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour - 12}:0${materialTimePicker.minute} pm"
                        } else {
                            "${materialTimePicker.hour - 12}:${materialTimePicker.minute} pm"
                        }
                    }
                    pickedHour == 12 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour}:0${materialTimePicker.minute} pm"
                        } else {
                            "${materialTimePicker.hour}:${materialTimePicker.minute} pm"
                        }
                    }
                    pickedHour == 0 -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour + 12}:0${materialTimePicker.minute} am"
                        } else {
                            "${materialTimePicker.hour + 12}:${materialTimePicker.minute} am"
                        }
                    }
                    else -> {
                        if (pickedMinute < 10) {
                            "${materialTimePicker.hour}:0${materialTimePicker.minute} am"
                        } else {
                            "${materialTimePicker.hour}:${materialTimePicker.minute} am"
                        }
                    }
                }

                // then update the preview TextView
                binding.selectTime.setText(formattedTime)
            }
        }


        // Booking Appointment
        binding.btnFinalbook.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{

            override fun onSlideComplete(view: SlideToActView) {
                val userName = sharedPreference.getString("name","").toString()
                val userPhone = sharedPreference.getString("phone","").toString()
                val userid = sharedPreference.getString("uid","").toString()
                val userPrescription = sharedPreference.getString("prescription", "").toString()

                val date = binding.selectDate.text.toString()
                val time = binding.selectTime.text.toString()
                val disease = binding.diseaseDropdown.text.toString()
                val situation = binding.situationDropdown.text.toString()
                TotalPoint += diseaseValue[disease]!!
                TotalPoint += conditionValue[situation]!!

                val appointmentD:HashMap<String,String> = HashMap() //define empty hashmap
                appointmentD["PatientName"] = userName
                appointmentD["PatientPhone"] = userPhone
                appointmentD["Time"] = time
                appointmentD["Date"] = date
                appointmentD["Disease"] = disease
                appointmentD["PatientCondition"] = situation
                appointmentD["Prescription"] = userPrescription
                appointmentD["TotalPoints"] = TotalPoint.toString().trim()

                val appointmentP : HashMap<String, String> = HashMap() //define empty hashmap
                appointmentP["DoctorUID"] = doctorUid.toString()
                appointmentP["DoctorName"] = doctorName.toString()
                appointmentP["DoctorPhone"] = doctorPhone.toString()
                appointmentP["Date"] = date
                appointmentP["Time"] = time
                appointmentP["Disease"] = disease
                appointmentP["PatientCondition"] = situation
                appointmentP["Prescription"] = userPrescription

                val appointmentDB_Doctor = FirebaseDatabase.getInstance().getReference("Doctor").child(doctorUid!!).child("DoctorsAppointments").child(date)
                appointmentDB_Doctor.child(userid).setValue(appointmentD)

                val appointmentDB_User_Doctor = FirebaseDatabase.getInstance().getReference("Users").child(doctorUid).child("DoctorsAppointments").child(date)
                appointmentDB_User_Doctor.child(userid).setValue(appointmentD)


                val appointmentDB_Patient = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("PatientsAppointments").child(date)
                appointmentDB_Patient.child(doctorUid).setValue(appointmentP)

                startActivity(Intent(baseContext,BookingDoneActivity::class.java))
                finish()
            }
        }


        // Disease List
        val items = listOf("Not sure", "Fever", "Cold & Flu", "Diarrhea", "Allergies", "Stomach Aches","Conjunctivitis", "Dehydration", "Tooth ache", "Ear ache", "Food poisoning")
        val adapter = ArrayAdapter(this, R.layout.list_items, items)
        binding.diseaseDropdown.setAdapter(adapter)

        // Situation List
        val situationItems = listOf("Critical", "Urgent", "Under-control")
        val situationAdapter = ArrayAdapter(this, R.layout.list_items, situationItems)
        binding.situationDropdown.setAdapter(situationAdapter)
    }

}

