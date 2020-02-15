package com.fanchen.location.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.R;
import com.fanchen.location.adapter.SearchPositionAdapter;
import com.fanchen.location.adapter.ItemDecorntion;
import com.fanchen.location.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    private static Toast toast;
    private static Dialog progressDialog;


    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    /**
     * 发起弹窗
     *
     * @param context
     * @param loadText
     */
    public static void showDialogNumal(Context context, Object loadText) {
        if (context == null) {
            return;
        }
        progressDialog = new Dialog(context, R.style.BaseProgressDialog);
        progressDialog.setContentView(R.layout.dialog_map_loading_layout);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ProgressBar progressBarWaiting = (ProgressBar) progressDialog.findViewById(R.id.iv_loading);
        TextView tv_loading_text = (TextView) progressDialog.findViewById(R.id.tv_loading_text);
        if (loadText instanceof Integer) {
            tv_loading_text.setText(context.getString((int) loadText));
        } else if (loadText instanceof String) {
            tv_loading_text.setText((String) loadText);
        }
        progressDialog.show();
    }

    /**
     * 取消弹窗
     */
    public static void cencelDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 强制弹起键盘
     *
     * @param context
     * @param editText
     */
    public static void showSoftInput(Context context, View editText) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);//强制显示键盘
    }


    /**
     * 地图搜索弹窗
     *
     * @param context
     * @param listener
     * @param bean
     */
    @SuppressLint("WrongConstant")
    public static void showSearchPup(final Context context, final LocationBean bean, final BaseQuickAdapter.OnItemClickListener listener) {
        if (context == null || bean == null || listener == null) {
            return;
        }
        final List<LocationBean> datas = new ArrayList<>();
        final PoiSearch mPoiSearch = PoiSearch.newInstance();
        //获得pup的view
        View view = LayoutInflater.from(context).inflate(R.layout.layout_send_map_search, null, false);
        final EditText edt_search = (EditText) view.findViewById(R.id.edt_search);
        final TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        final TextView tv_close = (TextView) view.findViewById(R.id.tv_close);
        final RecyclerView recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerview.addItemDecoration(new ItemDecorntion(0, 1, 0, 1));
        final SearchPositionAdapter locatorAdapter = new SearchPositionAdapter(datas);
        recyclerview.setAdapter(locatorAdapter);
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //设置window的宽高   1 window的布局 2、window的宽  3、window的高  4、window是否获取焦点
//        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, screenHeight, true);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //设置window背景色
        window.setBackgroundDrawable(new ColorDrawable(00000000));
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        window.setFocusable(true);
        //设置键盘不遮盖
        showSoftInput(context, edt_search);
        window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置window动画
        window.setAnimationStyle(R.style.BaseAnimStyle);
        //设置window在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        recyclerview.setVisibility(View.GONE);
        tv_close.setVisibility(View.VISIBLE);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // 根据输入框的内容，进行搜索
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(bean.getCity())//城市
                            .keyword(s.toString())//检索关键字
                            .pageNum(0)//分页编码
                            .pageCapacity(50));//每页容量，默认10条
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error != PoiResult.ERRORNO.NO_ERROR) {
                    recyclerview.setBackgroundResource(R.color.transparent);
                    recyclerview.setVisibility(View.GONE);
                    tv_close.setVisibility(View.VISIBLE);
                    showToastShort(context, R.string.nothing_to_search);
                    return;
                }
                if (poiResult.getAllPoi() == null) {
                    recyclerview.setBackgroundResource(R.color.transparent);
                    recyclerview.setVisibility(View.GONE);
                    tv_close.setVisibility(View.VISIBLE);
                    showToastShort(context, R.string.nothing_to_search);
                    return;
                }
                recyclerview.setVisibility(View.VISIBLE);
                tv_close.setVisibility(View.GONE);
                recyclerview.setBackgroundResource(R.color.wight_grey);
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                //获取在线建议检索结果
                datas.clear();
                for (int i = 0; i < allPoi.size(); i++) {
                    PoiInfo info = allPoi.get(i);
                    if (info.location != null && !TextUtils.isEmpty(info.address) && !TextUtils.isEmpty(info.city)) {
                        LocationBean bean = new LocationBean();
                        bean.setLng(info.location.longitude);
                        bean.setLat(info.location.latitude);
                        bean.setAddress(info.address);
                        bean.setCity(info.city);
                        bean.setName(info.name);
                        if (!datas.contains(bean)) {
                            datas.add(bean);
                        }
                    }
                }
                locatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }

        });
        locatorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                window.dismiss();
                locatorAdapter.setSelectSearchItemIndex(position);
                locatorAdapter.notifyDataSetChanged();
                listener.onItemClick(adapter, view, position);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                mPoiSearch.destroy();
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                mPoiSearch.destroy();
            }
        });
    }

    public static void showMapChoiceDialog(final Context context,final double latitude, final double longitude,final String city){
        new AlertDialog.Builder(context).setTitle(R.string.selected_map)
                .setSingleChoiceItems(context.getResources().getStringArray(R.array.map_list_scan),-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        double[] doubles = MapUtils.map_bd2hx(latitude, longitude);
                        switch (which) {
                            case 0:
                                MapUtils.openBaiduMap(context, String.valueOf(latitude), String.valueOf(longitude), city);
                                break;
                            case 1:
                                MapUtils.openGDMap(context, String.valueOf(doubles[0]), String.valueOf(doubles[1]), city);
                                break;
                            case 2:
                                MapUtils.openTencentMap(context, String.valueOf(doubles[0]), String.valueOf(doubles[1]), city);
                                break;
                            case 3:
                                MapUtils.openGoogleMap(context, String.valueOf(doubles[0]), String.valueOf(doubles[1]), city);
                                break;
                        }
                    }
                }).create().show();
    }

}
