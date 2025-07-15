package com.example.gym_tracker.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_tracker.R
import com.example.gym_tracker.StatisticsActivity
import com.example.gym_tracker.data.Exercise
import com.google.android.material.card.MaterialCardView
import com.example.gym_tracker.data.SetDao

class ExerciseAdapter(
    private var exercises: List<Exercise>,
    private var entries: List<ExerciseEntry>,
    private var sets: List<ExerciseSet>,
    private val lifecycleOwner: LifecycleOwner,
    private val setDao: SetDao
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var onItemClickListener: ((Exercise) -> Unit)? = null

    fun setOnItemClickListener(listener: (Exercise) -> Unit) {
        onItemClickListener = listener
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.text_exercise_name)
        val exerciseDetails: TextView = itemView.findViewById(R.id.text_exercise_details)
        val highestWeightTextView: TextView = itemView.findViewById(R.id.text_highest_weight)
        val workoutCard: MaterialCardView = itemView.findViewById(R.id.WorkoutCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    private fun displayHighestWeightForEntries(entryIds: List<Long>, holder: ExerciseViewHolder) {
        if (entryIds.isNotEmpty()) {
            setDao.getMaxWeightForEntries(entryIds).observe(lifecycleOwner) { highestWeight ->
                holder.highestWeightTextView.text = "Höchstes Gewicht: ${highestWeight ?: 0}"
            }
        } else {
            holder.highestWeightTextView.text = "Höchstes Gewicht: 0"
        }
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.exerciseName
        holder.exerciseDetails.text = "Muscle Group: ${exercise.muscleGroup}"

        // Filtern der Einträge nach Übung und Beobachtung des höchsten Gewichts
        val entryIds = entries.filter { it.exerciseId == exercise.exerciseId }.map { it.entryId }
        displayHighestWeightForEntries(entryIds, holder)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(exercise)
        }
    }

    fun updateEntriesAndSets(entries: List<ExerciseEntry>, sets: List<ExerciseSet>) {
        this.entries = entries
        this.sets = sets
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = exercises.size

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}
