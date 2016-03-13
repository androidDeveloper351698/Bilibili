package com.kaede.bilibilikaede.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kaede.bilibilikaede.Adapter.HomeFragmentAdapter;
import com.kaede.bilibilikaede.R;
import com.kaede.bilibilikaede.RxBus.RxBus;
import com.kaede.bilibilikaede.Utils.SizeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {
    @InjectView(R.id.nav_view) NavigationView nav;
    @InjectView(R.id.toolbar_home) Toolbar toolbar;
    @InjectView(R.id.home_menu) ImageView menu;
    @InjectView(R.id.user_avatar_toolbar) CircleImageView avater;
    @InjectView(R.id.user_nickname_toolbar) TextView nickname;
    @InjectView(R.id.tab_home) TabLayout tabLayout;
    @InjectView(R.id.viewpager) ViewPager viewPager;

    private CircleImageView iv_avatar;
    private PopupWindow popupWindow;
    private RxBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        View headerView = nav.getHeaderView(0);
        iv_avatar = (CircleImageView)headerView.findViewById(R.id.user_avatar);
        viewPager.setAdapter(new HomeFragmentAdapter(getBaseContext(),getSupportFragmentManager()));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(2);
        initListener();

        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_search_popwindow, null, false);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        initPopListener(popupWindow,contentView);
        popupWindow.setAnimationStyle(R.style.pop_anim_search);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

    }

    public RxBus getRxBusSingleton(){
        if(bus==null){
            synchronized (this){
                if(bus == null){
                    bus = new RxBus();
                }
            }
        }
        return bus;
    }

    private void initListener(){
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_menu_search:
                if(!popupWindow.isShowing()) {
                    popupWindow.showAtLocation(toolbar, Gravity.TOP, 0, SizeUtils.dp2px(getBaseContext(), 20));
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPopListener(final PopupWindow popupWindow, View contentView){
        ImageView back = (ImageView)contentView.findViewById(R.id.pop_back);
        ImageView clear = (ImageView)contentView.findViewById(R.id.pop_clear);
        final EditText input = (EditText)contentView.findViewById(R.id.pop_input);
        ImageView scan = (ImageView)contentView.findViewById(R.id.pop_scan);
        ImageView search = (ImageView)contentView.findViewById(R.id.pop_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContent = input.getText().toString().trim();
                Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
                intent.putExtra("keyword",inputContent);
                startActivity(intent);
                popupWindow.dismiss();
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"点击成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
