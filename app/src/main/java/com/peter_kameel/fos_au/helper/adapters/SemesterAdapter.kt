package com.peter_kameel.fos_au.helper.adapters


import android.content.Context
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter_kameel.fos_au.interfaces.AdaptersListener
import com.peter_kameel.fos_au.R
import com.peter_kameel.fos_au.data.RoomDB
import com.peter_kameel.fos_au.databinding.SemesterItemBinding
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity

class SemesterAdapter(
    private val context: Context,
    private val listener: AdaptersListener,
    private val list: List<SemesterEntity>
) :
    RecyclerView.Adapter<SemesterAdapter.ViewHolder>() {

    var adapter: CoarseAdapter? = null
    private val room by lazy { RoomDB.room(context) }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.semester_item,
                p0,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        //Get the semester from list
        val semester = list[p1]
        p0.itemBinding.semester = semester

        //Set Coarse Recycler LayoutManager
        p0.recycler.setHasFixedSize(true)
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        p0.recycler.layoutManager = manager

        CoroutineHelper.ioToMain(
            { room.getCoarseAsync(semester.id) },
            {
                p0.itemBinding.totalCoarse.text = getNumOfCourses(it)
                p0.itemBinding.gpaText.text = "GPA: ${"%.2f".format(getGpa(it))}"
                p0.itemBinding.hourText.text = "Hours: ${getHours(it)}"
                adapter = it?.let { it1 -> CoarseAdapter(context, it1) }
                //Add adapter to coarse RecyclerView
                p0.recycler.adapter = adapter
            })

        //on click show or hide coarse
        p0.card.setOnClickListener {
            if (p0.recycler.visibility == VISIBLE) {
                //Remove the info
                p0.recycler.visibility = GONE
            } else {
                //Show the Info
                p0.recycler.visibility = VISIBLE
            }//End else
        }
        //add coarse
        p0.add.setOnClickListener { listener.onSemesterClickButton(p0.add, semester) }
        //update note
        p0.note.setOnClickListener { listener.onSemesterClickButton(p0.note, semester) }
        //delete semester
        p0.delete.setOnClickListener { listener.onSemesterClickButton(p0.delete, semester) }
        //edit semester
        p0.edit.setOnClickListener { listener.onSemesterClickButton(p0.edit, semester) }

    }

    inner class ViewHolder(val itemBinding: SemesterItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val recycler = itemBinding.RecycleTermItem
        val card = itemBinding.TermCard
        val add = itemBinding.addCorase
        val edit = itemBinding.edit
        val delete = itemBinding.delete
        val note = itemBinding.note
    }

    companion object {
        //item text fun
        fun getHours(list: List<CoarseEntity>?): Int {
            var x = 0
            if (list != null) {
                for (item in list) {
                    x += item.hour.toInt()
                }
            }
            return x
        }

        fun getGpa(list: List<CoarseEntity>?): Double {
            var x = 0.0
            if (list != null) {
                for (item in list) {
                        val point = when {
                            item.deg.toInt() < 50 -> 0.0
                            item.deg.toInt() in 50..54 -> 1.0
                            item.deg.toInt() in 55..59 -> 1.3
                            item.deg.toInt() in 60..62 -> 1.7
                            item.deg.toInt() in 63..64 -> 2.0
                            item.deg.toInt() in 65..69 -> 2.3
                            item.deg.toInt() in 70..74 -> 2.7
                            item.deg.toInt() in 75..79 -> 3.0
                            item.deg.toInt() in 80..84 -> 3.3
                            item.deg.toInt() in 85..89 -> 3.7
                            item.deg.toInt() in 90..100 -> 4.0
                            else -> item.deg.toDouble()
                        }
                    x += (point * item.hour.toInt())

                }
            }
            return if (x > 0) {
                x / getHours(list)
            } else {
                0.0
            }
        }

        private fun getNumOfCourses(list: List<CoarseEntity>?) =
            "Total Courses:  ${list?.size.toString()}"
    }
}
