package com.cbc.views.adapters

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cbc.networkutils.Klog


class ViewPagerAdapter(childFragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), Filterable {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val string = p0.toString()
                Klog.d("## SEARCH-", string)

                val filterResult = FilterResults()
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {

            }

        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val createdFragment :Fragment = super.instantiateItem(container, position) as Fragment
        when (position) {
            0 -> {
                val firstTag: String? = createdFragment.tag
                //Log.d("----","firstTag: $firstTag" )
            }
            1 ->{
                val secondTag: String? = createdFragment.tag
                //Log.d("----","secondTag: $secondTag" )
            }
        }
        return createdFragment
    }
}