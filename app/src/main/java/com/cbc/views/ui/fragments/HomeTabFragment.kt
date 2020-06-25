package com.cbc.views.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.cbc.R
import com.cbc.views.adapters.ViewPagerAdapter
import com.cbc.views.ui.home.HomeFragment
import com.google.android.material.tabs.TabLayout


class HomeTabFragment : Fragment() {
    var mCallback: EditTextValueListenerInterface? = null

    interface EditTextValueListenerInterface {
        fun onTextChange(text: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

        mCallback = try {
            context as EditTextValueListenerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$activity must implement EditTextValueListenerInterface"
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (view != null) {
            if (view!!.parent != null) {
                (view!!.parent as ViewGroup).removeView(view)
            }
            return view;
        }

        val v = inflater.inflate(R.layout.fragment_tab_home, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(view)
    }

    private fun initData(view: View?) {
        val viewPager = view!!.findViewById<ViewPager>(R.id.tabHomeViewPager)
        val tabs = view.findViewById<TabLayout>(R.id.tabHomeTabs)
        val tabFragSearchFilter = view.findViewById<EditText>(R.id.tabFragSearhFilter)

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(HomeFragment(), "Contact")
        adapter.addFragment(MyMessageFragment(), "My Message")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                // Check if this is the page you want
                //Log.d("----","page: $position")

            }
        })

        tabFragSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(newText: Editable?) {
                mCallback!!.onTextChange(newText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onDetach() {
        mCallback = null // => avoid leaking, thanks @Deepscorn
        super.onDetach()
    }
}
