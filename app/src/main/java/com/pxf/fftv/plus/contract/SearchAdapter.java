package com.pxf.fftv.plus.contract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.GlideApp;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Video> mVideoList;

    private Context mContext;

    private OnClickListener mListener;

    SearchAdapter(Context context, ArrayList<Video> list, OnClickListener listener) {
        mContext = context;
        mVideoList = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Video video = mVideoList.get(position);

        holder.search_item_tv_name.setText(video.getTitle());
        holder.search_item_tv_area.setText("地区 " + video.getArea());
        holder.search_item_tv_director.setText("导演 " + video.getDirectors().get(0).getName());
        holder.search_item_tv_year.setText("年份 " + video.getYear());

        StringBuilder actors = new StringBuilder();
        for (int i = 0 ; i < video.getActors().size() ; i++) {
            actors.append(video.getActors().get(i).getName());
        }

        holder.search_item_tv_actor.setText("演员 " + actors.toString());
        holder.search_item_tv_content.setText(video.getDescription().trim());


        GlideApp.with(mContext).load(video.getImageUrl()).into(holder.search_item_iv_pic);

        holder.itemView.setOnClickListener(v -> mListener.onItemClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_item_iv_pic)
        ImageView search_item_iv_pic;

        @BindView(R.id.search_item_tv_name)
        TextView search_item_tv_name;

        @BindView(R.id.search_item_tv_director)
        TextView search_item_tv_director;

        @BindView(R.id.search_item_tv_actor)
        TextView search_item_tv_actor;

        @BindView(R.id.search_item_tv_area)
        TextView search_item_tv_area;

        @BindView(R.id.search_item_tv_year)
        TextView search_item_tv_year;

        @BindView(R.id.search_item_tv_content)
        TextView search_item_tv_content;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface OnClickListener {

        void onItemClick(int position);
    }
}
