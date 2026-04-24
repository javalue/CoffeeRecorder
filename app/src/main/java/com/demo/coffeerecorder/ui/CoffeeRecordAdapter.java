package com.demo.coffeerecorder.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.coffeerecorder.R;
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class CoffeeRecordAdapter extends RecyclerView.Adapter<CoffeeRecordAdapter.RecordViewHolder> {
    private final List<CoffeeRecordEntity> items = new ArrayList<>();
    private Listener listener;

    public void submitItems(List<CoffeeRecordEntity> records) {
        items.clear();
        items.addAll(records);
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView beanNameView;
        private final TextView createdAtView;
        private final TextView metaView;
        private final TextView subMetaView;
        private final Chip ratingChip;
        private final TextView priceView;
        private final TextView notesView;
        private final ImageView thumbnailView;
        private final TextView thumbnailFallbackView;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.imageThumbnail);
            thumbnailFallbackView = itemView.findViewById(R.id.tvThumbnailFallback);
            beanNameView = itemView.findViewById(R.id.tvBeanName);
            createdAtView = itemView.findViewById(R.id.tvCreatedAt);
            metaView = itemView.findViewById(R.id.tvMeta);
            subMetaView = itemView.findViewById(R.id.tvSubMeta);
            ratingChip = itemView.findViewById(R.id.chipRating);
            priceView = itemView.findViewById(R.id.tvPrice);
            notesView = itemView.findViewById(R.id.tvNotes);
        }

        void bind(CoffeeRecordEntity record, Listener listener) {
            beanNameView.setText(record.beanName);
            createdAtView.setText(CoffeeFormatters.formatDateTime(record.drankAt));
            CoffeePhotoLoader.bindThumbnail(
                    thumbnailView,
                    thumbnailFallbackView,
                    record.photoUri,
                    TextUtils.isEmpty(record.drinkType)
                            ? itemView.getContext().getString(R.string.home_icon_cup)
                            : record.drinkType.substring(0, 1)
            );
            metaView.setText(itemView.getContext().getString(
                    R.string.record_meta_format,
                    record.drinkType,
                    record.brewMethod,
                    record.cupSizeMl
            ));
            subMetaView.setText(itemView.getContext().getString(
                    R.string.record_sub_meta_format,
                    TextUtils.isEmpty(record.roaster) ? itemView.getContext().getString(R.string.app_name) : record.roaster,
                    TextUtils.isEmpty(record.origin) ? record.drinkType : record.origin
            ));
            ratingChip.setText(itemView.getContext().getString(R.string.record_rating_format, record.rating));

            if (record.priceYuan > 0) {
                priceView.setVisibility(View.VISIBLE);
                priceView.setText(CoffeeFormatters.formatPrice(itemView.getContext(), record.priceYuan));
            } else {
                priceView.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(record.notes)) {
                notesView.setVisibility(View.GONE);
            } else {
                notesView.setVisibility(View.VISIBLE);
                notesView.setText(itemView.getContext().getString(R.string.record_note_prefix, record.notes));
            }

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRecordClicked(record.id);
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (listener != null) {
                    listener.onRecordLongPressed(record.id);
                    return true;
                }
                return false;
            });
        }
    }

    public interface Listener {
        void onRecordClicked(long recordId);

        void onRecordLongPressed(long recordId);
    }
}
