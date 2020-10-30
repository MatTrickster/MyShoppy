package com.e.myshoppy;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridViewAdapter extends BaseAdapter {

    public Integer[] img;
    public String[] category;
    private Context mContext;

    public GridViewAdapter(Context c, Integer[] img, String[] category) {
        mContext = c;
        this.category = category;
        this.img = img;
    }


    @Override
    public int getCount() {
        return 13;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_view_list_item, null);
            TextView textView = grid.findViewById(R.id.text);
            CircleImageView imageView = grid.findViewById(R.id.image);
            textView.setText(category[i]);
            imageView.setImageResource(img[i]);
        } else {
            grid = convertView;
        }

        return grid;

    }

}
