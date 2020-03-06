package com.fanchen.message.messages;

import android.view.View;
import android.widget.TextView;

import com.fanchen.message.commons.models.IMessage;
import com.fanchen.ui.R;

public class ReCallViewHolder<Message extends IMessage> extends BaseMessageViewHolder<Message>
        implements MsgListAdapter.DefaultMessageViewHolder{

    public ReCallViewHolder(View itemView, boolean isSender) {
        super(itemView);
       TextView v =  itemView.findViewById(R.id.aurora_tv_msgitem_recall);
       v.setText(isSender ? mContext.getString(R.string.recalled_send_message) : mContext.getString(R.string.recalled_receive_message));
    }

    @Override
    public void onBind(final Message message) {
    }

    @Override
    public void applyStyle(MessageListStyle style) {
    }

}
