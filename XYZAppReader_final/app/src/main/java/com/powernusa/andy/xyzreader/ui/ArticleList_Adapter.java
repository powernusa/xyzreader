package com.powernusa.andy.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.powernusa.andy.xyzreader.ArticleDetailActivity;
import com.powernusa.andy.xyzreader.R;
import com.powernusa.andy.xyzreader.data.ArticleLoader;

/**
 * Created by Andy on 11/8/2016.
 */

public class ArticleList_Adapter  extends CursorRecyclerViewAdapter<ArticleList_Adapter.ViewHolder>{
    public static final String LOG_TAG = ArticleList_Adapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    public ArticleList_Adapter(Context context, Cursor cursor) {
        super(context, cursor);
        mCursor = cursor;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.list_article_row,parent,false);
        final ViewHolder vh = new ViewHolder(rootView);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //equivalent to _id
                Intent intent = new Intent(mContext,ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.EXTRA_CURRENT_ID,vh.getItemId());
                mContext.startActivity(intent);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {


        viewHolder.mTitleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
        viewHolder.mAuthorView.setText("by " + cursor.getString(ArticleLoader.Query.AUTHOR));

        viewHolder.mSubtitleView.setText(
                DateUtils.getRelativeTimeSpanString(
                        cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                        System.currentTimeMillis(),DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString());


        viewHolder.mImageView.setAspectRatio(cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        Glide.clear(viewHolder.mImageView);
        Glide.with(viewHolder.mImageView.getContext())
                .load(cursor.getString((ArticleLoader.Query.THUMB_URL)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(viewHolder.mImageView);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View mItemView;
        public TextView mTitleView;
        public TextView mSubtitleView;
        public TextView mAuthorView;
        public DynamicHeightNetworkImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mTitleView = (TextView) itemView.findViewById(R.id.article_title);
            mSubtitleView = (TextView) itemView.findViewById(R.id.article_subtitle);
            mAuthorView = (TextView) itemView.findViewById(R.id.article_author);
            mImageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}
