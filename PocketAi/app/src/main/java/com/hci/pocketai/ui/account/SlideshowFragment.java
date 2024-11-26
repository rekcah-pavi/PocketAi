package com.hci.pocketai.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hci.pocketai.LoginActivity;
import com.hci.pocketai.R;
import com.hci.pocketai.databinding.FragmentAccountBinding;

public class SlideshowFragment extends Fragment {

    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.uname;
        final TextView umail = binding.umail;

        // Access SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Retrieve the value of uname from SharedPreferences
        String uname = "Hi "+sharedPreferences.getString("name", "Userr")+"!";

        String mail = sharedPreferences.getString("email", "test@mail.com");

        // Set the value to the TextView
        textView.setText(uname);
        umail.setText(mail);


        Button buttonLogout = root.findViewById(R.id.logout_bt);

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false); // Logout user
            editor.apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button PrivacyPolicy = root.findViewById(R.id.privacy_policy);

        PrivacyPolicy.setOnClickListener(v -> {
            String privacyPolicyUrl = "https://www.google.com/privacy-policy"; // Replace with your actual URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
            startActivity(intent);
        });

        Button RateUs = root.findViewById(R.id.rate_us);

        RateUs.setOnClickListener(v -> {
            String privacyPolicyUrl = "https://www.google.com/privacy-policy"; // Replace with your actual URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
            startActivity(intent);
        });


        ImageView Twitter = root.findViewById(R.id.twitter);

        Twitter.setOnClickListener(v -> {
            String privacyPolicyUrl = "https://www.x.com"; // Replace with your actual URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
            startActivity(intent);
        });

        ImageView Telegram = root.findViewById(R.id.telegram);

        Telegram.setOnClickListener(v -> {
            String privacyPolicyUrl = "https://t.me"; // Replace with your actual URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
            startActivity(intent);
        });

        ImageView Whatsapp = root.findViewById(R.id.whatsapp);

        Whatsapp.setOnClickListener(v -> {
            String privacyPolicyUrl = "https://www.whatsapp.com"; // Replace with your actual URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
            startActivity(intent);
        });





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
