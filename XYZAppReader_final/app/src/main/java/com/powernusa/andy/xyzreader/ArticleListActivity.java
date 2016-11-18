package com.powernusa.andy.xyzreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.powernusa.andy.xyzreader.data.ArticleLoader;
import com.powernusa.andy.xyzreader.data.UpdaterService;
import com.powernusa.andy.xyzreader.ui.ArticleList_Adapter;

public class ArticleListActivity extends AppCompatActivity
                                            implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private ArticleList_Adapter mArticleListAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private LocalBroadcastManager mLocalBroadcastManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mSwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            startService(new Intent(getApplicationContext(), UpdaterService.class));
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(UpdaterService.EXTRA_UPDATER_FILTER.equals(intent.getAction())){
                //Toast.makeText(getApplicationContext(),"Finished updating",Toast.LENGTH_SHORT).show();
                boolean isRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING,false);
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_article_list);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

        if(savedInstanceState == null){  //when the app starts, run this service
            startService(new Intent(this, UpdaterService.class));
        }

        int columnCount = getResources().getInteger(R.integer.grid_column_count);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mArticleListAdapter = new ArticleList_Adapter(this,null);
        mArticleListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mArticleListAdapter);

        // implementing swipe listener
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeListener);

        if(mNavigationView != null){
            setupNavigationView(mNavigationView);
        }

        //Intialize ArticleLoader
        getSupportLoaderManager().initLoader(0,null,this);
    }

    private void setupNavigationView(NavigationView navView) {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                boolean returnClicked = true;
                switch (itemId){
                    case R.id.info:
                        Toast.makeText(getApplicationContext(),"Show program info",Toast.LENGTH_LONG).show();
                        //TODO Implement Info activity
                        Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        returnClicked=false;

                }
                mDrawerLayout.closeDrawers();
                return returnClicked;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(UpdaterService.EXTRA_UPDATER_FILTER);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("MAIN",">>>curseo size: " + data.getCount());
        mArticleListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleListAdapter.swapCursor(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        boolean returnClicked = true;
        switch (itemId){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }

        return returnClicked;
    }
}
