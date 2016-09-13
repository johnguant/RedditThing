package com.johnguant.redditthing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.johnguant.redditthing.PostFragment.OnListFragmentInteractionListener;
import com.johnguant.redditthing.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private List<Post> mValues;
    private final OnListFragmentInteractionListener mListener;
    LoadMoreListener more;

    public MyPostRecyclerViewAdapter(List<Post> values, OnListFragmentInteractionListener listener, LoadMoreListener more) {
        mListener = listener;
        mValues = values;
        this.more = more;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position == getItemCount() - 5){
            more.loadMore();
        }
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mAuthorView.setText(mValues.get(position).author);
        holder.mScoreView.setText(String.format( "%d", mValues.get(position).score));
        holder.mLinkFlairTextView.setText(mValues.get(position).linkFlairText);
        holder.mSubredditView.setText(mValues.get(position).subreddit);
        holder.mDomainView.setText(mValues.get(position).domain);
        if(!mValues.get(position).previewImage.equals("self") && !mValues.get(position).previewImage.equals("default") && !mValues.get(position).previewImage.equals("nsfw")) {
            ImageLoader mImageLoader = VolleyQueue.getInstance(holder.mView.getContext()).getImageLoader();
            holder.mThumbnailView.setImageUrl(mValues.get(position).previewImage, mImageLoader);
            holder.mThumbnailView.setVisibility(View.VISIBLE);
        } else {
            holder.mThumbnailView.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface LoadMoreListener {
        void loadMore();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mAuthorView;
        public final TextView mScoreView;
        public final TextView mLinkFlairTextView;
        public final TextView mSubredditView;
        public final TextView mDomainView;
        public final NetworkImageView mThumbnailView;
        public Post mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mAuthorView = (TextView) view.findViewById(R.id.author);
            mScoreView = (TextView) view.findViewById(R.id.score);
            mLinkFlairTextView = (TextView) view.findViewById(R.id.link_flair_text);
            mSubredditView = (TextView) view.findViewById(R.id.subreddit);
            mDomainView = (TextView) view.findViewById(R.id.domain);
            mThumbnailView = (NetworkImageView) view.findViewById(R.id.preview);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
