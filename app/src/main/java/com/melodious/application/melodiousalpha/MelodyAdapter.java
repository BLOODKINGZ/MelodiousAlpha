package com.melodious.application.melodiousalpha;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melodious.application.melodiousalpha.Models.Melody;
import com.melodious.application.melodiousalpha.Utilites.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MelodyAdapter extends RecyclerView.Adapter<MelodyAdapter.MelodyAdapterViewHolder>{
    private MediaPlayer mediaPlayer = new MediaPlayer();
    //private File[] melodyData;

    private ArrayList<Melody> melodyList = new ArrayList<>();

    private final MelodyAdapterOnClickHandler mClickHandler;


    public interface MelodyAdapterOnClickHandler{
        void onClick(Melody melody);
    }

    public  MelodyAdapter(MelodyAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;

    }

    public class MelodyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTracksView;
        final TextView durationView;

        public MelodyAdapterViewHolder(View view){
            super(view);
            mTracksView = (TextView) view.findViewById(R.id.tracks);
            durationView = (TextView) view.findViewById(R.id.list_melody_duration);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Melody melody = melodyList.get(adapterPosition);
            mClickHandler.onClick(melody);
        }


    }

    @NonNull
    @Override
    public MelodyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_melodies, viewGroup, false);
        return new MelodyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MelodyAdapterViewHolder holder, int position) {
        Melody currentMelody = melodyList.get(position);

        holder.mTracksView.setText(currentMelody.getName().split("\\.")[0]);

        holder.durationView.setText(currentMelody.getDuration());
    }

    @Override
    public int getItemCount() {
        if(melodyList == null){
            return 0;
        }
        else {
            return melodyList.size();
        }
    }

    public void setMelodyData(ArrayList xmelodyList){
        melodyList = xmelodyList;
        notifyDataSetChanged();
    }
}
