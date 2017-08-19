package com.monke.mrefreshview.base;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class MRefreshRecyclerBaseFView extends RecyclerView {
    private OnBaseRefreshListener baseRefreshListener;
    private OnBaseLoadListener baseLoadListener;

    public interface OnBaseLoadListener {

        public View getloadingView(ViewGroup parent);

        public void startLoadingUI(View v);

        public void errorLoadingUI(View v);

        public void startLoading();

        public void tryAgain();
    }

    public interface OnBaseRefreshListener {

        public int getMaxPullHeight();

        public int getPullToRefreshHeight();

        public void pullingUI(int step,int dur, float durRatio, int pulltoRefreshHeight, int maxPullHeight);

        public void startRefresh();

        public void startRefreshUi(int pulltoRefreshHeight, int maxPullHeight);

        public void pullResUi();

        public float getSlideCalcu(float dur);
    }

    public MRefreshRecyclerBaseFView(Context context) {
        super(context);
        init();
    }

    public MRefreshRecyclerBaseFView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MRefreshRecyclerBaseFView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (getAdapter() != null
                        &&((MRefreshRecyclerViewAdapter) getAdapter()).canLoadMore()
                        && ((MRefreshRecyclerViewAdapter) getAdapter()).getItemcount() > 0
                        && getAdapter().getItemCount() - 1 == ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            ((MRefreshRecyclerViewAdapter)getAdapter()).startLoadMore();
                        }
                    });
                }
            }
        });
    }

    public OnBaseRefreshListener getBaseRefreshListener() {
        return baseRefreshListener;
    }

    public void setBaseRefreshListener(OnBaseRefreshListener baseRefreshListener) {
        this.baseRefreshListener = baseRefreshListener;
    }

    public OnBaseLoadListener getBaseLoadListener() {
        return baseLoadListener;
    }

    public void setBaseLoadListener(OnBaseLoadListener baseLoadListener) {
        this.baseLoadListener = baseLoadListener;
        if (getAdapter() != null) {
            ((MRefreshRecyclerViewAdapter) getAdapter()).setBaseLoadListener(this.baseLoadListener);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (baseLoadListener != null)
            ((MRefreshRecyclerViewAdapter) adapter).setBaseLoadListener(this.baseLoadListener);
        super.setAdapter(adapter);
    }

    private float durTouchY = -1000000;
    private float durTouchDis = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (baseRefreshListener != null
                && (getAdapter()) != null
                && ((MRefreshRecyclerViewAdapter) getAdapter()).canRequest()
                &&((((MRefreshRecyclerViewAdapter) getAdapter()).getItemcount()==0)||(getLayoutManager() instanceof GridLayoutManager && 0 == ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition())||(getLayoutManager() instanceof LinearLayoutManager && 0 == ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition()))){
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    durTouchY = event.getY();
                    durTouchDis = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (durTouchY == -1000000) {
                        durTouchY = event.getY();
                        durTouchDis = 0;
                    }
                    float dY = event.getY() - durTouchY;  //>0下拉
                    if(durTouchDis==0 && dY<0)
                        return super.onTouchEvent(event);
                    if(dY!=0){
                        durTouchY = event.getY();
                        float step = baseRefreshListener.getSlideCalcu(dY);
                        durTouchDis += step;
                        if(durTouchDis<0){
                            durTouchDis = 0;
                        }else if(durTouchDis>baseRefreshListener.getMaxPullHeight()){
                            durTouchDis = baseRefreshListener.getMaxPullHeight();
                        }
                        if (baseRefreshListener != null
                                && (getAdapter()) != null
                                && ((MRefreshRecyclerViewAdapter) getAdapter()).canRequest()
                                &&((((MRefreshRecyclerViewAdapter) getAdapter()).getItemcount()==0)||(getLayoutManager() instanceof GridLayoutManager && 0 == ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition())||(getLayoutManager() instanceof LinearLayoutManager && 0 == ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition()))) {
                            float calcu = durTouchDis;

                            float ratio = calcu / baseRefreshListener.getPullToRefreshHeight();
                            if(ratio>1)
                                ratio = 1;

                            baseRefreshListener.pullingUI((int) step,((int) calcu), ratio, baseRefreshListener.getPullToRefreshHeight(), baseRefreshListener.getMaxPullHeight());
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (baseRefreshListener != null && (getAdapter()) != null && ((MRefreshRecyclerViewAdapter) getAdapter()).canRequest()) {
                        float calcu = durTouchDis;
                        if (calcu >= baseRefreshListener.getPullToRefreshHeight()) {
                            float ratio = calcu / baseRefreshListener.getPullToRefreshHeight();
                            if(ratio>1)
                                ratio = 1;
                            startRefresh(baseRefreshListener.getPullToRefreshHeight(),baseRefreshListener.getMaxPullHeight());
                        } else {
                            baseRefreshListener.pullResUi();
                        }
                    }
                    durTouchY = -1000000;
                    durTouchDis = 0;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void startRefresh(){
        startRefresh(baseRefreshListener.getPullToRefreshHeight(),baseRefreshListener.getMaxPullHeight());
    }
    public void startRefresh(int pullToHeight,int maxHeight){
        baseRefreshListener.startRefreshUi(pullToHeight,maxHeight);

        if(getAdapter()!=null){
            ((MRefreshRecyclerViewAdapter)getAdapter()).startRefreshUI();
        }
        baseRefreshListener.startRefresh();
    }
}
