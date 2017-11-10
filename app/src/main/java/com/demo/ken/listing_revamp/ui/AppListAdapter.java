package com.demo.ken.listing_revamp.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.demo.ken.listing_revamp.R;
import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;
import com.demo.ken.listing_revamp.mvp.AppListPresenter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ken.chow on 4/7/2017.
 */
public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<TopAppPojo.Feed.Entry> entrys = new ArrayList<>();
    private List<TopAppPojo.Feed.Entry> recommendEntries = new ArrayList<>();

    private final int VIEW_HEADER = 2;
    private final int VIEW_ODD = 1;
    private final int VIEW_EVEN = 0;
    private final int VIEW_PROG = -1;

    private int hasRecommendList = 0;

    private int lastPosition = -1;

    public AppListAdapter( Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_list,parent,false);
            vh = new HeaderViewHolder(view);
        } else if(viewType == VIEW_ODD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_app_odd,parent,false);
            vh = new ViewHolder(view);
        }else if(viewType == VIEW_EVEN){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_app_even,parent,false);
            vh = new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar,parent,false);
            vh = new ProgressViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ViewHolder){
            final TopAppPojo.Feed.Entry entry = entrys.get(position-hasRecommendList);

            ImageLoader.getInstance().displayImage(entry.imImage.get(2).label, ((ViewHolder) holder).icon);
            ((ViewHolder) holder).itemIndex.setText(String.valueOf(position+1-hasRecommendList));
            ((ViewHolder) holder).label.setText(entry.imName.label);
            ((ViewHolder) holder).category.setText(entry.category.attributes.label);
            ((ViewHolder) holder).rateCount.setText(entry.userRatingCount!=null ?
                    "(" + String.valueOf(entry.userRatingCount) + ")"
                    : "has no enough rating yet");

            ((ViewHolder) holder).rateBar.setVisibility(entry.averageUserRating!=null ? View.VISIBLE : View.GONE);
            ((ViewHolder) holder).rateBar.setRating(entry.averageUserRating!=null ? entry.averageUserRating : 0);

            setAnimation(holder.itemView, position);

        }else if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).recommendListAdapter.setOnSaleItems(recommendEntries);
        }
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        if(holder instanceof ViewHolder){
            ((ViewHolder)holder).clearAnimation();
        }
    }

    @Override
    public long getItemId(int position) {
        return 246810+position;
    }

    @Override
    public int getItemCount() {
        return entrys.size() + hasRecommendList ;
    }

    @Override
    public int getItemViewType(int position) {
        return (hasRecommendList > 0  && position == 0) ? VIEW_HEADER :
                this.entrys.get(position-hasRecommendList)==null ? VIEW_PROG :
                        (position-hasRecommendList-1)%2 == 0 ? VIEW_ODD :
                                VIEW_EVEN;
    }

    public void setRecommendEntries(List<TopAppPojo.Feed.Entry> entrys) {
        this.recommendEntries = entrys;
        hasRecommendList = entrys.size() > 0 ? 1 : 0;
        notifyDataSetChanged();
    }

    public void setSearchItems(List<TopAppPojo.Feed.Entry> entrys) {
        this.entrys = entrys;
        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemIndex;
        public ImageView icon;
        public TextView label;
        public TextView category;
        public RatingBar rateBar;
        public TextView rateCount;

        public ViewHolder(View itemView) {
            super(itemView);
            itemIndex = (TextView) itemView.findViewById(R.id.item_index);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
            label = (TextView) itemView.findViewById(R.id.label);
            category = (TextView) itemView.findViewById(R.id.category);
            rateBar = (RatingBar) itemView.findViewById(R.id.rate_bar);
            rateCount = (TextView) itemView.findViewById(R.id.rate_count);
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recommendList;
        public RecommendListAdapter recommendListAdapter;

        public HeaderViewHolder(View v) {
            super(v);
            Context context = itemView.getContext();
            recommendList = (RecyclerView) itemView.findViewById(R.id.recommend_list);
            recommendList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recommendListAdapter  = new RecommendListAdapter();
            recommendList.setAdapter(recommendListAdapter);
        }
    }
}
