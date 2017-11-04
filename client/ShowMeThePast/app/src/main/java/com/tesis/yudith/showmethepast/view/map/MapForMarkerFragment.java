package com.tesis.yudith.showmethepast.view.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapMarker;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapScenario;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;

import java.util.ArrayList;
import java.util.List;

public class MapForMarkerFragment extends Fragment
        implements
            OnMapReadyCallback,
            View.OnClickListener,
            INavigationChild,
            GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    public interface IPositionEditorListener {
        void onChange(MapForMarkerFragment target, List<SerializableMapMarker> markers);
    }

    private static final String ARG_SERIALIZABLE_MAP_SCENARIO = "ARG_SERIALIZABLE_MAP_SCENARIO";
    private static final String ARG_READ_ONLY_ID = "ARG_READ_ONLY_ID";
    private static final String ARG_HAS_CHANGED_ID = "ARG_HAS_CHANGED_ID";

    private final int REQUEST_PERMISSIONS_ID = 111;

    private String title;
    private View mainView;
    private GoogleMap googleMap;
    private MapView mapViewGoogleMap;
    private LinearLayout layoutForEditor;

    private Button btnPickPosition;

    boolean previewReadOnlyMode;
    boolean readOnlyMode;
    boolean hasChanged;

    LocationManager locationManager;

    IPositionEditorListener listener;
    SerializableMapScenario mapScenario;

    INavigationManager navigationManager;

    List<Marker> draggableMarkers;

    public void setListener(IPositionEditorListener listener) {
        this.listener = listener;
    }

    public MapForMarkerFragment() {

    }

    public static MapForMarkerFragment newInstance(SerializableMapScenario mapScenario, boolean readOnlyMode) {
        MapForMarkerFragment fragment = new MapForMarkerFragment();
        Bundle args = new Bundle();

        fragment.previewReadOnlyMode = readOnlyMode;

        args.putSerializable(ARG_SERIALIZABLE_MAP_SCENARIO, mapScenario);
        args.putBoolean(ARG_READ_ONLY_ID, readOnlyMode);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.mapScenario = (SerializableMapScenario)this.getArguments().getSerializable(ARG_SERIALIZABLE_MAP_SCENARIO);
            this.readOnlyMode = this.getArguments().getBoolean(ARG_READ_ONLY_ID);
            this.hasChanged = false;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mainView = inflater.inflate(R.layout.fragment_map_for_marker, container, false);
        return this.mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.navigationManager = (INavigationManager)context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.linkControls(view);
    }

    private void linkControls(View view) {
        this.mapViewGoogleMap = (MapView)view.findViewById(R.id.mapView_mapForMarker_mainMap);
        this.btnPickPosition = (Button)view.findViewById(R.id.btn_mapForMarker_pickPosition);
        this.layoutForEditor = (LinearLayout)view.findViewById(R.id.layout_mapForMarker_forEditor);

        this.btnPickPosition.setOnClickListener(this);

        if (this.mapViewGoogleMap != null) {
            this.mapViewGoogleMap.onCreate(null);
            this.mapViewGoogleMap.onResume();
            this.mapViewGoogleMap.getMapAsync(this);
        }

        if (this.readOnlyMode) {
            this.layoutForEditor.setVisibility(View.GONE);
        } else {
            this.layoutForEditor.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public LatLng getCenterPosition() {
        return new LatLng(this.mapScenario.getCenterLatitude(), this.mapScenario.getCenterLongitude());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity().getApplicationContext());
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraPosition cameraPosition = CameraPosition.builder().target(this.getCenterPosition())
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();

        this.createScenario();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapLongClickListener(this);

        this.tryEnableMyPosition();
    }

    private Marker addMarker(SerializableMapMarker targetMarker, boolean isDraggable) {
        LatLng position = new LatLng(targetMarker.getLatitude(), targetMarker.getLongitude());
        String title = targetMarker.getTitle();
        BitmapDescriptor icon = MarkersManager.getCurrent().get(targetMarker.getMarker());

        MarkerOptions markerOptions =
                new MarkerOptions()
                    .position(position)
                    .title(title)
                    .icon(icon)
                    .draggable(isDraggable && !this.readOnlyMode);

        Marker newMarker = this.googleMap.addMarker(markerOptions);
        newMarker.setTag(targetMarker);
        return newMarker;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_SERIALIZABLE_MAP_SCENARIO, this.mapScenario);
        outState.putBoolean(ARG_READ_ONLY_ID, this.readOnlyMode);
        outState.putBoolean(ARG_HAS_CHANGED_ID, this.hasChanged);
    }

    private void createScenario() {
        this.draggableMarkers = new ArrayList<>();
        for(SerializableMapMarker marker : this.mapScenario.getStaticMarkers()) {
            this.addMarker(marker, false);
        }

        for(SerializableMapMarker marker : this.mapScenario.getDraggableMarkers()) {
            this.draggableMarkers.add(this.addMarker(marker, true));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mapForMarker_pickPosition:
                if (this.hasChanged && this.listener != null) {
                    this.listener.onChange(this, this.mapScenario.getDraggableMarkers());
                    this.navigationManager.popFragment(true);
                } else {
                    this.navigationManager.popFragment(false);
                }

                break;
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }


    @Override
    public void onMapLongClick(final LatLng latLng) {
        if (this.readOnlyMode) {
            return;
        }

        if (this.mapScenario.getDraggableMarkers().size() == 1) {
            this.moveMarker(0, latLng);
        } else {
            String[] currentTitles = new String[this.draggableMarkers.size()];

            for (int i = 0; i < this.draggableMarkers.size(); i++){
                currentTitles[i] = this.draggableMarkers.get(i).getTitle();
            }

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.getActivity());
            builderSingle.setTitle(R.string.title_dialog_pick_marker);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_single_choice);
            arrayAdapter.addAll(currentTitles);

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                moveMarker(which, latLng);
                }
            });
            builderSingle.show();
        }
    }

    private void moveMarker(int idx, LatLng latLng) {
        SerializableMapMarker serializableMapMarker =  this.mapScenario.getDraggableMarkers().get(idx);
        if (serializableMapMarker != null) {
            serializableMapMarker.setLatitude(latLng.latitude);
            serializableMapMarker.setLongitude(latLng.longitude);
            this.draggableMarkers.get(idx).setPosition(latLng);
            this.hasChanged = true;
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (this.readOnlyMode) {
            return;
        }
        SerializableMapMarker serializableMarker = (SerializableMapMarker)marker.getTag();
        if (serializableMarker != null) {
            serializableMarker.setLatitude(marker.getPosition().latitude);
            serializableMarker.setLongitude(marker.getPosition().longitude);
            this.hasChanged = true;
        }
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        if (!this.previewReadOnlyMode) {
            return resources.getString(R.string.title_fragment_map_marker_position);
        }
        return resources.getString(R.string.title_fragment_map_marker_position_view);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }

    private void tryEnableMyPosition() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
        } else {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        this.googleMap.setMyLocationEnabled(true);
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }
}
