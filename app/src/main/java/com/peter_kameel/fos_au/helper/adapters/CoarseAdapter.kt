package com.peter_kameel.fos_au.helper.adapters


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.peter_kameel.fos_au.R
import com.peter_kameel.fos_au.data.repository.RoomRepository
import com.peter_kameel.fos_au.data.RoomDB
import com.peter_kameel.fos_au.databinding.CoarseItemBinding
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.ui.fragment.GPAFrag
import kotlinx.android.synthetic.main.coarse_dialog.*

class CoarseAdapter(
    private val context: Context,
    private val List: List<CoarseEntity>
) :
    RecyclerView.Adapter<CoarseAdapter.ViewHolder>() {

    private val room by lazy { RoomDB.room(context) }
    private val repository by lazy { RoomRepository(context) }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<CoarseItemBinding>
                (LayoutInflater.from(p0.context), R.layout.coarse_item, p0, false)
        )
    }

    override fun getItemCount() = List.size
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        //Get the coarse from list
        val coarse = List[p1]
        p0.itemBinding.course = coarse
        p0.itemBinding.model = this

        p0.edit.setOnClickListener { editCourse(coarse) }
        p0.delete.setOnClickListener { deleteCourse(coarse) }

    }

    class ViewHolder(val itemBinding: CoarseItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val edit = itemBinding.CoarseEdit
        val delete = itemBinding.CoarseDelete
    }

    fun deg(course: CoarseEntity): String = "Degree: ${course.deg}"
    fun hour(course: CoarseEntity): String = "Hours: ${course.hour}"

    //fun to edit course
    private fun editCourse(course: CoarseEntity) {
        //Show the Dialog
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.coarse_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //change add button hint to update
        dialog.CoarseADD.hint = "Update"
        //OnClick Add Button Will Save Coarse Info to Database
        dialog.CoarseADD.setOnClickListener {
            //Check if all data is field
            when {
                //Validate the data
                dialog.CoarseCode.text.isNullOrEmpty() -> dialog.CodeTextLayout.error =
                    "Enter Code"
                dialog.CoarseDeg.text.isNullOrEmpty() -> dialog.DegTextLayout.error =
                    "Enter Deg."
                dialog.CoarseDeg.text.toString().toInt() !in 0..100 -> dialog.DegTextLayout.error =
                    "Failed Deg."
                dialog.CoarseHours.text.isNullOrEmpty() -> dialog.HourTextLayout.error =
                    "Enter Hours"
                dialog.CoarseHours.text.toString().toInt() !in 1..4 -> dialog.HourTextLayout.error =
                    "Failed Hours"
                //if all data is ok add it to database
                else -> {
                    val course1 = CoarseEntity(
                        course.id,
                        course.userid,
                        course.semesterid,
                        dialog.CoarseCode.text.toString(),
                        dialog.CoarseDeg.text.toString(),
                        dialog.CoarseHours.text.toString(),
                        false
                    )
                    CoroutineHelper.ioToMain(
                        { repository.editCourse(course1) },
                        {
                            this.notifyDataSetChanged()
                            Toast.makeText(context, "Edit Success", Toast.LENGTH_SHORT).show()
                            //update gpa after edit
                            dialog.dismiss()
                        })
                }//End when
            }//End Listener
        }
        //OnClick Cancel Button dismiss
        dialog.CoarseCancel.setOnClickListener { dialog.dismiss() }
        //Show the Dialog
        dialog.show()
    }

    //fun to delete course
    private fun deleteCourse(course: CoarseEntity) {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(context)
        // Set the alert dialog title
        builder.setTitle("Deleting Course... ")
        // Display a message on alert dialog
        builder.setMessage("Are you Sure?\nYou will Delete this Course!")
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { _, _ ->
            CoroutineHelper.ioToMain(
                { room.deleteCourse(course) },
                {
                    notifyDataSetChanged()
                    //update gpa after delete the course
                })
        }
        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }
}