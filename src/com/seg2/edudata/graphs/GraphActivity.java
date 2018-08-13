package com.seg2.edudata.graphs;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.seg2.edudata.R;

/**
 * Graph activity handling all graph interaction.
 */
public class GraphActivity extends FragmentActivity implements ActionBar.TabListener {
    /**
     * static boolean set by the other activities to force a reload of the graph data
     * if new countries or topics have been chosen.
     */
    public static boolean forceGraphReload = false;

    private ActionBar actionbar;
    private ViewPager viewpager;
    private FragmentPagerAdapter ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        viewpager = (ViewPager) findViewById(R.id.pager);
        ft = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int arg0) {
                switch (arg0) {
                    case 0:
                        return new ColumnGraph();
                    case 1:
                        return new LineGraph();
                    case 2:
                        return new PieGraph();
                    case 3:
                        return new TableGraph();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        actionbar = getActionBar();
        viewpager.setAdapter(ft);
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionbar.addTab(actionbar.newTab().setText("Column").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Graph").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Pie").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Table").setTabListener(this));
        actionbar.getTabAt(0).setIcon(R.drawable.column_icon);
        actionbar.getTabAt(1).setIcon(R.drawable.graph_icon);
        actionbar.getTabAt(2).setIcon(R.drawable.pie_icon);
        actionbar.getTabAt(3).setIcon(R.drawable.table_icon);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                actionbar.setSelectedNavigationItem(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

}  