package com.monke.mrefreshview.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public abstract class MRefreshRecyclerViewAdapter extends RecyclerView.Adapter {
    public static final int LOADMORETYPE = 2001;

    private int isRequesting = 0;   //0是未执行网络请求  1是正在下拉刷新  2是正在加载更多
    private Boolean isAll = false;  //判断是否还有更多
    private Boolean loadMoreError = false;

    private MRefreshRecyclerBaseFView.OnBaseLoadListener baseLoadListener;

    public MRefreshRecyclerBaseFView.OnBaseLoadListener getBaseLoadListener() {
        return baseLoadListener;
    }

    public void setBaseLoadListener(MRefreshRecyclerBaseFView.OnBaseLoadListener baseLoadListener) {
        this.baseLoadListener = baseLoadListener;
    }

    public int getIsRequesting() {
        return isRequesting;
    }

    public Boolean canRequest() {
        return isRequesting == 0;
    }

    public Boolean canLoadMore() {
        return baseLoadListener != null && canRequest() && !isAll && !loadMoreError;
    }

    public void startRefreshUI() {
        Boolean hasLoad = getItemCount() != getItemcount();

        isRequesting = 1;     //getItemCount = getItemcount;
        isAll = false;
        loadMoreError = false;

        if (baseLoadListener != null) {
            if (hasLoad) {
                notifyItemRemoved(getItemCount());
            }
        }
    }

    public void finishRefreshUI(Boolean isAll) {
        Boolean hasLoad = getItemCount() != getItemcount();

        this.isRequesting = 0;
        this.isAll = isAll;   //isAll==true ?getItemCount = getItemcount:getItemCount!=getItemcount;
        loadMoreError = false;

        if (baseLoadListener != null) {
            if (hasLoad) {
                if (!isAll && getItemcount() > 0) {
                    notifyItemChanged(getItemCount() - 1);
                } else {
                    notifyItemRemoved(getItemCount());
                }
            } else {
                if (!isAll && getItemcount() > 0) {
                    notifyItemInserted(getItemCount() - 1);
                }
            }
        }
    }

    public void startLoadMore() {
        Boolean hasLoad = getItemCount() != getItemcount();
        isRequesting = 2;
        isAll = false;
        loadMoreError = false;

        if (baseLoadListener != null) {
            if (hasLoad) {
                notifyItemChanged(getItemCount() - 1);
            } else {
                notifyItemInserted(getItemCount() - 1);
            }
            baseLoadListener.startLoading();
        }
    }

    public void tryAgain() {
        Boolean hasLoad = getItemCount() != getItemcount();
        isRequesting = 2;
        isAll = false;
        loadMoreError = false;

        if (baseLoadListener != null) {
            if (hasLoad) {
                notifyItemChanged(getItemCount() - 1);
            } else {
                notifyItemInserted(getItemCount() - 1);
            }
            baseLoadListener.tryAgain();
        }
    }

    public void finishLoadMoreUI(Boolean isAll) {
        Boolean hasLoad = getItemCount() != getItemcount();
        this.isRequesting = 0;
        this.isAll = isAll;
        loadMoreError = false;
        if (baseLoadListener != null) {
            if (hasLoad) {
                if (!isAll && getItemcount() > 0) {
                    notifyItemChanged(getItemCount());
                } else {
                    notifyItemRemoved(getItemCount());
                }
            } else {
                if (!isAll && getItemcount() > 0) {
                    notifyItemInserted(getItemCount() - 1);
                }
            }
        }
    }

    public void loadMoreErrorUI() {
        Boolean hasLoad = getItemCount() != getItemcount();

        this.isRequesting = 0;
        this.isAll = false;
        this.loadMoreError = true;   //getItemCount = getItemcount;

        if (baseLoadListener != null) {
            if (hasLoad) {
                notifyItemChanged(getItemCount() - 1);
            } else {
                notifyItemInserted(getItemCount() - 1);
            }
        }
    }

    /**
     * UI总数  根据需要添加加载更多UI
     * @return
     */
    @Override
    public int getItemCount() {
        if (baseLoadListener != null) {
            if (isRequesting == 2) {
                return getItemcount() + 1;
            } else if (isRequesting == 0) {
                if ((getItemcount() > 0 && !isAll) || loadMoreError) {
                    return getItemcount() + 1;
                }
            }
        }
        return getItemcount();
    }

    /**
     * 真正的数据源数量
     * @return
     */
    public abstract int getItemcount();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADMORETYPE) {
            return new LoadMoreViewholder(baseLoadListener.getloadingView(parent));
        } else
            return onCreateViewholder(parent, viewType);
    }

    public abstract RecyclerView.ViewHolder onCreateViewholder(ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {
        if (getItemcount() != getItemCount() && position == getItemCount() - 1) {
            return LOADMORETYPE;
        } else {
            return getItemViewtype(position);
        }
    }

    /**
     * 实现获取ViewHolder类型   2001不能用
     * @param position
     * @return
     */
    public abstract int getItemViewtype(int position);

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == LOADMORETYPE) {
            if (!loadMoreError) {
                baseLoadListener.startLoadingUI(((LoadMoreViewholder) holder).v);
            } else {
                baseLoadListener.errorLoadingUI(((LoadMoreViewholder) holder).v);
            }
            ((LoadMoreViewholder) holder).v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canRequest() && loadMoreError) {
                        tryAgain();
                    }
                }
            });
        } else
            onBindViewholder(holder, position);
    }

    /**
     * 实现ViewHolder 绑定数据
     * @param holder
     * @param position
     */
    public abstract void onBindViewholder(RecyclerView.ViewHolder holder, int position);


    /**
     * 加载更多ViewHolder
     */
    class LoadMoreViewholder extends RecyclerView.ViewHolder {
        View v;

        public LoadMoreViewholder(View itemView) {
            super(itemView);
            v = itemView;
        }
    }
}
