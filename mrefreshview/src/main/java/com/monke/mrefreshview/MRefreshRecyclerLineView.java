package com.monke.mrefreshview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monke.mrefreshview.base.MRefreshRecyclerBaseF;
import com.monke.mrefreshview.lineprogressbar.RefreshProgressBar;
import com.monke.mrefreshview.utils.DensityUtil;

public class MRefreshRecyclerLineView extends MRefreshRecyclerBaseF {
    private int colorRefreshBg = 0xFFC1C1C1;
    private int colorRefreshPro = 0xFF555555;
    private int colorLoadText = 0xFF555555;
    private int colorLoadBg = 0xFFFFFFFF;

    private RefreshProgressBar rpbRefresh;

    public MRefreshRecyclerLineView(@NonNull Context context) {
        super(context);
        initRefreshProgressColor();
    }

    public MRefreshRecyclerLineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(attrs, 0, 0);
        initRefreshProgressColor();
    }

    public MRefreshRecyclerLineView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(attrs, defStyleAttr, 0);
        initRefreshProgressColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MRefreshRecyclerLineView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(attrs, defStyleAttr, defStyleRes);
        initRefreshProgressColor();
    }

    protected void initData(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MRefreshRecyclerLineView, defStyleRes, 0);
            colorRefreshBg = typedArray.getColor(R.styleable.MRefreshRecyclerLineView_mrcv_color_refresh_bg, colorRefreshBg);
            colorRefreshPro = typedArray.getColor(R.styleable.MRefreshRecyclerLineView_mrcv_color_refresh_progress, colorRefreshPro);
            colorLoadBg = typedArray.getColor(R.styleable.MRefreshRecyclerLineView_mrcv_color_load_bg, colorLoadBg);
            colorLoadText = typedArray.getColor(R.styleable.MRefreshRecyclerLineView_mrcv_color_load_text, colorLoadText);
            typedArray.recycle();
        }
    }


    private void initRefreshProgressColor() {
        rpbRefresh = (RefreshProgressBar) getRefreshView().findViewById(R.id.rpb_refresh);
        rpbRefresh.setVisibility(GONE);
        rpbRefresh.setSecondMaxProgress(getPullToRefreshHeight());
        rpbRefresh.setBgColor(colorRefreshBg);
        rpbRefresh.setSecondColor(colorRefreshPro);
        rpbRefresh.setFontColor(colorRefreshPro);
        rpbRefresh.setSpeed(rpbRefresh.getSecondMaxProgress()/40);
    }

    @Override
    public float getSlideCalcu(float res) {
        return (res/3f);
    }

    @Override
    protected View getRefreshUI() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_mrefreshline_rcv_refresh, this, false);
    }

    @Override
    public int getMaxPullHeight() {
        return DensityUtil.dp2px(getContext(), 50);
    }

    @Override
    public int getPullToRefreshHeight() {
        return DensityUtil.dp2px(getContext(), 50);
    }

    @Override
    public void pullingUI(int step,int dur, float durRatio, int pulltoRefreshHeight, int maxPullHeight) {
        if (rpbRefresh.getVisibility() != View.VISIBLE)
            rpbRefresh.setVisibility(VISIBLE);
        rpbRefresh.setSecondDurProgress(dur);
    }

    @Override
    public void startRefreshUi(int pulltoRefreshHeight, int maxPullHeight) {
        if (rpbRefresh.getVisibility() != View.VISIBLE)
            rpbRefresh.setVisibility(VISIBLE);
        rpbRefresh.setIsAutoLoading(true);
    }

    @Override
    public void pullResUi() {
        rpbRefresh.setSecondDurProgressWithAnim(0);
    }

    @Override
    public View getloadingView(ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_mrefreshline_rcv_loadmore, parent, false);
        v.setBackgroundColor(colorLoadBg);
        ((TextView) v.findViewById(R.id.tv_loadmore)).setTextColor(colorLoadText);
        return v;
    }

    /*
    设置正在加载时样式布局
     */
    @Override
    public void startLoadingUI(View v) {
        ((TextView) v.findViewById(R.id.tv_loadmore)).setText(getContext().getResources().getText(R.string.tv_loadmore_view_loading));
    }

    /*
    设置加载更多失败样式布局
     */
    @Override
    public void errorLoadingUI(View v) {
        ((TextView) v.findViewById(R.id.tv_loadmore)).setText(getContext().getResources().getText(R.string.tv_loadmore_view_loaderror));
    }
}
