package com.monke.mrefreshviewdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.monke.mrefreshview.base.MRefreshRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

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
