package com.demo.ken.listing_revamp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.ken.listing_revamp.R;
import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ken.chow on 4/7/2017.
 */
public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TopAppPojo.Feed.Entry> entrys = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final TopAppPojo.Feed.Entry entry = entrys.get(position);

        ImageLoader.getInstance().displayImage(entry.imImage.get(0).label, ((ViewHolder) holder).icon);

        ((ViewHolder) holder).label.setText(entry.imName.label);
        ((ViewHolder) holder).category.setText(entry.category.attributes.label);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return entrys.size();
    }

    public void setOnSaleItems(List<TopAppPojo.Feed.Entry> entrys) {
        this.entrys = entrys;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView label;
        public TextView category;

        public ViewHolder(View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.category);
            label = (TextView) itemView.findViewById(R.id.label);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

}
