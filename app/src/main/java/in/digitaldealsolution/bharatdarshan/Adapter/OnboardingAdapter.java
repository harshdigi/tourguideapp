package in.digitaldealsolution.bharatdarshan.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class OnboardingAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public OnboardingAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragmentArrayList.get(position);
    }

    public void addFragment(Fragment fragment) {
        fragmentArrayList.add(fragment);
    }


    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }
}
