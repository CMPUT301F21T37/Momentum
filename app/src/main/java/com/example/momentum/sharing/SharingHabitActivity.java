package com.example.momentum.sharing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.Habit;
import com.example.momentum.R;
import com.example.momentum.databinding.ActivitySharingHabitBinding;
import com.example.momentum.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SharingHabitActivity extends AppCompatActivity {

    private ActivitySharingHabitBinding binding;
    private SharingHabitViewModel mViewModel;
    private SharingHabitListAdapter mAdapter;
    private List<Habit> mHabitList;
    private FollowingEntity mFollowing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivitySharingHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        startObserve();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        Intent intent = getIntent();
        mFollowing = (FollowingEntity) intent.getSerializableExtra(Constants.FOLLOWING_INFO);
        mHabitList = new ArrayList<>();
        mViewModel = new ViewModelProvider(this).get(SharingHabitViewModel.class);
        mAdapter = new SharingHabitListAdapter(this, mFollowing, mHabitList);
        binding.back.setOnClickListener(view -> {
            finish();
        });
        binding.name.setText(mFollowing.getName());
        binding.uid.setText(getResources().getString(R.string.uid) + mFollowing.getUid());
        binding.habitsList.setAdapter(mAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startObserve() {
        mViewModel.habits.observe(this, list -> {
            mHabitList.clear();
            mHabitList.addAll(list);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.getFollowerHabits(mFollowing.getUid());
    }
}
