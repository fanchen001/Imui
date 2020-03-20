package com.fanchen.message.messages;

import android.view.View;
import android.widget.TextView;

import com.fanchen.message.commons.models.IMessage;
import com.fanchen.ui.R;


public class NotificationViewHolder<MESSAGE extends IMessage>  extends BaseMessageViewHolder<MESSAGE>
        implements MsgListAdapter.DefaultMessageViewHolder{

    private  TextView mTextView ;

    public NotificationViewHolder(View itemView, boolean isSender) {
        super(itemView);
        mTextView =  itemView.findViewById(R.id.aurora_tv_msgitem_event);
    }

    @Override
    public void onBind(final MESSAGE message) {
        if(mTextView != null)
            mTextView.setText(message.getText());
    }

    @Override
    public void applyStyle(MessageListStyle style) {
    }

}
