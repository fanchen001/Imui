package im.sdk.debug.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/7/20.
 *
 * @desc :好友相关四大功能:添加 删除 获取列表 更新备注
 */
public class FriendContactManager extends Activity {

    private Button mBu_showFriendList;
    private TextView mTv_showFriendList;
    private Button mBt_addFriend;
    private Button mBt_delFriend;
    private EditText mEt_noteName;
    private EditText mEt_noteText;
    private Button mBt_updateNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        //获取好友列表
        mBu_showFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_showFriendList.setText("");
                ContactManager.getFriendList(new GetUserInfoListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {
                        if (i == 0) {
                            StringBuilder sb = new StringBuilder();
                            for (UserInfo info : list) {
                                sb.append(info);
                                sb.append("\n" + "\n");
                            }
                            if (sb.length() == 0) {
                                mTv_showFriendList.append("没有好友");
                            }
                            mTv_showFriendList.append(sb.toString());
                            Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            Log.i("FriendContactManager", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });

        //添加好友
        mBt_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendContactManager.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        //示例只删除列表中第一个
        mBt_delFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactManager.getFriendList(new GetUserInfoListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {
                        if (i == 0) {
                            if (list.size() == 0) {
                                Toast.makeText(getApplicationContext(), "列表为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            UserInfo info = list.get(0);
                            info.removeFromFriendList(new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                        Log.i("FriendContactManager", "UserInfo.removeFromFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "没有获取到好友列表", Toast.LENGTH_SHORT).show();
                            Log.i("FriendContactManager", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }

                    }
                });
            }
        });

        //更新好友备注;本示例更新的是列表中的第一个人
        mBt_updateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String noteName = mEt_noteName.getText().toString();
                final String noteText = mEt_noteText.getText().toString();
                ContactManager.getFriendList(
                        new GetUserInfoListCallback() {
                            @Override
                            public void gotResult(int i, String s, List<UserInfo> list) {
                                if (list.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "没有好友不能更新", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final UserInfo info = list.get(0);
                                if (TextUtils.isEmpty(noteName) && TextUtils.isEmpty(noteText)) {
                                    Toast.makeText(FriendContactManager.this, "请输入相关参数", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!TextUtils.isEmpty(noteName)) {
                                    info.updateNoteName(noteName, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == 0) {
                                                Toast.makeText(getApplicationContext(), "更新 note name 成功", Toast.LENGTH_SHORT).show();
                                                mTv_showFriendList.append("获取更新后的note name : " + info.getNotename());
                                            } else {
                                                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                                                Log.i("FriendContactManager", "UserInfo.updateNoteName" + ", responseCode = " + i + " ; Desc = " + s);
                                            }
                                        }
                                    });

                                }
                                if (!TextUtils.isEmpty(noteText)) {
                                    info.updateNoteText(noteText, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == 0) {
                                                mTv_showFriendList.append("获取更新后的note text : " + info.getNoteText());
                                                Toast.makeText(getApplicationContext(), "更新 note text 成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                                                Log.i("FriendContactManager", "UserInfo.updateNoteText" + ", responseCode = " + i + " ; Desc = " + s);
                                            }
                                        }
                                    });
                                }

                            }

                        }
                );
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_friend_contact_manager);

        mBu_showFriendList = (Button) findViewById(R.id.bu_show_friend_list);
        mTv_showFriendList = (TextView) findViewById(R.id.tv_show_friend_list);
        mBt_addFriend = (Button) findViewById(R.id.bt_add_friend);
        mBt_delFriend = (Button) findViewById(R.id.bt_del_friend);

        mEt_noteName = (EditText) findViewById(R.id.et_note_name);
        mEt_noteText = (EditText) findViewById(R.id.et_note_text);
        mBt_updateNote = (Button) findViewById(R.id.bt_update_note);

    }
}
