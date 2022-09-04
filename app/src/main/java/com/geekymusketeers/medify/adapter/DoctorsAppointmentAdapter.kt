package com.geekymusketeers.medify.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geekymusketeers.medify.DoctorAppointment
import com.geekymusketeers.medify.PatientAppointment
import com.geekymusketeers.medify.R

class DoctorsAppointmentAdapter(var appointmentList: ArrayList<DoctorAppointment>) : RecyclerView.Adapter<DoctorsAppointmentAdapter.DoctorAppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_list,parent,false)
        return DoctorAppointmentViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorAppointmentViewHolder, position: Int) {

        val currentItem = appointmentList[position]

        holder.name.text = currentItem.PatientName + " (" + currentItem.PatientPhone + ")"
        holder.disease.text = currentItem.Disease + " - " + currentItem.PatientCondition
        holder.time.text = currentItem.Time
        holder.date.text = currentItem.Date
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }
    class DoctorAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.findViewById(R.id.nameDisplay)
        val disease: TextView = itemView.findViewById(R.id.diseaseDisplay)
        val time:TextView = itemView.findViewById(R.id.timeDisplay)
        val date:TextView = itemView.findViewById(R.id.dateDisplay)
    }
}