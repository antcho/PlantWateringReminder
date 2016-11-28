package fr.lille1.raingeval.plantwateringreminder.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.lille1.raingeval.plantwateringreminder.R;
import fr.lille1.raingeval.plantwateringreminder.entities.Plant;

/**
 * Created by anthony on 24/11/16.
 */

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantViewHolder> {

    private PlantClickListener clickListener;
    private PlantLongClickListener longClickListener;
    private List<Plant> dataset;

    public interface PlantClickListener {
        void onPlantClick(int position);
    }

    public interface PlantLongClickListener {
        void onPlantLongClick(int position);
    }


    static class PlantViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView wateringFrequency;

        public PlantViewHolder(View itemView, final PlantClickListener clickListener, final PlantLongClickListener longClickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewPlantName);
            wateringFrequency = (TextView) itemView.findViewById(R.id.textViewPlantWateringFrequency);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onPlantClick(getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (longClickListener != null) {
                        longClickListener.onPlantLongClick(getAdapterPosition());
                    }
                    return false;
                }
            });
        }
    }

    public PlantsAdapter(PlantClickListener clickListener, PlantLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.dataset = new ArrayList<>();
    }


    public void setPlants(@NonNull List<Plant> plants) {
        dataset = plants;
        notifyDataSetChanged();
    }

    public Plant getPlant(int position) {
        return dataset.get(position);
    }

    @Override
    public PlantsAdapter.PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(PlantsAdapter.PlantViewHolder holder, int position) {
        Plant plant = dataset.get(position);
        holder.name.setText(plant.getName());
        holder.wateringFrequency.setText(Integer.toString(plant.getWateringFrequency()));
        View itemView = holder.itemView;

        int daysSinceLastWatering = plant.getDaysSinceLastWatering();
        int frequency = plant.getWateringFrequency();
        if (daysSinceLastWatering < frequency - 1){
            itemView.setBackgroundColor(Color.parseColor("#8BC34A"));
        } else if (daysSinceLastWatering > frequency) {
            itemView.setBackgroundColor(Color.parseColor("#FF5722"));
        } else {
            itemView.setBackgroundColor(Color.parseColor("#FF9800"));
        }



    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
