package com.kaede.bilibilikaede.UI;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.kaede.bilibilikaede.Fragment.Search.BangumiFragment;
import com.kaede.bilibilikaede.Fragment.Search.GeneralFragment;
import com.kaede.bilibilikaede.Fragment.Search.SpecialFragment;
import com.kaede.bilibilikaede.Fragment.Search.UploaderFragment;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.RxBus;
import com.kaede.bilibilikaede.RxBus.SearchEvent.PassSearchKeywordEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends AppCompatActivity {
    @InjectView(R.id.tab_search) TabLayout tabs;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.search_text) EditText searchText;
    @InjectView(R.id.search_search) ImageView search;
    @InjectView(R.id.search_back) ImageView back;

    private RxBus rxBus;

    String[] titles = {"综合","番剧","专题","UP主"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchText.getText().toString().trim();
                if(!TextUtils.isEmpty(text)) {
                    rxBus.send(new PassSearchKeywordEvent(text));
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public RxBus getRxBusSingleton(){
        if(rxBus == null){
            synchronized (this) {
                if(rxBus == null) {
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }

    class MyPagerAdapter extends FragmentPagerAdapter{
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new GeneralFragment();
            }else if(position == 1){
                return new BangumiFragment();
            }else if(position == 2){
                return new SpecialFragment();
            }else if(position == 3){
                return new UploaderFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
