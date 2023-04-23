package amigo.app

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

// Custom Pager Adapter for BuildTripActivity's View Pager

class BuildTripPagerAdapter(fragmentManager: FragmentManager, val userName: String, val userID: String) : FragmentPagerAdapter(fragmentManager) {
    // Never destroy items from memory to retain state
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

    // Fragments saved in Hash Map
    private val fragments = LinkedHashMap<Int, Fragment>()

    override fun getItem(position: Int): Fragment {
        // if fragment not found
        return fragments[position] ?:
        // add new instance of the fragment to hashmap and return it
        when (position) {
            0 -> {
                fragments[position] = LocationFragment.newInstance()
                fragments[position]!!
            }
            1 -> {
                fragments[position] = DestinationFragment.newInstance()
                fragments[position]!!
            }
            2 -> {
                fragments[position] = TransportFragment.newInstance()
                fragments[position]!!
            }
            else -> LocationFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return PagesModel.values().size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return PagesModel.fromInt(position).title
    }
}