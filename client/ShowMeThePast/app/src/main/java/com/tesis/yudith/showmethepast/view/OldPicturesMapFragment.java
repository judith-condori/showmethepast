package com.tesis.yudith.showmethepast.view;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.childs.GPSPosition;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;

import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldPicturesMapFragment extends Fragment
        implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        INavigationChild,
        View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String ARG_TOURISTIC_PLACE_ID = "ARG_TOURISTIC_PLACE_ID";
    private final int REQUEST_PERMISSIONS_ID = 111;

    private GoogleMap googleMap;
    private String touristicPlaceId;
    private TouristicPlace currentTouristicPlace;
    private List<OldPicture> oldPictureList;

    private FloatingActionButton btnNewOldPicture;
    private MapView mapViewOldPictures;
    private ListView listViewOldPictures;

    Map<Marker, OldPicture> mapMarkers;

    Marker floatingTargetMarker;
    Polyline floatingLine;

    TabHost tabHostMain;

    public OldPicturesMapFragment() {
        this.mapMarkers = new HashMap<>();
    }

    public static OldPicturesMapFragment newInstance(String touristicPlaceId) {
        OldPicturesMapFragment fragment = new OldPicturesMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOURISTIC_PLACE_ID, touristicPlaceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.touristicPlaceId = getArguments().getString(ARG_TOURISTIC_PLACE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_old_pictures_map, container, false);
        this.loadTouristicPlace();
        this.linkControls(view);
        this.applyRoles();

        this.updateMainListView();

        return view;
    }

    private void updateMainListView() {
        ArrayList<String> listOldPictures = new ArrayList<>();
        for(OldPicture item : this.oldPictureList) {
            listOldPictures.add(LanguageManager.translate(item.getName()));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, listOldPictures);
        this.listViewOldPictures.setAdapter(arrayAdapter);
    }

    private void applyRoles() {
        if (!LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
            this.btnNewOldPicture.setVisibility(View.INVISIBLE);
        }
    }

    private void loadTouristicPlace() {
        this.currentTouristicPlace = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(this.touristicPlaceId, TouristicPlace.class);
        ObjectFilter filter = ObjectFilters.eq("touristicPlace", this.touristicPlaceId);
        this.oldPictureList = MyApp.getCurrent().getAppDaos().getCommonsDao().find(filter, OldPicture.class).toList();
    }

    private void linkControls(View view) {
        this.btnNewOldPicture = (FloatingActionButton)view.findViewById(R.id.btn_oldPictures_addNew);
        this.mapViewOldPictures = (MapView)view.findViewById(R.id.mapView_oldPictures_main);
        this.btnNewOldPicture.setOnClickListener(this);
        this.listViewOldPictures = (ListView)view.findViewById(R.id.listView_oldPictures_main);
        this.listViewOldPictures.setOnItemClickListener(this);

        this.tabHostMain = (TabHost)view.findViewById(R.id.tabHost_oldPictures);
        this.tabHostMain.setup();

        TabHost.TabSpec tabMap = tabHostMain.newTabSpec("TabMap");
        tabMap.setIndicator(this.getString(R.string.tab_title_map));
        tabMap.setContent(R.id.tab_oldPictures_map);
        this.tabHostMain.addTab(tabMap);

        TabHost.TabSpec tabList = tabHostMain.newTabSpec("TabList");
        tabList.setIndicator(this.getString(R.string.tab_title_list));
        tabList.setContent(R.id.tab_oldPictures_list);
        this.tabHostMain.addTab(tabList);

        if (this.mapViewOldPictures != null) {
            this.mapViewOldPictures.onCreate(null);
            this.mapViewOldPictures.onResume();
            this.mapViewOldPictures.getMapAsync(this);
        }
    }

    private void addTouristicPlaceMarker() {
        String title;
        LatLng position;
        BitmapDescriptor bitmapDescriptor = MarkersManager.getCurrent().get(MarkersManager.EMarker.MARKER_TOURISTIC_PLACE);

        title = LanguageManager.translate(this.currentTouristicPlace.getName());
        position = getTouristicPlacePosition();

        MarkerOptions markerOptions = new MarkerOptions()
                                            .position(position)
                                            .title(title)
                                            .icon(bitmapDescriptor);

        googleMap.addMarker(markerOptions);
    }

    private void addOldPictureMarkers(List<OldPicture> targetOldPictureList) {
        this.mapMarkers.clear();
        for(OldPicture oldPicture : targetOldPictureList) {
            this.addOldPictureMarker(oldPicture);
        }
    }

    private void addOldPictureMarker(OldPicture oldPicture) {
        String title;
        LatLng position;
        BitmapDescriptor bitmapDescriptor = MarkersManager.getCurrent().get(MarkersManager.EMarker.MARKER_PICK_START);

        title = LanguageManager.translate(oldPicture.getName());
        position = oldPicture.getPosition().getStartPosition().toLatLng();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(bitmapDescriptor);

        Marker marker = googleMap.addMarker(markerOptions);
        this.mapMarkers.put(marker, oldPicture);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getContext());
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraPosition cameraPosition = CameraPosition.builder().target(this.getTouristicPlacePosition())
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnInfoWindowLongClickListener(this);
        this.tryEnableMyPosition();

        this.resetChildrens();
    }

    private void resetChildrens() {
        this.googleMap.clear();
        this.addTouristicPlaceMarker();
        this.addOldPictureMarkers(this.oldPictureList);
    }

    public LatLng getTouristicPlacePosition() {
        GPSPosition position = this.currentTouristicPlace.getPosition();
        return new LatLng(position.getLatitude(), position.getLongitude());
    }

    void reCreateFloatingTargetMarker(OldPicture targetOldPicture) {
        String title = "";
        LatLng positionStart = targetOldPicture.getPosition().getStartPosition().toLatLng();
        LatLng positionTarget = targetOldPicture.getPosition().getTargetPosition().toLatLng();

        if (this.floatingTargetMarker != null) {
            this.floatingTargetMarker.remove();
        }

        if (this.floatingLine != null) {
            this.floatingLine.remove();
        }

        BitmapDescriptor bitmapDescriptor = MarkersManager.getCurrent().get(MarkersManager.EMarker.MARKER_PICK_TARGET);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(positionTarget)
                .title(title)
                .icon(bitmapDescriptor);

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(positionStart, positionTarget)
                .width(5)
                .color(Color.BLUE);

        this.floatingTargetMarker = this.googleMap.addMarker(markerOptions);
        this.floatingLine = this.googleMap.addPolyline(polylineOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        OldPicture targetOldPicture;
        if (this.mapMarkers.containsKey(marker)) {
            targetOldPicture = this.mapMarkers.get(marker);
            this.reCreateFloatingTargetMarker(targetOldPicture);
        }
        return false;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        OldPicture targetOldPicture;
        if (this.mapMarkers.containsKey(marker)) {
            targetOldPicture = this.mapMarkers.get(marker);
            this.loadFragment(OldPictureEditionFragment.newInstanceForUpdate(this.touristicPlaceId, targetOldPicture.getId()));
        }
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
                return;
            }
        }
    }

    void loadFragment(INavigationChild fragment) {
        ((INavigationManager)this.getContext()).pushFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_oldPictures_addNew:
                this.loadFragment(OldPictureEditionFragment.newInstanceForCreate(this.touristicPlaceId));
                break;
        }
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_old_pictures_map);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {
        this.loadTouristicPlace();
        this.updateMainListView();
        this.resetChildrens();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OldPicture oldPicture = this.oldPictureList.get(position);
        OldPictureEditionFragment oldPictureEditionFragment = OldPictureEditionFragment.newInstanceForUpdate(this.touristicPlaceId, oldPicture.getId());
        this.loadFragment(oldPictureEditionFragment);
    }
}
