package com.example.momentum.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentAddHabitBinding;

public class AddFragment extends Fragment {
    private AddViewModel AddViewModel;
    private FragmentAddHabitBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddHabitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAddHabit;
        AddViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
