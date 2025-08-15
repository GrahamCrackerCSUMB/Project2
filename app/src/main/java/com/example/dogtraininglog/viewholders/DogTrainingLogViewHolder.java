package com.example.dogtraininglog.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dogtraininglog.R;

/*Viewholders holds referenes to row views and binds the dogtraingin database to them*/
public class DogTrainingLogViewHolder extends RecyclerView.ViewHolder {
    private final TextView gymLogViewItem;

    /*Look up the row's child view and cache them*/
    private DogTrainingLogViewHolder(View gymLogView){
        super(gymLogView);
        gymLogViewItem = gymLogView.findViewById(R.id.recyclerItemTextview);
    }

    /*Add the data from the DogLog to the row's views*/
    public void bind (String text){
        gymLogViewItem.setText(text);
    }

    static DogTrainingLogViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doglog_recycler_item, parent, false);
        return new DogTrainingLogViewHolder(view);
    }
}
