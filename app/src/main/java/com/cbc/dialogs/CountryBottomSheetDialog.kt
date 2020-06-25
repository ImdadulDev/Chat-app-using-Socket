package com.cbc.dialogs


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.networkutils.Klog
import com.cbc.R
import com.cbc.dialoglistener.OnCountryListner
import com.cbc.models.country.Country
import com.cbc.models.country.Countrydata
import com.cbc.utils.CuntryHelper
import kotlinx.android.synthetic.main.fragment_country_bottom_sheet_dialog.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CountryBottomSheetDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountryBottomSheetDialog(val listner: OnCountryListner) : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: AdapterList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_country_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        REC_COUNTRY.layoutManager = LinearLayoutManager(activity)
        val countryList = CuntryHelper.GetAllList(activity!!)
        adapter = AdapterList(countryList)
        REC_COUNTRY.adapter = adapter
//        adapter.filter.filter("B")
        EDX_SEARCH.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

        })

    }


    inner class AdapterList(val listdata: Countrydata) :
        RecyclerView.Adapter<AdapterList.vholder>(), Filterable {
        var finallist = listdata.country
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(p0: CharSequence?): FilterResults {

                    if (p0.isNullOrEmpty()) {
                        finallist = listdata.country
                    } else {
                        val search = p0.toString()
                        Klog.d("## SEARCH-", search)
                        val filterlis = ArrayList<Country>()
                        listdata.country.forEach {
                            if (it.name.toLowerCase().startsWith(search.toLowerCase())) {
                                filterlis.add(it)
                            }
                        }
                        finallist = filterlis
                    }
                    val filterresult = FilterResults()
                    filterresult.values = finallist
                    return filterresult
                }

                override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                    finallist = p1?.values as List<Country>
                    Klog.d("## SEARCHED-", finallist.size.toString())
                    notifyDataSetChanged()
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vholder {
            return vholder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
            )
        }

        override fun getItemCount(): Int {
            Klog.d("## SIZE-", finallist.size.toString())
            return finallist.size
        }

        override fun onBindViewHolder(holder: vholder, position: Int) {
            val item = finallist[position]
            val identifier =
                activity!!.getResources()
                    .getIdentifier(item.code.toLowerCase(), "drawable", activity!!.getPackageName())
            if (identifier > 0) {
                holder.IMG_FLAG.setImageResource(identifier)
            }
            holder.TEXTNAME.text = "(+${item.callingCode}) ${item.name}"

            holder.itemView.setOnClickListener {
                listner.SelectedCode("${item.callingCode}")
                listner.SelectedCountry(item.name)
                val identifier =
                    activity!!.getResources().getIdentifier(
                        item.code.toLowerCase(),
                        "drawable",
                        activity!!.getPackageName()
                    )
                if (identifier > 0) {
                    listner.selectedImages(identifier)
                }
                dismiss()
            }
        }

        inner class vholder(itemv: View) : RecyclerView.ViewHolder(itemv) {
            val IMG_FLAG = itemv.findViewById<ImageView>(R.id.IMG_FLAG)
            val TEXTNAME = itemv.findViewById<TextView>(R.id.TEXTNAME)
        }

    }


    override fun onResume() {
        val window = dialog!!.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        super.onResume()
    }
}
