package com.example.gym_tracker.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_tracker.R

class StatisticsAdapter(
    private var entries: List<ExerciseEntry>,
    private var sets: List<ExerciseSet>,
) : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.text_date)
        val weightTextView: TextView = itemView.findViewById(R.id.text_weight)
        val repsTextView: TextView = itemView.findViewById(R.id.text_reps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_statistic, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val set = sets[position]
        val date = entries.find { it.entryId == set.entryId }?.date ?: "N/A"

        holder.dateTextView.text = date
        holder.weightTextView.text = "${set.weight} kg"
        holder.repsTextView.text = "${set.reps}x"
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    fun updateEntriesAndSets(entries: List<ExerciseEntry>, sets: List<ExerciseSet>) {
        this.entries = entries.sortedByDescending { it.date } // Einträge nach Datum absteigend
        this.sets = sets.sortedByDescending { it.entryId }    // Sets nach ID absteigend
        notifyDataSetChanged()
    }
}