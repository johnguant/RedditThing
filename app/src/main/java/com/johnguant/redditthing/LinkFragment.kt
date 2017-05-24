package com.johnguant.redditthing

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.johnguant.redditthing.LinkFragment.OnListFragmentInteractionListener
import com.johnguant.redditthing.redditapi.RedditApiService
import com.johnguant.redditthing.redditapi.ServiceGenerator
import com.johnguant.redditthing.redditapi.model.Link
import com.johnguant.redditthing.redditapi.model.Listing
import kotlinx.android.synthetic.main.fragment_post_list.*
import retrofit2.Call
import retrofit2.Callback

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class LinkFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mListener: OnListFragmentInteractionListener? = null
    private val mValues: MutableList<Link> = mutableListOf()
    private var adapter: LinkAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_post_list, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = context
        posts_refresh_layout.setOnRefreshListener(this)
        val layoutManager = LinearLayoutManager(context)
        list.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(list.context,
                layoutManager.orientation)
        list.addItemDecoration(dividerItemDecoration)
        adapter = LinkAdapter(mValues, mListener, {loadContent(mValues[mValues.size - 1])}, context)
        list.adapter = adapter
        loadContent()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context as OnListFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun loadContent() {
        mValues.clear()
        loadContent(null)
    }

    fun loadContent(lastPost: Link?) {
        posts_refresh_layout.isRefreshing = true
        val service = ServiceGenerator.createService(RedditApiService::class.java, context)
        val call = service.loadFrontpage(lastPost)
        call.enqueue(object : Callback<Listing<Link>> {
            override fun onResponse(call: Call<Listing<Link>>, response: retrofit2.Response<Listing<Link>>) {
                if (response.isSuccessful) {
                    if (lastPost == null) {
                        mValues.clear()
                    }
                    response.body().data!!.children!!.mapTo(mValues) { it.data }
                    adapter!!.notifyDataSetChanged()
                }
                posts_refresh_layout.isRefreshing = false
            }

            override fun onFailure(call: Call<Listing<Link>>, t: Throwable) {
                posts_refresh_layout.isRefreshing = false
            }
        })
    }

    override fun onRefresh() {
        loadContent()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(link: Link)
    }

    companion object {

        // TODO: Customize parameter initialization
        fun newInstance(): LinkFragment {
            val fragment = LinkFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
