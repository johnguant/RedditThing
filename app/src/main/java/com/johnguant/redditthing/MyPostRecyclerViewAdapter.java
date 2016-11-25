package com.johnguant.redditthing;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Link} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private List<Link> mValues;
    private final OnListFragmentInteractionListener mListener;
    LoadMoreListener more;
    private Context mContext;

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

        Link link = mValues.get(position);

        holder.mItem = link;
        holder.mTitleView.setText(link.getTitle());

        SpannableStringBuilder row1Builder = new SpannableStringBuilder();
        SpannableString score = new SpannableString(String.format(Locale.ENGLISH ,"%dpts", link.getScore()));
        row1Builder.append(score).append(" ");
        SpannableString author = new SpannableString(link.getAuthor());
        row1Builder.append(author);

        holder.mRow1.setText(row1Builder, TextView.BufferType.SPANNABLE);

        SpannableStringBuilder row2Builder = new SpannableStringBuilder();
        if(!TextUtils.isEmpty(link.getLinkFlairText())) {
            SpannableString flairText = new SpannableString(link.getLinkFlairText());
            row2Builder.append(flairText).append(" ");
        }
        SpannableString subreddit = new SpannableString(link.getSubreddit());
        subreddit.setSpan(new StyleSpan(Typeface.BOLD), 0, subreddit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        row2Builder.append(subreddit).append(" ");
        SpannableString domain = new SpannableString(link.getDomain());
        row2Builder.append(domain).append(" ");
        SpannableString created = new SpannableString(DateUtils.getRelativeTimeSpanString(link.getCreatedUtc()*1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
        row2Builder.append(created);

        holder.mRow2.setText(row2Builder, TextView.BufferType.SPANNABLE);

        if(link.getThumbnail().equals("self") || link.getThumbnail().equals("default") || link.getThumbnail().equals("nsfw")) {
            holder.mThumbnailView.setVisibility(View.GONE);
        } else if(link.getThumbnail().equals("image")) {
            Picasso.with(mContext).load(link.getMedia().getOembed().getThumbnailUrl()).resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size).centerCrop().into(holder.mThumbnailView);
            holder.mThumbnailView.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(mContext).load(link.getThumbnail()).resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size).centerCrop().into(holder.mThumbnailView);
            holder.mThumbnailView.setVisibility(View.VISIBLE);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.title) TextView mTitleView;
        @BindView(R.id.link_row1) TextView mRow1;
        @BindView(R.id.link_row2) TextView mRow2;
        @BindView(R.id.preview) ImageView mThumbnailView;
        Link mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
