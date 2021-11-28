package com.example.momentum.sharing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momentum.Habit;
import com.example.momentum.R;
import com.example.momentum.habits.HabitYearActivity;
import com.example.momentum.utils.Constants;

import java.util.List;

public class SharingHabitListAdapter extends RecyclerView.Adapter<SharingHabitListAdapter.ViewHolder> {

    private List<Habit> mData;
    private final Context mContext;
    private final FollowingEntity mFollowing;

    public SharingHabitListAdapter(Context context, FollowingEntity following, List<Habit> data) {
        mContext = context;
        mFollowing = following;
        mData = data;
    }

    public void setData(List<Habit> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_card_view, parent, false);
        return new SharingHabitListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = mData.get(position);
        if (habit != null) {
            holder.getTextView().setText(habit.getTitle());
            holder.getLayout().setOnClickListener(view -> {
                //Go to HabitYearActivity
                Intent intent = new Intent(mContext, HabitYearActivity.class);
                intent.putExtra(Constants.HABIT_TITLE, habit.getTitle());
                intent.putExtra(Constants.VISUAL_INDICATOR_USER, mFollowing.getUid());
                intent.putExtra(Constants.HABIT_FREQUENCY, habit.getWeekly_frequency());
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewGroup layout;
        private final TextView textView;

        public ViewHolder(@NonNull View view) {
            super(view);
            layout = (ViewGroup) view.findViewById(R.id.layout);
            textView = (TextView) view.findViewById(R.id.card_view_text);
        }

        public ViewGroup getLayout() {
            return layout;
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
