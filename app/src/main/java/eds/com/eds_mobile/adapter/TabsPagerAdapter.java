package eds.com.eds_mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import eds.com.eds_mobile.fragment.CreateReportFragment;
import eds.com.eds_mobile.fragment.ReportListFragment;

/**
 * Created by ulanowicz on 2/22/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm){
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return CreateReportFragment.newInstance();
        }
        else{
            return ReportListFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
        if (position == 0){
            return "Articles";
        }
        else {
            return "Library";
        }
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }
}

