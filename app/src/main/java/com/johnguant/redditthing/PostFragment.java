package com.johnguant.redditthing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OnListFragmentInteractionListener mListener;
    private List<Link> mValues;
    private MyPostRecyclerViewAdapter adapter;
    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.posts_refresh_layout) SwipeRefreshLayout refreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostFragment() {
        mValues = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();
        refreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MyPostRecyclerViewAdapter(mValues, mListener, new MyPostRecyclerViewAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {
                loadContent(mValues.get(mValues.size()-1));
            }
        }, context);
        recyclerView.setAdapter(adapter);
        loadContent();
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
                refreshLayout.setRefreshing(false);
            }
        });
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
