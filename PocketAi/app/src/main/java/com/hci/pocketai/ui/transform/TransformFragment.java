package com.hci.pocketai.ui.transform;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hci.pocketai.ImgtoTxtActivity;
import com.hci.pocketai.R;
import com.hci.pocketai.TextToVoiceActivity;
import com.hci.pocketai.TxtToImgActivity;
import com.hci.pocketai.VoiceToTxtActivity;
import com.hci.pocketai.databinding.FragmentTransformBinding;
import com.hci.pocketai.databinding.ItemTransformBinding;

import java.util.Arrays;
import java.util.List;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TransformViewModel transformViewModel =
                new ViewModelProvider(this).get(TransformViewModel.class);

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewTransform;
        TransformAdapter adapter = new TransformAdapter();
        recyclerView.setAdapter(adapter);

        // Handle item click events
        adapter.setOnItemClickListener((text, position) -> {
            if ("Text to Image".equals(text)) {
                Intent intent = new Intent(getContext(), TxtToImgActivity.class);
                startActivity(intent);
            }else if ("Voice to Text".equals(text)) {
                    Intent intent = new Intent(getContext(), VoiceToTxtActivity.class);
                    startActivity(intent);
            }else if ("Image to Text".equals(text)) {
                Intent intent = new Intent(getContext(), ImgtoTxtActivity.class);
                startActivity(intent);
            }else if ("Text to Voice".equals(text)) {
                Intent intent = new Intent(getContext(), TextToVoiceActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Comming soon!", Toast.LENGTH_SHORT).show();
                // Handle other items here if necessary
            }
        });

        transformViewModel.getTexts().observe(getViewLifecycleOwner(), adapter::submitList);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class TransformAdapter extends ListAdapter<String, TransformViewHolder> {

        private final List<Integer> drawables = Arrays.asList(
                R.drawable.txt_img,
                R.drawable.voice_to_txt,
                R.drawable.img_to_txt,
                R.drawable.txt_to_voice,
                R.drawable.bg_remove

        );

        private OnItemClickListener onItemClickListener;

        public interface OnItemClickListener {
            void onItemClick(String text, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        protected TransformAdapter() {
            super(new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            });
        }

        @NonNull
        @Override
        public TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TransformViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransformViewHolder holder, int position) {
            String text = getItem(position);
            holder.textView.setText(text);
            holder.imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(holder.imageView.getResources(),
                            drawables.get(position), null));

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(text, position);
                }
            });
        }
    }

    private static class TransformViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public TransformViewHolder(ItemTransformBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageViewItemTransform;
            textView = binding.textViewItemTransform;
        }
    }
}
