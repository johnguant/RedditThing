package com.johnguant.redditthing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnguant.redditthing.PostFragment.OnListFragmentInteractionListener;
import com.johnguant.redditthing.redditapi.model.Link;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Link} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private List<Link> mValues;
    private final OnListFragmentInteractionListener mListener;
    LoadMoreListener more;
    Context mContext;

    public MyPostRecyclerViewAdapter(List<Link> values, OnListFragmentInteractionListener listener, LoadMoreListener more, Context context) {
        mListener = listener;
        mValues = values;
        this.more = more;
        mContext = context;
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
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mAuthorView.setText(mValues.get(position).getAuthor());
        holder.mScoreView.setText(String.format(Locale.ENGLISH ,"%d", mValues.get(position).getScore()));
        holder.mLinkFlairTextView.setText(mValues.get(position).getLinkFlairText());
        holder.mSubredditView.setText(mValues.get(position).getSubreddit());
        holder.mDomainView.setText(mValues.get(position).getDomain());
        if(!mValues.get(position).getThumbnail().equals("self") && !mValues.get(position).getThumbnail().equals("default") && !mValues.get(position).getThumbnail().equals("nsfw")) {
            Picasso.with(mContext).load(mValues.get(position).getThumbnail()).into(holder.mThumbnailView);
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
        public final ImageView mThumbnailView;
        public Link mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mAuthorView = (TextView) view.findViewById(R.id.author);
            mScoreView = (TextView) view.findViewById(R.id.score);
            mLinkFlairTextView = (TextView) view.findViewById(R.id.link_flair_text);
            mSubredditView = (TextView) view.findViewById(R.id.subreddit);
            mDomainView = (TextView) view.findViewById(R.id.domain);
            mThumbnailView = (ImageView) view.findViewById(R.id.preview);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
