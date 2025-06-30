package edu.northeastern.a6group7;

import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerHistoryAdapter extends RecyclerView.Adapter<StickerHistoryAdapter.ViewHolder> {
    private List<Sticker> stickers;
    private Context context;
    private String currentUser;


    public StickerHistoryAdapter(Context context, List<Sticker> stickers, String currentUser) {
        this.context = context;
        this.stickers = stickers;
        this.currentUser = currentUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImage;
        TextView senderText;
        TextView timestampText;

        public ViewHolder(View itemView) {
            super(itemView);
            stickerImage = itemView.findViewById(R.id.stickerImage);
            senderText = itemView.findViewById(R.id.senderText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }

    @Override
    public StickerHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerHistoryAdapter.ViewHolder holder, int position) {
        Sticker sticker = stickers.get(position);
        holder.senderText.setText("From: " + sticker.getSender());
        holder.timestampText.setText(sticker.getTimeStamp());

        int imageRes = R.drawable.sticker_heart;
        if ("sticker_star".equals(sticker.getStickerId())) {
            imageRes = R.drawable.sticker_star;
        } else if ("sticker_fire".equals(sticker.getStickerId())) {
            imageRes = R.drawable.sticker_fire;
        }

        holder.stickerImage.setImageResource(imageRes);
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }


}




