package com.project.hawfarmbusiness;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMainFragment extends Fragment {

    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.activity_home_main, container, false);

        Bundle bundle = new Bundle();
        bundle.putString("status", "pending");
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.current_order_frame, orderFragment).commit();

        return mainView;
    }

}
