package com.tesis.yudith.showmethepast.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

public class AboutFragment extends Fragment implements INavigationChild, View.OnClickListener {

    public final static String FRAGMENT_TAG_ABOUT = "FRAGMENT_TAG_ABOUT";

    Button btnPrivacyPolicies;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        this.linkControls(view);
        return view;
    }

    private void linkControls(View view) {
        this.btnPrivacyPolicies = (Button)view.findViewById(R.id.btn_about_privacyPolicies);
        this.btnPrivacyPolicies.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_about);
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG_ABOUT;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_about_privacyPolicies:
                this.openPrivacyPoliciesLink();
                break;
        }
    }

    private void openPrivacyPoliciesLink() {
        String url = this.getString(R.string.configuration_privacy_policies_url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        this.startActivity(intent);
    }
}
