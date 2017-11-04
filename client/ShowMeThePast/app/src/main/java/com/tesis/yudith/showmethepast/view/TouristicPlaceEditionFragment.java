package com.tesis.yudith.showmethepast.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.AppBlocker;
import com.tesis.yudith.showmethepast.configuration.Constants;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.CommonConstants;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.domain.collections.childs.GPSPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapMarker;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapScenario;
import com.tesis.yudith.showmethepast.helpers.BitmapTools;
import com.tesis.yudith.showmethepast.helpers.VolleyErrorTools;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.view.map.MapForMarkerFragment;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TouristicPlaceEditionFragment extends Fragment
        implements
            View.OnClickListener,
            StringEditorDialogFragment.IStringEditorListener,
            INavigationChild,
            MapForMarkerFragment.IPositionEditorListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String ARG_TOURISTIC_PLACE_ID = "touristicPlaceArgumentId";
    private static final String ARG_MODE_ID = "modeArgumentId";

    public static final int VIEW_OR_UPDATE_MODE = 0;
    public static final int CREATE_MODE = 1;

    private final int REQUEST_ID_TOURISTIC_PLACE = 0;
    private final int REQUEST_ID_IMAGE = 1;

    final int SELECT_PHOTO = 111;

    private int targetMode;
    private String targetTouristicPlaceId;

    TouristicPlace touristicPlace;
    ImageData imageData;

    Button btnSave;
    Button btnDelete;

    Button btnPosition;
    Button btnOldPictures;
    FloatingActionButton btnAddInformation;

    TextView txtTitle;
    TextView txtDescription;
    ImageView imgPicture;

    LinearLayout layoutEditor;
    ListView listViewInformation;

    INavigationManager navigationManager;

    private OnTouristicPlaceEditionEvents mListener;

    public void setPreviewTargetMode(int targetMode) {
        this.targetMode = targetMode;
    }

    public TouristicPlaceEditionFragment() {
        // Required empty public constructor
    }

    public static TouristicPlaceEditionFragment newInstanceForViewUpdate(String touristicPlaceId) {
        TouristicPlaceEditionFragment fragment = new TouristicPlaceEditionFragment();
        Bundle args = new Bundle();

        fragment.setPreviewTargetMode(VIEW_OR_UPDATE_MODE);
        args.putInt(ARG_MODE_ID, VIEW_OR_UPDATE_MODE);
        args.putString(ARG_TOURISTIC_PLACE_ID, touristicPlaceId);
        fragment.setArguments(args);

        return fragment;
    }

    public static TouristicPlaceEditionFragment newInstanceForCreate() {
        TouristicPlaceEditionFragment fragment = new TouristicPlaceEditionFragment();
        Bundle args = new Bundle();

        fragment.setPreviewTargetMode(CREATE_MODE);
        args.putInt(ARG_MODE_ID, CREATE_MODE);
        args.putString(ARG_TOURISTIC_PLACE_ID, null);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.targetTouristicPlaceId = this.getArguments().getString(ARG_TOURISTIC_PLACE_ID);
            this.targetMode = this.getArguments().getInt(ARG_MODE_ID);
            this.loadInformation();
        } else {
            try {
                throw new Exception("Error touristic place and mode ar mandatory for this fragment.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadInformation() {
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();

        if (this.targetMode == VIEW_OR_UPDATE_MODE) {
            this.touristicPlace = commonsDao.findOne(this.targetTouristicPlaceId, TouristicPlace.class);
            this.imageData = commonsDao.findOne(this.touristicPlace.getImage(), ImageData.class);
        } else {
            this.touristicPlace = new TouristicPlace();
            this.touristicPlace.setPosition(null);
            this.imageData = new ImageData();
        }
    }


    private void updateUI() {
        String name = LanguageManager.translate(this.touristicPlace.getName());
        String description = LanguageManager.translate(this.touristicPlace.getDescription());
        String imageBase64 = this.imageData.getData();

        if (name == null || name.trim().length() == 0) {
            name = this.getResources().getString(R.string.label_put_a_name);
        }

        if (description == null || description.trim().length() == 0) {
            description = this.getResources().getString(R.string.label_put_a_description);
        }

        if (imageBase64 == null || imageBase64.length() == 0) {
            this.imgPicture.setImageDrawable(this.getResources().getDrawable(R.mipmap.ic_no_image, this.getActivity().getTheme()));
        } else {
            this.imgPicture.setImageBitmap(BitmapTools.decodeFromBase64(imageBase64));
        }


        this.txtTitle.setText(name);
        this.txtDescription.setText(description);

        if (touristicPlace.getPosition() != null) {
            String regionKey = touristicPlace.getPosition().getRegionKey();
            if (regionKey != null) {
                this.btnPosition.setText(regionKey);
            }
        }

        this.updateInformationList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_touristic_place_edition, container, false);

        this.linkControls(view);
        this.applyRoles();

        this.updateUI();

        return view;
    }

    private void linkControls(View view) {
        this.txtTitle = (TextView)view.findViewById(R.id.txt_TouristicPlace_Title);
        this.txtDescription = (TextView)view.findViewById(R.id.txt_TouristicPlace_Description);
        this.btnSave = (Button)view.findViewById(R.id.btn_TouristicPlace_Save);
        this.btnDelete = (Button)view.findViewById(R.id.btn_TouristicPlace_delete);
        this.imgPicture = (ImageView)view.findViewById(R.id.img_TouristicPlace_Picture);
        this.btnPosition = (Button)view.findViewById(R.id.btn_TouristicPlace_Position);
        this.btnOldPictures = (Button)view.findViewById(R.id.btn_TouristicPlace_OldPictures);
        this.btnAddInformation = (FloatingActionButton)view.findViewById(R.id.btn_TouristicPlace_addInformation);
        this.listViewInformation = (ListView)view.findViewById(R.id.listView_TouristicPlace_information);
        this.listViewInformation.setOnItemClickListener(this);
        this.listViewInformation.setOnItemLongClickListener(this);

        this.layoutEditor = (LinearLayout)view.findViewById(R.id.layout_TouristicPlace_forEditor);

        this.btnSave.setOnClickListener(this);
        this.txtTitle.setOnClickListener(this);
        this.txtDescription.setOnClickListener(this);
        this.imgPicture.setOnClickListener(this);
        this.btnPosition.setOnClickListener(this);
        this.btnOldPictures.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
        this.btnAddInformation.setOnClickListener(this);
    }

    private void applyRoles() {
        boolean isEditor = LoginUserManager.getCurrent().isCurrentUserAnEditor();
        if (!isEditor) {
            this.layoutEditor.setVisibility(View.GONE);
            this.btnAddInformation.setVisibility(View.GONE);
        }

        if (this.targetMode == CREATE_MODE) {
            this.btnOldPictures.setEnabled(false);
            this.btnDelete.setEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.navigationManager = (INavigationManager)context;

        if (context instanceof OnTouristicPlaceEditionEvents) {
            mListener = (OnTouristicPlaceEditionEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTouristicPlaceEditionEvents");
        }
    }

    void loadFragment(INavigationChild fragment) {
        ((INavigationManager)this.getContext()).pushFragment(fragment);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener.onTouristicPlaceEditionFinished(true);
        mListener = null;
    }

    boolean currentUserIsAnEditor() {
        return LoginUserManager.getCurrent().isCurrentUserAnEditor();
    }

    @Override
    public void onClick(View v) {
        String title;
        FragmentManager fm;
        StringEditorDialogFragment editor;

        switch (v.getId()) {
            case R.id.txt_TouristicPlace_Title:
                title = this.getResources().getString(R.string.label_edit_touristic_place_title);

                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    fm = this.getActivity().getSupportFragmentManager();
                    editor = StringEditorDialogFragment.newInstance(title, this, v.getId(), 0, this.touristicPlace.getName());
                    editor.setTargetFragment(this, 123);
                    editor.show(fm, "fragment_string_editor_dialog");
                }

                break;
            case R.id.txt_TouristicPlace_Description:
                title = this.getResources().getString(R.string.label_edit_touristic_place_description);

                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    fm = this.getActivity().getSupportFragmentManager();
                    editor = StringEditorDialogFragment.newInstance(title, this, v.getId(), 0, this.touristicPlace.getDescription());
                    editor.show(fm, "fragment_string_editor_dialog");
                }
                break;
            case R.id.img_TouristicPlace_Picture:
                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
                break;
            case R.id.btn_TouristicPlace_Position:
                this.actionPickTouristicPlace();
                break;
            case R.id.btn_TouristicPlace_Save:
                this.saveTouristicPlace();
                break;
            case R.id.btn_TouristicPlace_delete:
                this.deleteTouristicPlace();
                break;
            case R.id.btn_TouristicPlace_OldPictures:
                OldPicturesMapFragment oldPicturesFragment = OldPicturesMapFragment.newInstance(this.touristicPlace.getId());
                this.loadFragment(oldPicturesFragment);
                break;
            case R.id.btn_TouristicPlace_addInformation:
                title = this.getResources().getString(R.string.label_notification_open_information);

                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    fm = this.getActivity().getSupportFragmentManager();
                    editor = StringEditorDialogFragment.newInstance(title, this, v.getId(), 0, new MultiLanguageString());
                    editor.setTargetFragment(this, 123);
                    editor.show(fm, "fragment_string_editor_dialog");
                }
                break;
                /*
                OldPictureEditionFragment oldPictureFragment = OldPictureEditionFragment.newInstance();
                //this.loadFragment(oldPictureFragment, R.string.title_fragment_old_picture);
                this.getActivity().setTitle(this.getResources().getString(R.string.title_fragment_old_picture));
                FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, oldPictureFragment).commit();
                break;*/
        }
    }

    private void actionPickTouristicPlace() {
        LatLng position = this.getCurrentPosition();
        SerializableMapMarker targetMarker = new SerializableMapMarker();
        SerializableMapScenario scenario = new SerializableMapScenario();

        targetMarker.setMarker(MarkersManager.EMarker.MARKER_TOURISTIC_PLACE);

        if (this.targetMode == CREATE_MODE) {
            scenario.setCenterLatitude(position.latitude);
            scenario.setCenterLongitude(position.longitude);

            targetMarker.setLatitude(position.latitude);
            targetMarker.setLongitude(position.longitude);
            targetMarker.setTitle(this.getResources().getString(R.string.label_new_touristic_place));
        } else {
            scenario.setCenterLatitude(this.touristicPlace.getPosition().getLatitude());
            scenario.setCenterLongitude(this.touristicPlace.getPosition().getLongitude());

            targetMarker.setLatitude(scenario.getCenterLatitude());
            targetMarker.setLongitude(scenario.getCenterLongitude());
            targetMarker.setTitle(LanguageManager.translate(this.touristicPlace.getName()));
        }

        scenario.getDraggableMarkers().add(targetMarker);

        MapForMarkerFragment picker = MapForMarkerFragment.newInstance(scenario, !this.currentUserIsAnEditor());
        picker.setListener(this);
        this.loadFragment(picker);
    }

    private void deleteTouristicPlace() {
        final TouristicPlaceEditionFragment self = this;

        AlertDialog dialog =
                new AlertDialog.Builder(this.getContext())
                    .setTitle(this.getResources().getString(R.string.label_confirmation))
                    .setMessage(this.getResources().getString(R.string.question_for_delete))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            self.confirmDeleteTouristicPlace();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
    }

    private void confirmDeleteTouristicPlace() {
        final TouristicPlaceEditionFragment self = this;
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        AppBlocker.loading();

        MyApp.getCurrent().getAppRequests().getCommonRequests().delete(REQUEST_ID_TOURISTIC_PLACE, currentUser, this.touristicPlace, new IRequestListener<MongoCollection>() {
            @Override
            public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
                MyApp.getCurrent().getAppControllers().getTouristicPlacesController().delete(self.touristicPlace);
                MyApp.getCurrent().updateLastModification();
                AppBlocker.finished();
                self.removeFromUI();
            }

            @Override
            public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                AppBlocker.finished();
                if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                    MyApp.getCurrent().getAppControllers().getTouristicPlacesController().delete(self.touristicPlace);
                    self.removeFromUI();
                } else {
                    Toast.makeText(self.getContext(), "Error removing the document from the server.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields() {
        if (this.touristicPlace.getPosition() == null) {
            Toast.makeText(this.getContext(), "You need to pick a position.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.imageData.getData() == null || this.imageData.getData().length() == 0) {
            Toast.makeText(this.getContext(), "You need to pick an image.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.imageData.getData() == null || this.imageData.getData().length() == 0) {
            Toast.makeText(this.getContext(), "You need to pick an image.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.touristicPlace.getName().isInvalid()) {
            Toast.makeText(this.getContext(), "You need to specify a valid name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.touristicPlace.getDescription().isInvalid()) {
            Toast.makeText(this.getContext(), "You need to specify a valid description.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Toast.makeText(this.getContext(), "Visible: " + isVisibleToUser , Toast.LENGTH_SHORT).show();
    }

    private void saveTouristicPlace() {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        String currentRegionKey = this.touristicPlace.getPosition().getRegionKey();

        if (!this.validateFields()) {
            return;
        }

        if (currentRegionKey == null || currentRegionKey.length() == 0) {
            currentRegionKey = this.getRegionKeyFor(this.touristicPlace.getPosition().toLatLng());
            this.touristicPlace.getPosition().setRegionKey(currentRegionKey);
        }

        if (currentUser == null) {
            Toast.makeText(this.getContext(), "You must be logged.", Toast.LENGTH_LONG).show();
            return;
        }

        if (this.targetMode == CREATE_MODE) {
            this.createTouristicPlace();
        } else {
            this.updateTouristicPlace();
        }
    }

    private void updateTouristicPlace() {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        final TouristicPlaceEditionFragment self = this;

        AppBlocker.loading();

        this.imageData.setDescription(null);
        this.imageData.setAuthor(null);

        MyApp.getCurrent().getAppRequests().getCommonRequests().update(REQUEST_ID_IMAGE, currentUser, this.imageData, new IRequestListener<MongoCollection>() {
            @Override
            public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
                self.imageData.setUpdateInformation(result);
                MyApp.getCurrent().getAppDaos().getCommonsDao().update(self.imageData, ImageData.class);
                MyApp.getCurrent().getAppRequests().getCommonRequests().update(REQUEST_ID_TOURISTIC_PLACE, currentUser, self.touristicPlace, new IRequestListener<MongoCollection>() {
                    @Override
                    public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
                        self.touristicPlace.setUpdateInformation(result);
                        MyApp.getCurrent().getAppDaos().getCommonsDao().update(self.touristicPlace, TouristicPlace.class);
                        AppBlocker.finished();
                        MyApp.getCurrent().updateLastModification();
                    }

                    @Override
                    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                        AppBlocker.finished();
                        if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                            Toast.makeText(self.getContext(), "This register does'nt exists in the server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(self.getContext(), "Error updating the register.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                AppBlocker.finished();
                if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                    Toast.makeText(self.getContext(), "This register does'nt exists in the server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(self.getContext(), "Error updating the register.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createTouristicPlace() {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        final TouristicPlaceEditionFragment self = this;

        AppBlocker.loading();

        this.imageData.setDescription(null);
        this.imageData.setAuthor(null);

        MyApp.getCurrent().getAppRequests().getCommonRequests().create(REQUEST_ID_IMAGE, currentUser, this.imageData, new IRequestListener<MongoCollection>() {
            @Override
            public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {

                self.imageData.setCreateInformation(result);
                self.touristicPlace.setImage(result.getId());

                MyApp.getCurrent().getAppRequests().getCommonRequests().create(REQUEST_ID_TOURISTIC_PLACE, currentUser, self.touristicPlace, new IRequestListener<MongoCollection>() {
                    @Override
                    public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
                        self.touristicPlace.setCreateInformation(result);
                        //Toast.makeText(self.getContext(), "Touristic Place: " + JsonTools.objectToJson(result), Toast.LENGTH_LONG).show();

                        self.saveLocally();
                        self.removeFromUI();
                        MyApp.getCurrent().updateLastModification();

                        AppBlocker.finished();
                    }

                    @Override
                    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                        Toast.makeText(self.getContext(), "Error, cannot create the touristic place.", Toast.LENGTH_LONG).show();
                        AppBlocker.finished();
                    }
                });

                //Toast.makeText(self.getContext(), "Image :" + result.getId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                Toast.makeText(self.getContext(), "Error, cannot create the image.", Toast.LENGTH_LONG).show();
                AppBlocker.finished();
            }
        });
    }

    private void removeFromUI() {
        this.mListener.onTouristicPlaceEditionFinished(true);
        //FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
        //ft.remove(this).commit();
        this.navigationManager.popFragment(true);
    }

    private void saveLocally() {
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        commonsDao.insert(this.imageData, ImageData.class);
        commonsDao.insert(this.touristicPlace, TouristicPlace.class);
    }

    LatLng getCurrentPosition() {
        GPSPosition position = this.touristicPlace.getPosition();

        if (position == null) {
            // Coordinates of "Casa Nacional de Moneda Potosi"
            return new LatLng(-19.588528, -65.754171);
        } else {
            return new LatLng(position.getLatitude(), position.getLongitude());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                Bitmap scaledBitmap = BitmapTools.scaleBitmap(bitmap, Constants.MAX_BITMAP_AREA);

                this.imageData.setData(BitmapTools.encodeToBase64(scaledBitmap, Bitmap.CompressFormat.JPEG, 75));
                this.updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onChange(StringEditorDialogFragment target, int targetField, int targetIndex, MultiLanguageString values) {
        switch (targetField) {
            case R.id.txt_TouristicPlace_Title:
                this.touristicPlace.setName(values);
                break;
            case R.id.txt_TouristicPlace_Description:
                this.touristicPlace.setDescription(values);
                break;
            case R.id.btn_TouristicPlace_addInformation:
                this.touristicPlace.getInformationList().add(values);
                this.updateInformationList();
                break;
            case R.id.listView_TouristicPlace_information:
                this.touristicPlace.getInformationList().set(targetIndex, values);
                break;
        }
        this.updateUI();
    }

    private void updateInformationList() {
        ArrayList<String> currentAdapterArray = new ArrayList<>();

        for(int i = 0; i < this.touristicPlace.getInformationList().size(); i++) {
            currentAdapterArray.add(LanguageManager.translate(this.touristicPlace.getInformationList().get(i)));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, currentAdapterArray);
        this.listViewInformation.setAdapter(arrayAdapter);
    }

    private String getRegionKeyFor(LatLng target) {
        String currentRegionKey;
        try {
            currentRegionKey = MyApp.getCurrent().getGeocoderTools().getRegionKey(target);
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(this.getContext(), this.getString(R.string.error_message_google_maps_geocoder) + "\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            currentRegionKey = CommonConstants.DEFAULT_REGION_KEY;
        }
        return currentRegionKey;
    }

    @Override
    public void onChange(MapForMarkerFragment target, List<SerializableMapMarker> markers) {
        SerializableMapMarker marker = markers.get(0);
        GPSPosition position = marker.getGPSPosition();

        String region = this.getRegionKeyFor(position.toLatLng());

        position.setRegionKey(region);
        this.btnPosition.setText(region);
        this.touristicPlace.setPosition(position);
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        if (this.targetMode == CREATE_MODE) {
            return resources.getString(R.string.title_fragment_touristic_places_creation);
        } else {
            if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                return resources.getString(R.string.title_fragment_touristic_places_edition);
            }
            return resources.getString(R.string.title_fragment_touristic_places_view);
        }
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.editInformationItem(position);
    }

    private void editInformationItem(int position) {
        String title = this.getResources().getString(R.string.label_notification_open_information);

        if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
            FragmentManager fm = this.getActivity().getSupportFragmentManager();
            StringEditorDialogFragment editor = StringEditorDialogFragment.newInstance(title, this, R.id.listView_TouristicPlace_information, position, this.touristicPlace.getInformationList().get(position));
            editor.setTargetFragment(this, 123);
            editor.show(fm, "fragment_string_editor_dialog");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

        final String[] options = new String[]{ this.getString(R.string.label_button_update), this.getString(R.string.label_button_delete) };

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.getActivity());
        builderSingle.setTitle(R.string.title_information_list_options);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_single_choice);
        arrayAdapter.addAll(options);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_button_update))) {
                    editInformationItem(position);
                } else if (options[which].equals(getString(R.string.label_button_delete))) {
                    deleteInformationItem(position);
                }
            }
        });
       builderSingle.show();

        return false;
    }

    private void deleteInformationItem(int position) {
        this.touristicPlace.getInformationList().remove(position);
        this.updateInformationList();
    }

    public interface OnTouristicPlaceEditionEvents {
        void onTouristicPlaceEditionFinished(boolean needsUpdate);
    }
}
