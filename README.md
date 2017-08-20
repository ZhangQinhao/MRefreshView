# MRefreshView
　　一个具备上拉刷新下拉加载的RecyclerView。业务封装完善，上拉下拉时机已经集中控制不需要用户去做判断。
同时用户也能在继承  `MRefreshRecyclerBaseF`类可以实现自己独特UI的刷新控件。

### 引入

 - Android Studio

在build.gradle引入  `compile 'com.zhangmonke:MRefreshView:1.0.2'`

 - eclipse

建议使用As，方便版本更新。实在不行，只有复制粘贴源码了。


###类结构
`MRefreshRecyclerBaseFView`是`RecyclerView`的子类，封装了一些必要触摸操作，以及回调监听
`MRefreshRecyclerViewAdatper`是`Adapter`的子类，封装了刷新以及加载的控制条件。所有使用此类控件的Adapter都要继承这个类
`MRefreshRecyclerBaseF`是整体封装的基类，用户如果需要自定义上拉刷新，下拉加载的效果可以直接继承此类，完成抽象方法。
`MRefreshRecyclerLineView`/`MRefreshRecyclerMaterView`是两个已经继承`MRefreshRecyclerBaseF`的样式示例。
`OnLoadMoreListener`/`OnRefreshListener`分别是加载更多以及下拉刷新的监听器，通过`set`进`MRefreshRecycerBaseF`，不设置则不具备相应功能。

###MRefreshRecyclerMaterView

![enter description here][1]
####使用
``` stylus
<com.monke.mrefreshview.MRefreshRecyclerMaterView
        android:id="@+id/mrcv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:mrcv_color_refresh="#ffc0cb"
        app:mrcv_color_load_bg="#ffffff"
        android:background="#ffffff"/>
```

``` stylus
public class DemoAdapter extends MRefreshRecyclerViewAdapter{
    private List<String> datas;

    public DemoAdapter(){
        datas = new ArrayList<>();
    }
    @Override
    public int getItemcount() {
        return datas.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewholder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_demo_item,parent,false));
    }

    @Override
    public int getItemViewtype(int position) {
        return 0;
    }

    @Override
    public void onBindViewholder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).tvPo.setText(datas.get(position));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvPo;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tvPo = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    public void addNewData(List<String> newDatas){
        if(newDatas!=null && newDatas.size()>0){
            int oldCount = getItemcount();
            datas.addAll(newDatas);
            notifyItemRangeInserted(oldCount,newDatas.size());
        }
    }
    public void replaceAll(List<String> newDatas){
        datas.clear();
        if(newDatas!=null && newDatas.size()>0){
            datas.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}
```

``` stylus
mrcv = (MRefreshRecyclerMaterView) findViewById(R.id.mrcv);
        demoAdapter = new DemoAdapter();
        mrcv.setAdapter(demoAdapter);
        mrcv.setLayoutManager(new GridLayoutManager(this,2));
        mrcv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void startRefresh() {
                page = 1;
                refreshDatas();
            }
        });
        mrcv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                page++;
                updateDatas();
            }

            @Override
            public void loadTryAgain() {
                updateDatas();
            }
        });
        //初次载入
        mrcv.startRefresh();
```

``` stylus
<declare-styleable name="MRefreshRecyclerMaterView">
        <attr name="mrcv_color_load_text" />    <!--加载更多  字体颜色-->
        <attr name="mrcv_color_load_bg" />       <!--加载更多   背景颜色-->
        <attr name="mrcv_color_refresh" format="color" />      <!--下拉刷新  环形进度条颜色-->
    </declare-styleable>
```
###MRefreshRecyclerLineView

![enter description here][2]
####使用
``` stylus
<com.monke.mrefreshview.MRefreshRecyclerLineView
        android:id="@+id/mrcv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:mrcv_color_refresh_bg="#00000000"
        app:mrcv_color_refresh_progress="#aaaaaa"
        android:background="#ffffff"/>
```
其他一样使用
``` stylus
<declare-styleable name="MRefreshRecyclerLineView">
        <attr name="mrcv_color_refresh_bg" format="color" />    <!--下拉刷新  进度条背景颜色-->
        <attr name="mrcv_color_refresh_progress" format="color" />       <!--下拉刷新  进度条颜色-->
        <attr name="mrcv_color_load_text" />    <!--加载更多  字体颜色-->
        <attr name="mrcv_color_load_bg" />       <!--加载更多   背景颜色-->
    </declare-styleable>
```

###最后
如果用户需要自定义自己的上下拉刷新控件（保证下拉效果是不下滑item）  都可以继承`MRefreshRecyclerBaseF`来实现，可以参考`MRefreshRecyclerLineView`/`MRefreshRecyclerMaterView`,
下个版本会实现基于`ScrollView`的刷新，同时会完成下拉刷新头部与item同时滚动的效果。
此控件的上拉刷新，下拉加载的时机已经封装在Adapter中，用户只需要把经历放到业务逻辑上就可以。



[1]: ./images/gif_1.gif "1.gif"
[2]: ./images/gif_2.gif "2.gif"
