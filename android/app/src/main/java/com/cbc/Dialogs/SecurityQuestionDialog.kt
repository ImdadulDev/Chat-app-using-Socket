package com.cbc.Dialogs


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import kotlinx.android.synthetic.main.fragment_security_question_dialog.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecurityQuestionDialog.newInstance] factory method to
 * create an instance of this fragment.
 */

const val QUESTIONONE = 1
const val QUESTIONTWO = 2

class SecurityQuestionDialog : DialogFragment() {
    // TODO: Rename and change types of parameters

    /***** STATIC QUESTION FOR SECURITY (Note) That only Addition but not removed and modified allowed ***********/
    var securityarrayone = ArrayList<String>()


    lateinit var onSelectQuestion: OnSelectQuestion

    interface OnSelectQuestion {
        fun Select(auestionfor: Int, questionNo: Int, answare: String)
    }

    private var param1: Int? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_question_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Window.FEATURE_NO_TITLE
        onSelectQuestion = activity as OnSelectQuestion
        toolbar.title = "Select a question"
        ALERT_REC.layoutManager = LinearLayoutManager(activity)
        securityarrayone.add("Which phone number do you remember most from your childhood? ")
        securityarrayone.add("What are the last 5 digits of your driver's license number?")
        securityarrayone.add("What is the first name of the boy or girl that you first kissed?")
        securityarrayone.add("Who was your childhood hero?")
        securityarrayone.add("Where were you when you had your first alcoholic drink (or cigarette)?")
        securityarrayone.add("When you were young, what did you want to be when you grew up?")
        securityarrayone.add("What was your favorite place to visit as a child?")
        securityarrayone.add("Who is your favorite actor, musician, or artist?")
        securityarrayone.add("What is your favorite movie?")


        when (param1) {
            QUESTIONONE -> {
                ALERT_REC.adapter =
                    AdapterQuestion(securityarrayone)

            }
            QUESTIONTWO -> {
                var securityarraytwo = ArrayList<String>()
                securityarraytwo = securityarrayone.clone() as ArrayList<String>
                securityarraytwo.removeAt(param2!!)
                ALERT_REC.adapter =
                    AdapterQuestion(securityarraytwo)

            }
        }


    }


    companion object {

        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
            SecurityQuestionDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }


    /************ ADAPTER FOR DIALOG ***********/

    inner class AdapterQuestion(val array: ArrayList<String>) :
        RecyclerView.Adapter<AdapterQuestion.VHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
            return VHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_question,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return array.size
        }

        override fun onBindViewHolder(holder: VHolder, position: Int) {
            val data = array[position]
            holder.TXT_QUESTION.text = data
            holder.itemView.setOnClickListener {
                onSelectQuestion.Select(param1!!, position, data!!)
                dismissAllowingStateLoss()
            }

        }

        inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val TXT_QUESTION = itemView.findViewById<TextView>(R.id.TXT_QUESTION)
        }

    }


}
