package com.monke.mrefreshview;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import com.monke.mrefreshview.circleprogressbar.CircleProgressBar;
import com.monke.mrefreshview.utils.DensityUtil;

public class MRefreshRecyclerMaterView extends MRefreshRecyclerBaseF {
    private int colorLoadText = 0xFF555555;
    private int colorLoadBg = 0xFFFFFFFF;
    private int colorRefresh = 0xFF555555;
    private final long RES_PULL_ANIM_DUR = 1000;

    private CircleProgressBar cpbRefresh;

    private ValueAnimator pullAnimator;

    public MRefreshRecyclerMaterView(@NonNull Context context) {
        super(context);
        initRefreshViewColor();
    }

    public MRefreshRecyclerMaterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(attrs,0,0);
        initRefreshViewColor();
    }

    public MRefreshRecyclerMaterView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(attrs,defStyleAttr,0);
        initRefreshViewColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MRefreshRecyclerMaterView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(attrs,defStyleAttr,defStyleRes);
        initRefreshViewColor();
    }

    @Override
    public float getSlideCalcu(float res) {
        return (res/1.5f);
    }

    private void initRefreshViewColor() {
        LayoutParams layoutParams = (LayoutParams) getRefreshView().getLayoutParams();
        layoutParams.topMargin = -refreshViewHeight();
        getRefreshView().setLayoutParams(layoutParams);
        cpbRefresh = (CircleProgressBar) getRefreshView().findViewById(R.id.cpb_refresh);
        cpbRefresh.setShowArrow(true);
        cpbRefresh.setmProgressColor(colorRefresh);
    }

    protected void initData(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MRefreshRecyclerMaterView, defStyleRes, 0);
            colorLoadBg = typedArray.getColor(R.styleable.MRefreshRecyclerMaterView_mrcv_color_load_bg, colorLoadBg);
            colorLoadText = typedArray.getColor(R.styleable.MRefreshRecyclerMaterView_mrcv_color_load_text, colorLoadText);
            colorRefresh = typedArray.getColor(R.styleable.MRefreshRecyclerMaterView_mrcv_color_refresh,colorRefresh);
            typedArray.recycle();
        }
    }

    @Override
    protected View getRefreshUI() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_mrefresh_rcv_refresh, this, false);
    }

    /**
     * 下拉刷新界面UI高度，用来设置初始位置
     *
     * @return
     */
    public int refreshViewHeight() {
        return DensityUtil.dp2px(getContext(), 40);
    }

    /**
     * 能触发下拉刷新最短长度
     *
     * @return
     */
    @Override
    public int getMaxPullHeight() {
        return DensityUtil.dp2px(getContext(), 90) + refreshViewHeight();
    }

    /**
     * 最多下拉长度
     *
     * @return
     */
    @Override
    public int getPullToRefreshHeight() {
        return DensityUtil.dp2px(getContext(), 45) + refreshViewHeight();
    }

    @Override
    public void pullingUI(int step,int dur, float durRatio, int pulltoRefreshHeight, int maxPullHeight) {
        if (cpbRefresh.isRunning())
            cpbRefresh.stop();
        if (pullAnimator != null && pullAnimator.isRunning())
            pullAnimator.cancel();
        cpbRefresh.setShowArrow(true);
        LayoutParams layoutParams = (LayoutParams) getRefreshView().getLayoutParams();
//        layoutParams.topMargin = -refreshViewHeight() + dur;
        int temp = layoutParams.topMargin+step;
        if(temp<-refreshViewHeight()){
            temp = -refreshViewHeight();
        }else if(temp > getMaxPullHeight()-refreshViewHeight()){
            temp = getMaxPullHeight()-refreshViewHeight();
        }
        layoutParams.topMargin = temp;
        getRefreshView().setLayoutParams(layoutParams);
        cpbRefresh.rate(dur * 1.0f / maxPullHeight);
    }

    @Override
    public void startRefreshUi(int pulltoRefreshHeight, final int maxPullHeight) {

        if (cpbRefresh.isRunning())
            cpbRefresh.stop();
        if (pullAnimator != null && pullAnimator.isRunning())
            pullAnimator.cancel();
        final LayoutParams layoutParams = (LayoutParams) getRefreshView().getLayoutParams();
        pullAnimator = ValueAnimator.ofInt(layoutParams.topMargin, getPullToRefreshHeight() - refreshViewHeight())
                .setDuration((long) (Math.abs(getPullToRefreshHeight() - refreshViewHeight() - layoutParams.topMargin) * 1.0f / (getMaxPullHeight()) * RES_PULL_ANIM_DUR));

        pullAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.topMargin = (int) animation.getAnimatedValue();
                getRefreshView().setLayoutParams(layoutParams);
                cpbRefresh.rate(layoutParams.topMargin * 1.0f / maxPullHeight);
            }
        });
        pullAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cpbRefresh.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pullAnimator.start();
    }

    @Override
    public void pullResUi() {
        if (cpbRefresh.isRunning())
            cpbRefresh.stop();
        if (pullAnimator != null && pullAnimator.isRunning())
            pullAnimator.cancel();

        final LayoutParams layoutParams = (LayoutParams) getRefreshView().getLayoutParams();
        pullAnimator = ValueAnimator.ofInt(layoutParams.topMargin, -refreshViewHeight())
                .setDuration((long) (Math.abs(layoutParams.topMargin + refreshViewHeight())*1.0f / (getMaxPullHeight()) * RES_PULL_ANIM_DUR));
        pullAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.topMargin = (int) animation.getAnimatedValue();
                getRefreshView().setLayoutParams(layoutParams);
                cpbRefresh.rate(layoutParams.topMargin * 1.0f / getMaxPullHeight());
            }
        });
        pullAnimator.start();
    }

    /*
    设置加载更多样式布局
     */
    @Override
    public View getloadingView(ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_mrefresh_rcv_loadmore, parent, false);
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
