package hk.hku.cs.hkufriendhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Objects;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private final List<Map<String, Object>> postList;

    public MainAdapter(List<Map<String, Object>> postList){
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> post = postList.get(position);

        holder.usernameTextView.setText((String) post.get("username"));
        holder.contentTextView.setText((String) post.get("content"));

//        Object statObj = post.get("stat");
//        if (statObj instanceof String) {
//            holder.statTextView.setText((String) statObj);
//        } else if (statObj instanceof Map) {
//
//        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView contentTextView;
        TextView statTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.post_username);
            contentTextView = itemView.findViewById(R.id.post_text);
            statTextView = itemView.findViewById(R.id.post_group_stat);
        }
    }
}
