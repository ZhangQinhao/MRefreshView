package com.monke.mrefreshview.base;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * FrameLayout 包含MRefreshRecyclerBaseFView+headView
 * 如果需要定制类似的上下拉刷新继承此类  完成抽象方法就可以
 */
public abstract class MRefreshRecyclerBaseF extends FrameLayout {
    private MRefreshRecyclerBaseFView mRefreshRecyclerBaseFView;
    private View refreshView;

    public MRefreshRecyclerBaseF(@NonNull Context context) {
        super(context);
        init();
    }

    public MRefreshRecyclerBaseF(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MRefreshRecyclerBaseF(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MRefreshRecyclerBaseF(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 界面UI初始化  FrameLayout包含一个MRefreshRecyclerBaseFView+refreshViwe;
     */
    private void init() {
        mRefreshRecyclerBaseFView = new MRefreshRecyclerBaseFView(getContext());
        addView(mRefreshRecyclerBaseFView);

        refreshView = getRefreshUI();
        addView(refreshView);
    }

    /**
     *
     * @return 自定义刷新UI
     */
    protected abstract View getRefreshUI();

    /**
     *
     * @return 获取添加的自定义刷新UI
     */
    public View getRefreshView() {
        return refreshView;
    }

    /**
     * 封装setAdapter
     * 对GridLayoutManager进行修改
     * 刷新控件暂时不支持除了LinearLayoutManager/GridLayoutManager
     * @param mRefreshRecyclerViewAdapter
     */
    public void setAdapter(MRefreshRecyclerViewAdapter mRefreshRecyclerViewAdapter) {
        mRefreshRecyclerBaseFView.setAdapter(mRefreshRecyclerViewAdapter);
        if (mRefreshRecyclerBaseFView.getLayoutManager() != null && mRefreshRecyclerBaseFView.getLayoutManager() instanceof GridLayoutManager) {
            setLayoutManager(mRefreshRecyclerBaseFView.getLayoutManager());
        }
    }

    /**
     * 封装setLayoutManager
     * 对GridLayoutManager进行修改
     * 刷新控件暂时不支持除了LinearLayoutManager/GridLayoutManager
     * @param layoutManager
     */
    public void setLayoutManager(final RecyclerView.LayoutManager layoutManager) {
        if (mRefreshRecyclerBaseFView.getAdapter() != null && layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup oldSpanSizeLookup = ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mRefreshRecyclerBaseFView.getAdapter().getItemViewType(position) == MRefreshRecyclerViewAdapter.LOADMORETYPE) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    } else
                        return oldSpanSizeLookup.getSpanSize(position);
                }
            };
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(spanSizeLookup);
        }
        mRefreshRecyclerBaseFView.setLayoutManager(layoutManager);
    }

    /**
     * 刷新事件监听器   null则关闭下拉刷新
     * @param refreshListener
     */
    public void setOnRefreshListener(final OnRefreshListener refreshListener) {
        if (refreshListener == null)
            mRefreshRecyclerBaseFView.setBaseRefreshListener(null);
        else
            mRefreshRecyclerBaseFView.setBaseRefreshListener(new MRefreshRecyclerBaseFView.OnBaseRefreshListener() {
                @Override
                public int getMaxPullHeight() {
                    return MRefreshRecyclerBaseF.this.getMaxPullHeight();
                }

                @Override
                public int getPullToRefreshHeight() {
                    return MRefreshRecyclerBaseF.this.getPullToRefreshHeight();
                }

                @Override
                public void pullingUI(int step,int dur, float durRatio, int pulltoRefreshHeight, int maxPullHeight) {
                    MRefreshRecyclerBaseF.this.pullingUI(step,dur, durRatio, pulltoRefreshHeight, maxPullHeight);
                }

                @Override
                public void startRefresh() {
                    refreshListener.startRefresh();
                }

                @Override
                public void startRefreshUi(int pulltoRefreshHeight, int maxPullHeight) {
                    MRefreshRecyclerBaseF.this.startRefreshUi(pulltoRefreshHeight, maxPullHeight);
                }

                @Override
                public void pullResUi() {
                    MRefreshRecyclerBaseF.this.pullResUi();
                }

                @Override
                public float getSlideCalcu(float dur) {
                    return MRefreshRecyclerBaseF.this.getSlideCalcu(dur);
                }
            });
    }

    /**
     * 重写此方法可以计算滑动与下拉刷新UI的比例问题  默认1：1
     *
     * @param res
     * @return
     */
    public float getSlideCalcu(float res) {
        return res;
    }

    public abstract int getMaxPullHeight();

    public abstract int getPullToRefreshHeight();

    public abstract void pullingUI(int step,int dur, float durRatio, int pulltoRefreshHeight, int maxPullHeight);

    public abstract void startRefreshUi(int pulltoRefreshHeight, int maxPullHeight);

    public abstract void pullResUi();

    /**
     * 加载更多事件监听器   null则关闭加载更多
     * @param loadMoreListener
     */
    public void setOnLoadMoreListener(final OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener == null) {
            mRefreshRecyclerBaseFView.setBaseLoadListener(null);
        } else {
            mRefreshRecyclerBaseFView.setBaseLoadListener(new MRefreshRecyclerBaseFView.OnBaseLoadListener() {
                @Override
                public View getloadingView(ViewGroup parent) {
                    return MRefreshRecyclerBaseF.this.getloadingView(parent);
                }

                @Override
                public void startLoadingUI(View v) {
                    MRefreshRecyclerBaseF.this.startLoadingUI(v);
                }

                @Override
                public void errorLoadingUI(View v) {
                    MRefreshRecyclerBaseF.this.errorLoadingUI(v);
                }

                @Override
                public void startLoading() {
                    loadMoreListener.loadMore();
                }

                @Override
                public void tryAgain() {
                    loadMoreListener.loadTryAgain();
                }
            });
        }
    }

    /**
     * 加载更多  UI
     * @param parent
     * @return
     */
    public abstract View getloadingView(ViewGroup parent);

    /**
     * 正在加载   UI效果调整
     * @param v
     */
    public abstract void startLoadingUI(View v);

    /**
     * 加载更多失败   UI效果调整
     * @param v
     */
    public abstract void errorLoadingUI(View v);

    /**
     * 加载或者刷新完成
     * @param isAll   是否还有更多
     */
    public void completeRequest(Boolean isAll) {
        if (mRefreshRecyclerBaseFView.getAdapter() != null) {
            if (((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).getIsRequesting() == 1) {
                ((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).finishRefreshUI(isAll);
                pullResUi();
            } else if (((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).getIsRequesting() == 2) {
                ((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).finishLoadMoreUI(isAll);
            }
        }
    }

    /**
     * 加载或者刷新失败
     */
    public void errorRequest() {
        if (mRefreshRecyclerBaseFView.getAdapter() != null) {
            if(((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).getIsRequesting()==1){
                pullResUi();
            }
            ((MRefreshRecyclerViewAdapter) mRefreshRecyclerBaseFView.getAdapter()).loadMoreErrorUI();
        }
    }

    public MRefreshRecyclerBaseFView getmRefreshRecyclerBaseFView() {
        return mRefreshRecyclerBaseFView;
    }

    /**
     * 强制自动下拉刷新
     */
    public void startRefresh(){
        mRefreshRecyclerBaseFView.startRefresh();
    }
}
