package com.johnguant.redditthing

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.graphics.Typeface
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.johnguant.redditthing.LinkFragment.OnListFragmentInteractionListener
import com.johnguant.redditthing.redditapi.model.Link
import com.johnguant.redditthing.redditapi.model.Thing
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post.view.*
import net.dean.jraw.models.Submission
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [Link] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class LinkAdapter(private val mValues: List<Link>, private val mListener: OnListFragmentInteractionListener, internal var more: () -> Unit, private val mContext: Context) : PagedListAdapter<Submission, LinkAdapter.ViewHolder>(object: DiffCallback<Submission>() {
    override fun areItemsTheSame(oldLink: Submission, newLink: Submission): Boolean {
        return oldLink.id == newLink.id
    }

    override fun areContentsTheSame(oldLink: Submission, newLink: Submission): Boolean{
        return oldLink == newLink
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
               .inflate(R.layout.fragment_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount - 5) {
            more()
        }

        val link = mValues[position]

        holder.mItem = link
        holder.itemView.title.text = link.title

        val row1Builder = SpannableStringBuilder()
        val score = SpannableString(String.format(Locale.ENGLISH, "%dpts", link.score))
        row1Builder.append(score).append(" ")
        val author = SpannableString(link.author)
        row1Builder.append(author)

        holder.itemView.link_row1.setText(row1Builder, TextView.BufferType.SPANNABLE)

        val row2Builder = SpannableStringBuilder()
        if (!TextUtils.isEmpty(link.linkFlairText)) {
            val flairText = SpannableString(link.linkFlairText)
            row2Builder.append(flairText).append(" ")
        }
        val subreddit = SpannableString(link.subreddit)
        subreddit.setSpan(StyleSpan(Typeface.BOLD), 0, subreddit.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        row2Builder.append(subreddit).append(" ")
        val domain = SpannableString(link.domain)
        row2Builder.append(domain).append(" ")
        val created = SpannableString(DateUtils.getRelativeTimeSpanString(link.createdUtc * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE))
        row2Builder.append(created)

        holder.itemView.link_row2.setText(row2Builder, TextView.BufferType.SPANNABLE)

        if (link.thumbnail!!.contains("thumbs.redditmedia.com")) {
            Picasso.with(mContext).load(link.thumbnail).resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size).centerCrop().into(holder.itemView.preview)
            holder.itemView.preview.visibility = View.VISIBLE
        } else {
            holder.itemView.preview.visibility = View.GONE
        }
        holder.mView.setOnClickListener {
            mListener.onListFragmentInteraction(holder.mItem)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        lateinit var mItem: Link
        override fun toString(): String {
            return super.toString() + " '" + itemView.title.text + "'"
        }
    }
}

