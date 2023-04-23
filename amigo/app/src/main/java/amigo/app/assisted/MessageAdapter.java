package amigo.app.assisted;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import amigo.app.R;

/**
 * This class structures and process the messaging list in chat, so that the messaging would be
 * displayed at two sides of the page.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private List<MessageBody> messgaeList;

    public MessageAdapter(List<MessageBody> messgaeList) {
        this.messgaeList = messgaeList;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msg_body, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {

        MessageBody msg = messgaeList.get(position);

        switch (msg.getType()) {
            case Received:
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftMsg.setText(msg.getMessage());
                break;
            case Sent:
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightMsg.setText(msg.getMessage());
                break;

        }
    }

    @Override
    public int getItemCount() {
        return messgaeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout leftLayout;
        ConstraintLayout rightLayout;

        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = (ConstraintLayout) itemView.findViewById(R.id.left_layout);
            rightLayout = (ConstraintLayout) itemView.findViewById(R.id.right_layout);
            leftMsg = (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg = (TextView) itemView.findViewById(R.id.right_msg);
        }
    }

}
