package com.example.vikaspandey.graphviewtutorial;

/**
 * Created by vikaspandey on 31/5/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by vikaspandey on 29/6/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private Context context;
    private ArrayList<String> arrayList;

    private int itemTypeDefault = 1;

    public ItemAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.arrayList = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == itemTypeDefault) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_records, parent, false);
            return new ItemViewHolder(context, view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_records, parent, false);
        return new ItemViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.bindViewWithData(viewHolder, this, arrayList.get(position),
                    position);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return itemTypeDefault;
    }

    public void removeItem(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }
}

