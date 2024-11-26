package com.hci.pocketai.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hci.pocketai.R;
import com.hci.pocketai.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Handle "Contact Us" text
        TextView textContactUs = root.findViewById(R.id.text_contact_us);
        textContactUs.setOnClickListener(v -> {
            String sourceCodeUrl = "https://github.com/rekcah-pavi/PocketAi";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceCodeUrl));
            startActivity(intent);
        });

        // Handle Night Mode Switch
        Switch nightModeSwitch = root.findViewById(R.id.switch1);

        // Check current night mode and set the switch state
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            nightModeSwitch.setChecked(true);
        } else {
            nightModeSwitch.setChecked(false);
        }

        // Add listener for switch toggle
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable night mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Disable night mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Recreate the activity to apply theme change
            requireActivity().recreate();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
