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

import com.johnguant.redditthing.redditapi.RedditApiService;
import com.johnguant.redditthing.redditapi.ServiceGenerator;
import com.johnguant.redditthing.redditapi.model.Link;
import com.johnguant.redditthing.redditapi.model.Listing;
import com.johnguant.redditthing.redditapi.model.Thing;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

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
    private List<Link> mValues;
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
            adapter = new MyPostRecyclerViewAdapter(mValues, mListener, new MyPostRecyclerViewAdapter.LoadMoreListener() {
                @Override
                public void loadMore() {
                    loadContent(mValues.get(mValues.size()-1));
                }
            }, context);
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

    public void loadContent(final Link lastPost){
        refreshLayout.setRefreshing(true);
        RedditApiService service = ServiceGenerator.createService(RedditApiService.class, getContext());
        Call<Listing<Link>> call = service.loadFrontpage(lastPost);
        call.enqueue(new Callback<Listing<Link>>() {
            @Override
            public void onResponse(Call<Listing<Link>> call, retrofit2.Response<Listing<Link>> response) {
                if(response.isSuccessful()){
                    if(lastPost == null){
                        mValues.clear();
                    }
                    for(Thing<Link> c : response.body().getData().getChildren()){
                        mValues.add(c.getData());
                    }
                    adapter.notifyDataSetChanged();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Listing<Link>> call, Throwable t) {
                Log.d("redditThing", "it no work");
                refreshLayout.setRefreshing(false);
            }
        });
        /*new AsyncTask<Link, Void, Void>(){

            @Override
            protected Void doInBackground(Link... lastLink) {
                String url;
                if(lastPost[0] == null){
                    url = "https://oauth.reddit.com/";
                } else {
                    url = "https://oauth.reddit.com/?after=" + lastPost[0].kind + "_" + lastPost[0].id;
                }


                RedditRequest redditRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray posts =  response.getJSONObject("data").getJSONArray("children");
                            for(int i = 0; i<posts.length(); i++){
                                Link p = new Link(posts.getJSONObject(i));
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
                }, getContext());

                VolleyQueue.getInstance(getActivity()).addToRequestQueue(redditRequest);
                return null;
            }
        }.execute(lastPost);*/
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
        void onListFragmentInteraction(Link link);
    }
}
