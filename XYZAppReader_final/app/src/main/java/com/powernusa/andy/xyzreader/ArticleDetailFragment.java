package com.powernusa.andy.xyzreader;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.powernusa.andy.xyzreader.data.ArticleLoader;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment
                implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = ArticleDetailFragment.class.getSimpleName();
    public static final String ARG_ID = "arg_id";
    private long mCurrentId;
    private TextView mArticleBody;
    private ImageView mImagePhoto;
    private TextView mArticleSubtile;
    private Cursor mCursor;
    private Toolbar mToolbar;
    private LinearLayout mMetaBar;
    private FloatingActionButton mFab;


    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    public static Fragment newArticleDetailFragment(long itemId){
        Bundle args = new Bundle();
        args.putLong(ARG_ID,itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            long id = (long) getArguments().get(ARG_ID);
            mCurrentId = id;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mArticleBody =(TextView) view.findViewById(R.id.article_body);
        mImagePhoto = (ImageView) view.findViewById(R.id.article_photo);
        mArticleSubtile = (TextView) view.findViewById(R.id.article_subtitle);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_title);
        mFab = (FloatingActionButton) view.findViewById(R.id.share_fab);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity().getSupportLoaderManager().initLoader(0,null,this);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ArticleLoader itemLoader = ArticleLoader.newInstanceForItemId(getActivity(),mCurrentId);
        return itemLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null){
            mCursor = cursor;
            mCursor.moveToFirst(); //!!!
            final String lbody = Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)).toString();
            mArticleBody.setText(lbody);
            String author = Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + cursor.getString(ArticleLoader.Query.AUTHOR)).toString();

            mArticleSubtile.setText(author);

            if (mToolbar != null) {

                mToolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }

            Glide.with(this)
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(mImagePhoto);

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(lbody)
                            .getIntent(), getString(R.string.action_share)));

                }
            });
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

}
