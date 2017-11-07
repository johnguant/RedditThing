package com.johnguant.redditthing

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.expandable.expandableItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.johnguant.redditthing.redditapi.RedditApiService
import com.johnguant.redditthing.redditapi.ServiceGenerator
import com.johnguant.redditthing.redditapi.model.Link
import com.johnguant.redditthing.redditapi.model.Listing
import com.johnguant.redditthing.redditapi.model.Subreddit
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import kotlinx.android.synthetic.main.activity_reddit_thing.*
import kotlinx.android.synthetic.main.app_bar_reddit_thing.*
import retrofit2.Call
import retrofit2.Response

class RedditThing : AppCompatActivity(), LinkFragment.OnListFragmentInteractionListener {
    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_thing)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val links = LinkFragment.newInstance("")
        supportFragmentManager.beginTransaction().replace(R.id.post_list, links).commit()

        supportActionBar?.title = "Frontpage"

        drawer = drawer {
            accountHeader {
                profile("john_guant")
            }
            primaryItem("Frontpage")
            primaryItem("All")
            expandableItem {
                name = "Subreddits"
                identifier = 5
                secondaryItem("Hello")
            }
        }

        prepareListData()
    }

    private fun prepareListData() {
        val service = ServiceGenerator.createService(RedditApiService::class.java, this)
        val call = service.getMySubreddits()
        call.enqueue(object: retrofit2.Callback<Listing<Subreddit>> {
            override fun onResponse(call: Call<Listing<Subreddit>>, response: Response<Listing<Subreddit>>) {
                if(response.isSuccessful){
                    val subredditsList = response.body()!!.data?.children as MutableList
                    subredditsList.sortBy { subreddit -> subreddit.data.displayName }
                    val subreddits = drawer.getDrawerItem(5)
                    val list = mutableListOf<SecondaryDrawerItem>()
                    subredditsList.forEach {
                        val item = SecondaryDrawerItem().withName(it.data.displayName).withLevel(2)
                        list.add(item)
                    }
                    subreddits.subItems.clear()
                    subreddits.subItems.addAll(list)
                    drawer.adapter.notifyAdapterDataSetChanged()
                }
            }
            override fun onFailure(call: Call<Listing<Subreddit>>?, t: Throwable?) {
                TODO("not implemented")
            }
        })

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.reddit_thing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onListFragmentInteraction(link: Link) {
        Log.d("redditThing", link.url)
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(link.url))
    }

    fun onSubredditSelection(name: String) {
        Log.d("redditThing", name)
        supportActionBar?.title = name
        val links = LinkFragment.newInstance(name)
        supportFragmentManager.beginTransaction().replace(R.id.post_list, links).commit()
        drawer_layout.closeDrawer(GravityCompat.START)
    }
}
