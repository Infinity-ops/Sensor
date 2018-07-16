package com.example.vikaspandey.graphviewtutorial;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vikaspandey on 31/5/18.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private TextView textLabel;

    public ItemViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        textLabel = (TextView) view.findViewById(R.id.text);
    }

    public void bindViewWithData(RecyclerView.ViewHolder viewHolder, ItemAdapter adapter,
                                 final String item, int position) {

        textLabel.setText(item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ("Item Selected" + item), Toast.LENGTH_SHORT).show();
            }
        });
    }
}