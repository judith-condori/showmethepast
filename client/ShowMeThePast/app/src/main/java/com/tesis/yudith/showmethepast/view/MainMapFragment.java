package com.tesis.yudith.showmethepast.view;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.domain.CommonEnumerators;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
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

public class MainMapFragment extends Fragment
        implements
            OnMapReadyCallback,
            GoogleMap.OnMarkerClickListener,
            View.OnClickListener,
            SearchView.OnQueryTextListener,
            INavigationChild,
            GoogleMap.OnInfoWindowLongClickListener {

    private GoogleMap googleMap;
    private MapView mapViewGoogleMap;
    private MarkerOptions markerOptions;
    private Map<Marker, String> linksTouristicPlaces;
    private Map<Marker, String> linksOldPictures;
    private BitmapDescriptor museumDescriptor;
    private BitmapDescriptor startOldPictureDescriptor;
    private BitmapDescriptor targetOldPictureDescriptor;

    private SearchView searchViewPlaces;

    private Marker selected;

    private final int REQUEST_PERMISSIONS_ID = 111;
    private static final String TARGET_TYPE = "TARGET_TYPE";
    private static final String TARGET_DOCUMENT_ID = "TARGET_DOCUMENT_ID";

    private Class<?> targetType;
    private String targetDocumentId;

    public MainMapFragment() {
        this.linksTouristicPlaces = new HashMap<>();
        this.linksOldPictures = new HashMap<>();
    }

    public static MainMapFragment newInstance() {
        MainMapFragment fragment = new MainMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MainMapFragment newInstanceForTouristicPlace(String touristicPlaceId) {
        MainMapFragment fragment = new MainMapFragment();
        Bundle args = new Bundle();
        args.putString(TARGET_TYPE, MongoCollection.getServerCollectionName(TouristicPlace.class));
        args.putString(TARGET_DOCUMENT_ID, touristicPlaceId);
        fragment.setArguments(args);
        return fragment;
    }

    public static MainMapFragment newInstanceForOldPicture(String oldPictureId) {
        MainMapFragment fragment = new MainMapFragment();
        Bundle args = new Bundle();
        args.putString(TARGET_TYPE, MongoCollection.getServerCollectionName(OldPicture.class));
        args.putString(TARGET_DOCUMENT_ID, oldPictureId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(MyApp.getCurrent().getApplicationContext());
        if (getArguments() != null) {
            this.targetDocumentId = getArguments().getString(TARGET_DOCUMENT_ID);
            if (MongoCollection.getServerCollectionName(TouristicPlace.class).equals(getArguments().getString(TARGET_TYPE))) {
                this.targetType = TouristicPlace.class;
            } else if (MongoCollection.getServerCollectionName(OldPicture.class).equals(getArguments().getString(TARGET_TYPE))) {
                this.targetType = OldPicture.class;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_map, container, false);
        this.linkControls(view);
        return view;
    }

    private void linkControls(View view) {
        this.mapViewGoogleMap = (MapView)view.findViewById(R.id.mapView_mainMap_map);
        this.searchViewPlaces = (SearchView)view.findViewById(R.id.search_mainMap_searchPlace);

        this.searchViewPlaces.setOnQueryTextListener(this);

        if (this.mapViewGoogleMap != null) {
            this.mapViewGoogleMap.onCreate(null);
            this.mapViewGoogleMap.onResume();
            this.mapViewGoogleMap.getMapAsync(this);
        }
    }

    LatLng getCenterPosition() {
        // Coordinates of "Casa Nacional de Moneda Potosi"
        return new LatLng(-19.588528, -65.754171);
        //return new LatLng(this.currentLatitude, this.currentLongitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getContext());

        this.museumDescriptor = MarkersManager.getCurrent().get(MarkersManager.EMarker.MARKER_TOURISTIC_PLACE);
        this.startOldPictureDescriptor = MarkersManager.getCurrent().get(MarkersManager.EMarker.MARKER_PICK_START);

        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.centerMap(googleMap, this.getCenterPosition());

        googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnInfoWindowLongClickListener(this);

        this.tryEnableMyPosition();

        this.filterTouristicPlaces("");
    }

    private void centerMap(GoogleMap googleMap, LatLng centerPosition) {
        CameraPosition cameraPosition = CameraPosition.builder().target(centerPosition)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    private void filterTouristicPlaces(String hint) {
        List<TouristicPlace> touristicPlaces = MyApp.getCurrent().getAppControllers().getTouristicPlacesController().filterTouristicPlaces(hint);

        this.processTouristicPlaceMarkers(touristicPlaces);
    }

    private void processTouristicPlaceMarkers(List<TouristicPlace> places) {
        Marker newMarker;
        String title;
        LatLng position;

        this.googleMap.clear();
        this.linksTouristicPlaces.clear();
        this.linksOldPictures.clear();

        for(TouristicPlace touristicPlace : places) {

            title = LanguageManager.translate(touristicPlace.getName());
            position = new LatLng(touristicPlace.getPosition().getLatitude(), touristicPlace.getPosition().getLongitude());

            this.markerOptions = new MarkerOptions()
                    .position(position)
                    .title(title)
                    .icon(this.museumDescriptor);

            newMarker = googleMap.addMarker(markerOptions);

            this.linksTouristicPlaces.put(newMarker, touristicPlace.getId());
            this.processOldPictureMarkers(touristicPlace);

            if (this.targetType == TouristicPlace.class && touristicPlace.getId().equals(targetDocumentId)) {
                this.centerMap(googleMap, touristicPlace.getPosition().toLatLng());
            }
        }
    }

    private void processOldPictureMarkers(TouristicPlace parentTouristicPlace) {
        Marker newMarker;
        String title;
        LatLng position;

        List<OldPicture> oldPictures = MyApp.getCurrent().getAppDaos().getCommonsDao().find(ObjectFilters.eq("touristicPlace", parentTouristicPlace.getId()), OldPicture.class).toList();

        for(OldPicture oldPicture : oldPictures) {

            title = LanguageManager.translate(oldPicture.getName());

            position = oldPicture.getPosition().getStartPosition().toLatLng();

            this.markerOptions = new MarkerOptions()
                    .position(position)
                    .title(title)
                    .icon(this.startOldPictureDescriptor);

            newMarker = googleMap.addMarker(markerOptions);

            this.linksOldPictures.put(newMarker, oldPicture.getId());

            if (this.targetType == OldPicture.class && oldPicture.getId().equals(targetDocumentId)) {
                this.centerMap(googleMap, oldPicture.getPosition().getStartPosition().toLatLng());
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //String targetId = this.links.get(marker);
        //Toast.makeText(this.getContext(), targetId, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        //this.googleMap.setOnInfoWindowLongClickListener(this);
        //Fragment targetFragment = TouristicPlaceEditionFragment.newInstanceForViewUpdate(this.currentTouristicPlaces.get(position).getId());
        //this.loadFragment(targetFragment);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String hint) {
        this.filterTouristicPlaces(hint);
        return false;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        String targetId;
        TouristicPlaceEditionFragment touristicPlaceEditionFragment;
        OldPictureEditionFragment oldPictureEditionFragment;

        if (this.linksOldPictures.containsKey(marker)) {
            targetId = this.linksOldPictures.get(marker);
            OldPicture targetOldPicture = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(targetId, OldPicture.class);
            oldPictureEditionFragment = OldPictureEditionFragment.newInstanceForUpdate(targetOldPicture.getTouristicPlace(), targetId);
            this.loadFragment(oldPictureEditionFragment);

        } else if (this.linksTouristicPlaces.containsKey(marker)) {
            targetId = this.linksTouristicPlaces.get(marker);
            touristicPlaceEditionFragment = TouristicPlaceEditionFragment.newInstanceForViewUpdate(targetId);
            this.loadFragment(touristicPlaceEditionFragment);
        }
    }

    void loadFragment(INavigationChild fragment) {
        ((INavigationManager)this.getContext()).pushFragment(fragment);
    }

    void updateSearch() {
        this.filterTouristicPlaces(this.searchViewPlaces.getQuery().toString());
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_main_map);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {
        this.updateSearch();
    }
}
