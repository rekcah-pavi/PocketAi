package com.hci.pocketai.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.hci.pocketai.databinding.FragmentNotificationBinding;

public class ReflowFragment extends Fragment {

private FragmentNotificationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ReflowViewModel reflowViewModel =
                new ViewModelProvider(this).get(ReflowViewModel.class);

    binding = FragmentNotificationBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textReflow;
        reflowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}