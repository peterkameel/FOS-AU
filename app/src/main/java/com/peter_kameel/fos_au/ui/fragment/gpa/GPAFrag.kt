package com.peter_kameel.fos_au.ui.fragment.gpa

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.peter_kameel.fos_au.interfaces.AdaptersListener
import com.peter_kameel.fos_au.R
import com.peter_kameel.fos_au.data.util.ShareTAG
import com.peter_kameel.fos_au.data.util.SharedPrefs
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.helper.adapters.SemesterAdapter
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.coarse_dialog.*
import kotlinx.android.synthetic.main.gpa_fragment.view.*
import kotlinx.android.synthetic.main.inter_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GPAFrag : Fragment(), AdaptersListener {

    //Recycler Adapter
    lateinit var adapter: SemesterAdapter
    private val scope by lazy { CoroutineScope(IO) }
    private lateinit var fragModel: GPAFragViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gpa_fragment, container, false)
        fragModel = ViewModelProviders.of(this).get(GPAFragViewModel::class.java)
        //call fun to get semester data from ViewModel then calc gpa
        CoroutineHelper.ioToMain(
            { fragModel.getSemester() },
            { fragModel.getGPA() })
        // RecycleView set LayoutManager
        view.RecycleTerms.setHasFixedSize(true)
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        view.RecycleTerms.layoutManager = manager

        //Observe the semester data from ViewModel and send it to adapter
        fragModel.semesterLiveData.observeForever {
            //Recycle View setAdapter
            adapter = SemesterAdapter(requireContext(), this, it)
            view.RecycleTerms.adapter = adapter
        }
        //Observe the total GPA
        fragModel.gpaLiveData.observeForever {
            val snack = Snackbar.make(view, "GPA : $it", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("Close", View.OnClickListener { snack.dismiss() })
            snack.show()
        }

        //add anew semester
        view.Semester_fab.setOnClickListener {
            //initialize bottom sheet
            val sheet = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            sheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            view.sheet_add_semester.setOnClickListener {
                interSemester()
                sheet.dismiss()
            }
            view.sheet_calc_gpa.setOnClickListener {
                fragModel.getGPA()
                sheet.dismiss()
            }
            sheet.setContentView(view)
            sheet.show()
        }

        //initialize the banner ade
        MobileAds.initialize(context)
        val adRequest = AdRequest.Builder().build()
        view.adView2.adListener = object : AdListener() {
            override fun onAdLoaded() {
                view.adView2.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: Int) {
                view.adView2.visibility = View.GONE
            }
        }
        view.adView2.loadAd(adRequest)

        return view
    }

    //Listener for Click on Semester
    override fun onSemesterClickButton(view: View, semester: SemesterEntity) {
        when (view.id) {
            R.id.addCorase -> insertCourse(semester)
            R.id.edit -> editSemester(semester)
            R.id.delete -> deleteSemester(semester)
            R.id.note -> updateNote(semester.id, semester.note)
        }
    }

    //fun for this fragment
    private fun interSemester() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.inter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.EnterTextLayout.hint = "Enter Semester Name EX: First Semester"
        //OnClick Add Buttons
        dialog.inter.setOnClickListener {
            //insert semester using interSemester fun from ViewModel
            val semester = SemesterEntity(
                0,
                SharedPrefs.readSharedString(context, ShareTAG().userID, ""),
                dialog.EnterText.text.toString(),
                "initial note",
                false
            )
            CoroutineHelper.ioToMain(
                { fragModel.interSemester(semester) },
                {
                    scope.launch { fragModel.getSemester() }
                    dialog.dismiss()
                })
        }//End OnClick Add
        //OnClick Cancel Buttons
        dialog.interCancel.setOnClickListener { dialog.dismiss() }//End Cancel
        //Show the dialog
        dialog.show()
    }

    //fun used in Semester Adapter
    //Used To Add Coarse To Database
    private fun insertCourse(semester: SemesterEntity) {
        //Show the Dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.coarse_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //OnClick Add Button Will Save Coarse Info to Database
        dialog.CoarseADD.setOnClickListener {
            if (dialog.CoarseCode.text.toString() == "0") {
                val coarse = CoarseEntity(
                    0,
                    SharedPrefs.readSharedString(context, ShareTAG().userID, ""),
                    semester.id.toString(),
                    dialog.CoarseCode.text.toString(),
                    dialog.CoarseDeg.text.toString(),
                    dialog.CoarseHours.text.toString(),
                    false
                )
                CoroutineHelper.ioToMain(
                    { fragModel.interCourse(coarse) },
                    {
                        //Notify Coarse adapter that data is changed
                        adapter.notifyDataSetChanged()
                        dialog.dismiss() // Dismiss dialog
                    })
            } else {
                //Check if all data is field
                when {
                    dialog.CoarseCode.text.isNullOrEmpty() -> dialog.CodeTextLayout.error =
                        "Enter Code"
                    dialog.CoarseDeg.text.isNullOrEmpty() -> dialog.DegTextLayout.error =
                        "Enter Deg."
                    dialog.CoarseDeg.text.toString()
                        .toInt() !in 0..100 -> dialog.DegTextLayout.error =
                        "Failed Deg."
                    dialog.CoarseHours.text.isNullOrEmpty() -> dialog.HourTextLayout.error =
                        "Enter Hours"
                    dialog.CoarseHours.text.toString()
                        .toInt() !in 1..4 -> dialog.HourTextLayout.error =
                        "Failed Hours"
                    //if all data is ok add it to database
                    else -> {
                        val coarse = CoarseEntity(
                            0,
                            SharedPrefs.readSharedString(context, ShareTAG().userID, ""),
                            semester.id.toString(),
                            dialog.CoarseCode.text.toString(),
                            dialog.CoarseDeg.text.toString(),
                            dialog.CoarseHours.text.toString(),
                            false
                        )
                        CoroutineHelper.ioToMain(
                            { fragModel.interCourse(coarse) },
                            {
                                //Notify Coarse adapter that data is changed
                                adapter.notifyDataSetChanged()
                                //Clear EditText to Enter another Coarse
                                dialog.CoarseCode.text = null
                                dialog.CoarseDeg.text = null
                                dialog.CoarseHours.text = null
                                dialog.CodeTextLayout.error = null
                                dialog.DegTextLayout.error = null
                                dialog.HourTextLayout.error = null
                            })
                    }//End when
                }//End Listener
            }
        }
        //OnClick Cancel Button dismiss
        dialog.CoarseCancel.setOnClickListener { dialog.dismiss() }
        //Show the Dialog
        dialog.show()
    }

    //Used To Edit Semester
    private fun editSemester(semester: SemesterEntity) {
        //Show the Dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.inter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.EnterTextLayout.hint = "Enter Semester Name Ex: First Semester"
        dialog.EnterText.setText(semester.name)
        //OnClick Add Button Will Save Update Info to Database
        dialog.inter.setOnClickListener {
            val semester1 = SemesterEntity(
                semester.id,
                semester.userid,
                dialog.EnterText.text.toString(),
                semester.note,
                false
            )
            CoroutineHelper.ioToMain(
                { fragModel.updateSemester(semester1) },
                {
                    scope.launch { fragModel.getSemester() }
                    dialog.dismiss()
                })
        }//End listener
        //OnClick Cancel Button dismiss
        dialog.interCancel.setOnClickListener { dialog.dismiss() }
        //Show the Dialog
        dialog.show()
    }

    //Used To Delete Semester
    private fun deleteSemester(semester: SemesterEntity) {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(context)
        // Set the alert dialog title
        builder.setTitle("Deleting Semester... ")
        // Display a message on alert dialog
        builder.setMessage("Are you Sure?\nYou will Delete this Semester!")
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { _, _ ->
            // Delete semester && it`s related courses
            CoroutineHelper.io3Jobs(
                { fragModel.deleteSemesterCourses(semester.id) },
                { fragModel.deleteSemester(semester) }, { _, _ -> fragModel.getSemester() })
        }
        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }

    //Update the Note
    private fun updateNote(id: Int, note: String) {
        //Show the Dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.inter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //read the old one
        dialog.EnterText.setText(note)
        //OnClick Add Button Will Save Update Info to Database
        dialog.inter.setOnClickListener {
            CoroutineHelper.ioToMain(
                { fragModel.updateNote(dialog.EnterText.text.toString(), id) },
                {
                    scope.launch { fragModel.getSemester() }
                    dialog.dismiss()
                })
        }//End listener
        //OnClick Cancel Button dismiss
        dialog.interCancel.setOnClickListener { dialog.dismiss() }
        //Show the Dialog
        dialog.show()
    }

}