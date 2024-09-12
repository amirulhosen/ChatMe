package mir.jan.chatme;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

class AdapterChat extends RecyclerView.Adapter<AdapterChat.ChatHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPR_RIGHT = 1;
    Context context;
    List<ModelChat> list;
    String imageurl;
    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> list, String imageurl) {
        this.context = context;
        this.list = list;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new ChatHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new ChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        String message = list.get(position).getMessage();
        String timeStamp = list.get(position).getTimestamp();
        String type = list.get(position).getType();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.message.setText(message);
        holder.time.setText(timedate);
        try {
            Glide.with(context).load(imageurl).into(holder.image);
        } catch (Exception e) {

        }
        holder.message.setVisibility(View.VISIBLE);
        holder.message.setText(message);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPR_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView message, time;
        ImageView isSee;
        LinearLayout msglayput;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profilec);
            message = itemView.findViewById(R.id.msgc);
            time = itemView.findViewById(R.id.timetv);
            isSee = itemView.findViewById(R.id.isSeen);
            msglayput = itemView.findViewById(R.id.msglayout);
        }
    }
}

