package com.tesis.yudith.showmethepast.view;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.requests.CommonRequests;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

public class SynchronizationFragment extends Fragment implements View.OnClickListener, INavigationChild {

    Button btnStart;
    ProgressBar progressGeneral;
    ProgressBar progressModifications;
    ProgressBar progressDeletions;

    private static int[] arrayCollectionNames = {
            R.string.label_collection_touristic_locations,
            R.string.label_collection_old_pictures,
            R.string.label_collection_images
    };

    private static Class<?>[] arrayCollectionTypes = {
            TouristicPlace.class,
            OldPicture.class,
            ImageData.class
    };

    private final int STEP_CREATIONS = 0;
    private final int STEP_EDITIONS = 1;
    private final int STEP_DELETIONS = 2;

    private int getDataCollectionIndex;
    private int getDataStepIndex;

    public SynchronizationFragment() {

    }

    public static SynchronizationFragment newInstance() {
        SynchronizationFragment fragment = new SynchronizationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synchronization, container, false);
        this.linkControls(view);
        return view;
    }

    private void linkControls(View view) {
        this.btnStart = (Button)view.findViewById(R.id.btn_syncActivity_start);
        this.progressGeneral = (ProgressBar) view.findViewById(R.id.progress_synchronization_general);
        this.progressModifications = (ProgressBar) view.findViewById(R.id.progress_synchronization_modifications);
        this.progressDeletions = (ProgressBar) view.findViewById(R.id.progress_synchronization_deletions);

        this.btnStart.setOnClickListener(this);
    }

    public void onButtonPressed(Uri uri) {

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_syncActivity_start:
                this.startSynchronization();
        }
    }

    CommonRequests getCommonRequests() {
        return MyApp.getCurrent().getAppRequests().getCommonRequests();
    }


    private void startSynchronization() {
        this.disableControls();

        this.getDataCollectionIndex = 0;
        this.getDataStepIndex = 0;

        //this.getCommonRequests().processSyncRequest(LoginUserManager.getCurrent().getUserInformation(), CommonRequests.SynType.CREATIONS,  );
    }

    private void disableControls() {
        this.btnStart.setEnabled(false);
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_synchronization);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }
}
