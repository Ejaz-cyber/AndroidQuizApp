package in.iejaz.orangequiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    public MyPagerAdapter(FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragLogin();
            case 1: return new FragSignup();

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
