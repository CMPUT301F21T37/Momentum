package com.example.momentum.sharing;

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

import com.example.momentum.databinding.FragmentSharingBinding;

public class SharingFragment extends Fragment {
    private SharingViewModel SharingViewModel;
    private FragmentSharingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharingViewModel =
                new ViewModelProvider(this).get(SharingViewModel.class);

        binding = FragmentSharingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSharing;
        SharingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
