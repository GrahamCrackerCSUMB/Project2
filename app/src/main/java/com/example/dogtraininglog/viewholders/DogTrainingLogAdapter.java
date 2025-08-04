package com.example.dogtraininglog.viewholders;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.dogtraininglog.database.entities.DogLog;

public class DogTrainingLogAdapter extends ListAdapter<DogLog, DogTrainingLogViewHolder> {
  public DogTrainingLogAdapter(@NonNull DiffUtil.ItemCallback<DogLog> diffCallback){
      super(diffCallback);
  }

  @Override
    public DogTrainingLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
      return DogTrainingLogViewHolder.create(parent);
  }

    @Override
    public void onBindViewHolder(@NonNull DogTrainingLogViewHolder holder, int position) {
        DogLog current = getItem(position);
        holder.bind(current.toString());
    }

    public static class GymLogDiff extends DiffUtil.ItemCallback<DogLog>{
      @Override
      public boolean areItemsTheSame(@NonNull DogLog oldItem, @NonNull DogLog newItem) {
          return oldItem == newItem;
      }

      @Override
      public boolean areContentsTheSame(@NonNull DogLog oldItem, @NonNull DogLog newItem) {
          return oldItem.equals(newItem);
      }
  }


}
