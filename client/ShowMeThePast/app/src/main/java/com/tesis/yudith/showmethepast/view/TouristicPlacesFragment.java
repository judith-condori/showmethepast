package com.tesis.yudith.showmethepast.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.configuration.NitriteManager;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;

import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.ArrayList;
import java.util.List;

public class TouristicPlacesFragment extends Fragment
        implements
            View.OnClickListener,
            TouristicPlaceEditionFragment.OnTouristicPlaceEditionEvents,
            AdapterView.OnItemClickListener,
            INavigationChild,
            SearchView.OnQueryTextListener {

    public final static String FRAGMENT_TAG_TOURISTIC_PLACES = "FRAGMENT_TAG_TOURISTIC_PLACES";

    FloatingActionButton btnNewTouristicPlace;
    ListView listViewResults;
    SearchView searchView;

    ArrayAdapter<String> arrayAdapter;
    List<String> currentItems;
    List<TouristicPlace> currentTouristicPlaces;

    public TouristicPlacesFragment() {
        this.currentItems = new ArrayList<>();
        this.currentTouristicPlaces = new ArrayList<>();
    }

    public static TouristicPlacesFragment newInstance() {
        TouristicPlacesFragment fragment = new TouristicPlacesFragment();
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

        View view = inflater.inflate(R.layout.fragment_touristic_places, container, false);


        this.btnNewTouristicPlace = (FloatingActionButton) view.findViewById(R.id.btn_touristicPlaces_addNew);
        this.listViewResults = (ListView)view.findViewById(R.id.listView_touristicPlace_results);

        this.btnNewTouristicPlace.setOnClickListener(this);

        this.arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, this.currentItems);
        this.listViewResults.setAdapter(this.arrayAdapter);
        this.listViewResults.setOnItemClickListener(this);

        this.searchView = (SearchView) view.findViewById(R.id.searchView_touristicPlace_finder);

        this.searchView.setOnQueryTextListener(this);
        this.updateResults("");

        this.applyRoles();

        return view;
    }

    private void applyRoles() {
        boolean isEditor = LoginUserManager.getCurrent().isCurrentUserAnEditor();
        if (!isEditor) {
            this.btnNewTouristicPlace.setVisibility(View.GONE);
        }
    }

    private void updateResults(String search) {
        this.currentItems.clear();
        this.currentTouristicPlaces.clear();

        ObjectFilter targetFilter = NitriteManager.ALL_FILTER;

        if (search != null && search.length() > 0) {
            targetFilter = ObjectFilters.regex( LanguageManager.adaptFieldName("name"), String.format("(?i).*%s.*", search));
        }

        //ObjectRepository<TouristicPlace> targetRepository = MyApp.getCurrent().getAppDaos().getNitriteManager().getDb().getRepository(TouristicPlace.class);

        //int count = targetRepository.find(ObjectFilters.ALL).totalCount();

        List<TouristicPlace> touristicPlaceList =  MyApp.getCurrent().getAppDaos().getCommonsDao().find(targetFilter, TouristicPlace.class).toList();

        for(TouristicPlace item : touristicPlaceList) {
            this.currentItems.add(LanguageManager.translate(item.getName()));
            this.currentTouristicPlaces.add(item);
        }

        this.arrayAdapter.notifyDataSetChanged();
        //Toast.makeText(this.getContext(), "Count: " + count, Toast.LENGTH_LONG).show();

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
            case R.id.btn_touristicPlaces_addNew:
                this.loadFragment(TouristicPlaceEditionFragment.newInstanceForCreate());
                break;
        }
    }

    void loadFragment(INavigationChild fragment) {
        ((INavigationManager)this.getContext()).pushFragment(fragment);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TouristicPlaceEditionFragment targetFragment = TouristicPlaceEditionFragment.newInstanceForViewUpdate(this.currentTouristicPlaces.get(position).getId());
        this.loadFragment(targetFragment);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.updateResults(newText);
        return false;
    }

    @Override
    public void onTouristicPlaceEditionFinished(boolean needsUpdate) {
        if (needsUpdate) {
            this.updateResults(this.searchView.getQuery().toString());
        }
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_touristic_places);
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG_TOURISTIC_PLACES;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }
}
