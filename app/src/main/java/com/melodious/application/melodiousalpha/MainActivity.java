package com.melodious.application.melodiousalpha;

import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private MelodyAdapter melodyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        //swapping between layouts
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new NewFragment(),"");
        viewPagerAdapter.addFragment(new BestFragment(), "");
        viewPagerAdapter.addFragment(new ExclusiveFragment(), "");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.latest);
        tabLayout.getTabAt(1).setIcon(R.drawable.best);
        tabLayout.getTabAt(2).setIcon(R.drawable.exclusive);

        for(int i=0; i < tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if(tab != null){
                tab.setCustomView(R.layout.custom_tab_layout);
            }
        }
    }
}
