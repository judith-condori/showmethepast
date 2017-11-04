package com.tesis.yudith.showmethepast.view;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.aservices.AlertsByPositionService;
import com.tesis.yudith.showmethepast.configuration.AppConfiguration;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

import org.w3c.dom.Text;

import java.util.Date;

public class SettingsFragment extends Fragment implements INavigationChild, CompoundButton.OnCheckedChangeListener {

    private final int REQUEST_PERMISSIONS_ID = 111;

    EditText txtAddressServer;
    ToggleButton toggleUseGyroscope;
    ToggleButton toggleAlertsByPosition;
    EditText txtMinimumDistance;
    TableRow tableRowServerSettings;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    AppConfiguration getConfiguration() {
        return MyApp.getCurrent().getConfiguration();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.txtAddressServer = (EditText) view.findViewById(R.id.txt_settings_addressServer);
        this.toggleUseGyroscope = (ToggleButton) view.findViewById(R.id.toggle_settings_userGyroscope);
        this.toggleAlertsByPosition = (ToggleButton) view.findViewById(R.id.toggle_settings_enableAlerts);
        this.txtMinimumDistance = (EditText) view.findViewById(R.id.txt_settings_minimumDistance);
        this.tableRowServerSettings = (TableRow) view.findViewById(R.id.tableRow_settings_serverAddress);

        if (MyApp.getCurrent().isReleaseMode()) {
            this.tableRowServerSettings.setVisibility(View.GONE);
        }

        this.loadValues();

        this.txtAddressServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newValue = txtAddressServer.getText().toString();
                getConfiguration().writeServerAddress(newValue);
            }
        });

        this.txtMinimumDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newValue = txtMinimumDistance.getText().toString();
                if (newValue.equals("")) {
                    newValue = "0";
                }
                getConfiguration().writeAlertsMinimumRadius(Integer.parseInt(newValue));
            }
        });

        this.toggleUseGyroscope.setOnCheckedChangeListener(this);
        this.toggleAlertsByPosition.setOnCheckedChangeListener(this);

        return view;
    }

    private void loadValues() {
        this.txtAddressServer.setText(this.getConfiguration().readServerAddress());
        this.toggleUseGyroscope.setChecked(this.getConfiguration().readUseGyroscope());
        this.toggleAlertsByPosition.setChecked(this.getConfiguration().readEnableAlertsByPosition());

        this.txtMinimumDistance.setText(this.getConfiguration().readAlertsMinimunRadius() + "");
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_settings);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean result = false;
        switch (requestCode) {
            case REQUEST_PERMISSIONS_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        result = true;
                    }
                }
            }
        }
        this.saveAlertsByPosition(result);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_settings_userGyroscope:
                getConfiguration().writeUseGyroscope(isChecked);
                break;
            case R.id.toggle_settings_enableAlerts:
                if (!isChecked) {
                    this.saveAlertsByPosition(false);
                } else {
                    if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ) {
                        this.saveAlertsByPosition(true);
                    } else {
                        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_ID);
                    }
                }
                break;
        }
    }

    public void saveAlertsByPosition(boolean checked) {
        if (checked) {
            AlertsByPositionService.startService();
        } else {
            AlertsByPositionService.stopService();
        }
        this.getConfiguration().writeEnableAlertsByPosition(checked);
        this.toggleAlertsByPosition.setChecked(checked);
    }
}
