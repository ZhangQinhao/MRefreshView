package com.monke.mrefreshviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.monke.mrefreshview.MRefreshRecyclerMaterView;
import com.monke.mrefreshview.base.OnLoadMoreListener;
import com.monke.mrefreshview.base.OnRefreshListener;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewDemoActivity extends AppCompatActivity {
    private MRefreshRecyclerMaterView mrcv;
    private DemoAdapter demoAdapter;

    private int page = 1;
    private int MAX_PAGE = 5;   //虚拟数据  只加载5页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_demo);
        mrcv = (MRefreshRecyclerMaterView) findViewById(R.id.mrcv);
        demoAdapter = new DemoAdapter();
        mrcv.setAdapter(demoAdapter);
        mrcv.setLayoutManager(new LinearLayoutManager(this));
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
    }

    public void updateDatas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> newData = new ArrayList<>();
                if (page <= MAX_PAGE) {
                    for (int i = 0; i < 5; i++) {
                        newData.add("page=" + page + " item=" + i);
                    }
                }
                demoAdapter.addNewData(newData);
                mrcv.completeRequest(newData.size() == 0 ? true : false);
            }
        }, 1000);
    }

    public void refreshDatas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> newData = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    newData.add("page=" + page + " item=" + i);
                }
                demoAdapter.replaceAll(newData);
                mrcv.completeRequest(newData.size() == 0 ? true : false);
            }
        },1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_rv_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.linear:
                mrcv.setLayoutManager(new LinearLayoutManager(RecyclerViewDemoActivity.this));
                break;
            case R.id.grid:
                mrcv.setLayoutManager(new GridLayoutManager(RecyclerViewDemoActivity.this,2));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
