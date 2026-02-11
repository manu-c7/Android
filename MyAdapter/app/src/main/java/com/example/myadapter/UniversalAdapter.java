package com.example.myadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class UniversalAdapter extends BaseAdapter {

    private final Context context;
    private final List<NewsItem> dataSource;
    private final LayoutInflater inflater;
    private final ImageDownloader imageDownloader;

    public UniversalAdapter(Context context, List<NewsItem> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
        this.inflater = LayoutInflater.from(context);
        this.imageDownloader = new ImageDownloader();
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_news, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.tvTitle);
            holder.shortDescription = convertView.findViewById(R.id.tvShortDescription);
            holder.thumbnail = convertView.findViewById(R.id.ivThumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewsItem item = dataSource.get(position);
        holder.title.setText(item.getTitle());
        holder.shortDescription.setText(item.getShortDescription());

        if (item.getThumbUrl() != null && !item.getThumbUrl().isEmpty()) {
            imageDownloader.download(item.getThumbUrl(), holder.thumbnail);
        } else {
            holder.thumbnail.setImageDrawable(null);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView shortDescription;
        ImageView thumbnail;
    }
}
