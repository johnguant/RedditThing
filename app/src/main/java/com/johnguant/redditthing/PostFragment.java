package com.johnguant.redditthing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Post> mValues;
    private MyPostRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostFragment() {
        mValues = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostFragment newInstance(int columnCount) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("redditThing", "hello3");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Set the adapter
        Log.d("redditThing", "hello2");
        //if (view instanceof RecyclerView) {
            Context context = view.getContext();
            refreshLayout = (SwipeRefreshLayout) view;
            refreshLayout.setOnRefreshListener(this);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyPostRecyclerViewAdapter(mValues, mListener, view.getContext(), new MyPostRecyclerViewAdapter.LoadMoreListener() {
                @Override
                public void loadMore() {
                    loadContent(mValues.get(mValues.size()-1));
                }
            });
            recyclerView.setAdapter(adapter);
            loadContent();
            Log.d("redditThing", "hello1");
        //}
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadContent(){
        mValues.clear();
        loadContent(null);
    }

    public void loadContent(Post lastPost){
        refreshLayout.setRefreshing(true);

        String url;

        if(lastPost == null){
            url = "https://www.reddit.com/r/newzealand/.json";
        } else {
            url = "https://www.reddit.com/r/newzealand/.json?after=" + lastPost.kind + "_" + lastPost.id;
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray posts =  response.getJSONObject("data").getJSONArray("children");
                    for(int i = 0; i<posts.length(); i++){
                        Post p = new Post(posts.getJSONObject(i));
                        mValues.add(p);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
            }
        });

        VolleyQueue.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onRefresh() {
        loadContent();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Post item);
    }
}
