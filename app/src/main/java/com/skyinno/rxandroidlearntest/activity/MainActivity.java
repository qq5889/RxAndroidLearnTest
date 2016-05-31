package com.skyinno.rxandroidlearntest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.adapter.CommonAdapter;
import com.skyinno.rxandroidlearntest.bean.IntentBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.lv)
    ListView mLv;
    private List<IntentBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initList();
        mLv.setAdapter(new CommonAdapter<IntentBean>(this, mList, R.layout.item) {
            @Override
            public void convert(ViewHolder holder, IntentBean item) {
                holder.setText(R.id.tv, item.name);
            }
        });
        mLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, mList.get(position).clazz));
    }

    private void initList() {
        mList.add(new IntentBean("简单的rx使用", SimpleRxActivity.class));
        mList.add(new IntentBean("简单的变换使用", MapRxActivity.class));
    }
}
