package com.powernusa.andy.xyzreader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.powernusa.andy.xyzreader.data.ArticleLoader;

public class ArticleDetailActivity extends AppCompatActivity
                    implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = ArticleDetailActivity.class.getSimpleName();
    public static String EXTRA_CURRENT_ID = "current_id";
    private ViewPager mViewPager;
    private long mCurrentId;
    private Cursor mCursor;
    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);

        if(savedInstanceState == null){
            if(getIntent() != null){
                mCurrentId = getIntent().getLongExtra(EXTRA_CURRENT_ID,0);
            }
        }
        else{
            mCurrentId = savedInstanceState.getLong(EXTRA_CURRENT_ID);
        }
        getSupportLoaderManager().initLoader(0,null,this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_CURRENT_ID,mCurrentId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ArticleLoader articleLoader = ArticleLoader.newAllArticlesInstance(this);
        return articleLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(mCurrentId > 0){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                if(cursor.getLong(ArticleLoader.Query._ID) == mCurrentId){

                    final int position = cursor.getPosition();
                    mCursor = cursor;
                    myPagerAdapter.notifyDataSetChanged();
                    Log.d(LOG_TAG,"current pager item selected: " + position);
                    mViewPager.setCurrentItem(position,false);
                    break;
                }
                cursor.moveToNext();
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        myPagerAdapter.notifyDataSetChanged();

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            mCursor.moveToPosition(position);
            long id = mCursor.getLong(ArticleLoader.Query._ID);
            Log.d(LOG_TAG,"+++pager getItem called: " + id);
            return ArticleDetailFragment.newArticleDetailFragment(id);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount():0;
        }
    }
}
