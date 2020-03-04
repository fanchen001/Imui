package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupBasicInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import com.fanchen.R;

/**
 * Created by ${chenyn} on 16/3/30.
 *
 * @desc :修改群组信息
 */
public class UpdateGroupInfoActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    private EditText mEt_groupId;
    private EditText mEt_groupName;
    private Button mBt_updateGroupName;
    private ProgressDialog mProgressDialog = null;
    private long mGroupID;
    private EditText mEt_groupDesc;
    private EditText mEt_groupAvatarPath;
    private TextView mTv_updateInfo;
    private Button mBt_updateGroupDesc;
    private String mAvatarPath;
    private RadioGroup mRg_type;
    private GroupBasicInfo.Type groupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mEt_groupAvatarPath.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            }
        });

        mBt_updateGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                final String name = mEt_groupName.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mGroupID = Long.parseLong(id);
                    JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (null != groupInfo) {
                                groupInfo.updateName(name, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            mTv_updateInfo.setText("修改群组名成功" + "\n");
                                            mProgressDialog.dismiss();
                                        } else {
                                            mTv_updateInfo.setText("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                mTv_updateInfo.setText("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBt_updateGroupDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mGroupID = Long.parseLong(id);
                    final String desc = mEt_groupDesc.getText().toString();
                    JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (null != groupInfo) {
                                groupInfo.updateDescription(desc, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            mProgressDialog.dismiss();
                                            mTv_updateInfo.setText("修改群描述成功" + "\n");
                                        } else {
                                            mProgressDialog.dismiss();
                                            mTv_updateInfo.setText("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                            Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupDescription " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        }
                                    }
                                });
                            } else {
                                mProgressDialog.dismiss();
                                mTv_updateInfo.setText("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.bt_update_group_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(mAvatarPath)) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mGroupID = Long.parseLong(id);
                    JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (null != groupInfo) {
                                groupInfo.updateAvatar(new File(mAvatarPath), null, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            mProgressDialog.dismiss();
                                            mTv_updateInfo.setText("修改群头像成功" + "\n");
                                        } else {
                                            mProgressDialog.dismiss();
                                            mTv_updateInfo.setText("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                            Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupAvatar " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        }
                                    }
                                });
                            } else {
                                mProgressDialog.dismiss();
                                mTv_updateInfo.setText("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id和头像路径", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.bt_change_group_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mEt_groupId.getText().toString();
                if (!TextUtils.isEmpty(id) && groupType != null) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mGroupID = Long.parseLong(id);
                    JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (responseCode == 0) {
                                groupInfo.changeGroupType(groupType, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        mProgressDialog.dismiss();
                                        if (responseCode == 0) {
                                            Toast.makeText(getApplicationContext(), "修改群组类型成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mTv_updateInfo.setText("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        }
                                    }
                                });
                            } else {
                                mProgressDialog.dismiss();
                                mTv_updateInfo.setText("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id和群组类型", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.rb_private) {
                    groupType = GroupBasicInfo.Type.private_group;
                } else if (checkId == R.id.rb_public) {
                    groupType = GroupBasicInfo.Type.public_group;
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_update_group_info);

        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
        mEt_groupName = (EditText) findViewById(R.id.et_group_name);
        mBt_updateGroupName = (Button) findViewById(R.id.bt_update_group_name);
        mEt_groupDesc = (EditText) findViewById(R.id.et_group_desc);
        mTv_updateInfo = (TextView) findViewById(R.id.tv_update_info);
        mBt_updateGroupDesc = (Button) findViewById(R.id.bt_update_group_desc);
        mEt_groupAvatarPath = (EditText) findViewById(R.id.et_group_avatar_path);
        mRg_type = (RadioGroup) findViewById(R.id.rg_type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (null != cursor) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mAvatarPath = cursor.getString(columnIndex);
                mEt_groupAvatarPath.setText("新头像路径：" + mAvatarPath);
                cursor.close();
            }
        }
    }
}
