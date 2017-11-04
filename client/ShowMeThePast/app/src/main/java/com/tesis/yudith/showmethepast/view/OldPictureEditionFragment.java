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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.TestCameraActivity;
import com.tesis.yudith.showmethepast.configuration.AppBlocker;
import com.tesis.yudith.showmethepast.configuration.Constants;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.CommonConstants;
import com.tesis.yudith.showmethepast.domain.CommonEnumerators;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.domain.collections.childs.ARPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.GPSPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapMarker;
import com.tesis.yudith.showmethepast.domain.serializables.SerializableMapScenario;
import com.tesis.yudith.showmethepast.helpers.BitmapTools;
import com.tesis.yudith.showmethepast.helpers.VolleyErrorTools;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.view.ar.ArTestCanvas;
import com.tesis.yudith.showmethepast.view.map.MapForMarkerFragment;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;

import java.io.IOException;
import java.util.List;

public class OldPictureEditionFragment extends Fragment
        implements
            View.OnClickListener,
            MapForMarkerFragment.IPositionEditorListener,
            StringEditorDialogFragment.IStringEditorListener,
            INavigationChild,
            IRequestListener<MongoCollection> {

    final int SELECT_OLD_PHOTO = 112;
    final int REQUEST_AR_ACTIVITY = 114;

    private static final String ARG_TOURISTIC_PLACE_ID = "ARG_TOURISTIC_PLACE_ID";
    private static final String ARG_OLD_PICTURE_ID = "ARG_OLD_PICTURE_ID";
    private static final String ARG_EDITION_MODE_ID = "ARG_EDITION_MODE_ID";

    private final int REQUEST_ID_OLD_PICTURE = 0;
    private final int REQUEST_ID_IMAGE = 1;

    Button btnSave;
    Button btnDelete;
    Button btnPositions;
    Button btnArPosition;

    TextView txtName;
    TextView txtDescription;
    ImageView imgPicture;

    OldPicture oldPicture;
    ImageData imageData;

    String touristicPlaceId;
    String oldPictureIdArgument;
    TouristicPlace touristicPlace;

    LinearLayout layoutForEdition;

    CommonEnumerators.EEditionMode currentEditionMode;
    private Bitmap currentOldPictureBitmap;
    INavigationManager navigationManager;

    public void setPreviewEditionMode(CommonEnumerators.EEditionMode currentEditionMode) {
        this.currentEditionMode = currentEditionMode;
    }

    public OldPictureEditionFragment() {

    }

    public static OldPictureEditionFragment newInstanceForCreate(String touristicPlaceId) {
        OldPictureEditionFragment fragment = new OldPictureEditionFragment();
        Bundle args = new Bundle();
        fragment.setPreviewEditionMode(CommonEnumerators.EEditionMode.MODE_CREATE);
        args.putString(ARG_TOURISTIC_PLACE_ID, touristicPlaceId);
        args.putSerializable(ARG_EDITION_MODE_ID, CommonEnumerators.EEditionMode.MODE_CREATE);
        fragment.setArguments(args);
        return fragment;
    }

    public static OldPictureEditionFragment newInstanceForUpdate(String touristicPlaceId, String oldPictureId) {
        OldPictureEditionFragment fragment = new OldPictureEditionFragment();
        Bundle args = new Bundle();
        fragment.setPreviewEditionMode(CommonEnumerators.EEditionMode.MODE_UPDATE_OR_VIEW);
        args.putString(ARG_TOURISTIC_PLACE_ID, touristicPlaceId);
        args.putString(ARG_OLD_PICTURE_ID, oldPictureId);
        args.putSerializable(ARG_EDITION_MODE_ID, CommonEnumerators.EEditionMode.MODE_UPDATE_OR_VIEW);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_TOURISTIC_PLACE_ID, this.touristicPlaceId);
        outState.putSerializable(ARG_EDITION_MODE_ID, this.currentEditionMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.touristicPlaceId = this.getArguments().getString(ARG_TOURISTIC_PLACE_ID);
            this.touristicPlace = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(this.touristicPlaceId, TouristicPlace.class);
            this.currentEditionMode = (CommonEnumerators.EEditionMode)this.getArguments().getSerializable(ARG_EDITION_MODE_ID);
            if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_UPDATE_OR_VIEW) {
                this.oldPictureIdArgument = this.getArguments().getString(ARG_OLD_PICTURE_ID);
            }
            this.startInformation();
        }
    }

    private void startInformation() {
        if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_CREATE) {
            this.oldPicture = new OldPicture();
            this.imageData = new ImageData();
            this.oldPicture.setTouristicPlace(this.touristicPlaceId);

            this.oldPicture.getPosition().setStartPosition(null);
            this.oldPicture.getPosition().setTargetPosition(null);

        } else {
            this.oldPicture = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(this.oldPictureIdArgument, OldPicture.class);
            this.imageData = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(this.oldPicture.getImage(), ImageData.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_picture_edition, container, false);
        this.linkControls(view);
        this.applyRoles();
        this.updateUI();
        return view;
    }

    private void applyRoles() {
        if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_CREATE) {
            this.btnDelete.setEnabled(false);
        }

        if (!LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
            this.layoutForEdition.setVisibility(View.GONE);
        }
    }

    void linkControls(View view) {
        this.btnSave = (Button)view.findViewById(R.id.btn_editOldPicture_Save);
        this.btnDelete = (Button)view.findViewById(R.id.btn_editOldPicture_delete);
        this.btnPositions = (Button)view.findViewById(R.id.btn_editOldPicture_positions);
        this.btnArPosition = (Button)view.findViewById(R.id.btn_editOldPicture_arPosition);

        this.txtName = (TextView) view.findViewById(R.id.txt_editOldPicture_name);
        this.txtDescription = (TextView)view.findViewById(R.id.txt_editOldPicture_Description);
        this.imgPicture = (ImageView)view.findViewById(R.id.img_editOldPicture_Picture);

        this.layoutForEdition = (LinearLayout)view.findViewById(R.id.layout_editOldPicture_forEditor);

        this.btnSave.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
        this.btnPositions.setOnClickListener(this);
        this.btnArPosition.setOnClickListener(this);
        this.txtName.setOnClickListener(this);
        this.txtDescription.setOnClickListener(this);
        this.imgPicture.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.navigationManager = (INavigationManager)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void loadFragment(INavigationChild fragment) {
        ((INavigationManager)this.getContext()).pushFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        String title;
        FragmentManager fm;
        StringEditorDialogFragment editor;

        switch (v.getId()) {
            case R.id.btn_editOldPicture_Save:
                this.actionSave();
                break;
            case R.id.btn_editOldPicture_delete:
                this.actionDelete();
                break;

            case R.id.btn_editOldPicture_positions:
                this.actionPickPositions();
                break;
            case R.id.btn_editOldPicture_arPosition:
                this.actionAR();
                break;
            case R.id.txt_editOldPicture_name:
                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    title = this.getResources().getString(R.string.label_edit_touristic_place_title);
                    fm = this.getActivity().getSupportFragmentManager();
                    editor = StringEditorDialogFragment.newInstance(title, this, v.getId(), 0, this.oldPicture.getName());
                    editor.setTargetFragment(this, 123);
                    editor.setListener(this);
                    editor.show(fm, "fragment_string_editor_dialog");
                }
                break;
            case R.id.txt_editOldPicture_Description:
                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    title = this.getResources().getString(R.string.label_edit_touristic_place_description);

                    fm = this.getActivity().getSupportFragmentManager();
                    editor = StringEditorDialogFragment.newInstance(title, this, v.getId(), 0, this.oldPicture.getDescription());
                    editor.setTargetFragment(this, 123);
                    editor.setListener(this);
                    editor.show(fm, "fragment_string_editor_dialog");
                }
                break;
            case R.id.img_editOldPicture_Picture:
                if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_OLD_PHOTO);
                }
                break;
        }
    }

    private void actionAR() {
        if (this.imageData.getData() == null || this.imageData.getData().length()==0) {
            Toast.makeText(this.getContext(), "Pick a image!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle options = new Bundle();

        options.putString(TestCameraActivity.ARG_TARGET_SERIALIZED_IMAGE, this.imageData.getData());
        options.putSerializable(TestCameraActivity.ARG_TARGET_POSITION_OBJECT_KEY, this.oldPicture.getPosition());
        options.putSerializable(TestCameraActivity.ARG_TARGET_OLD_PICTURE_DOCUMENT, this.oldPicture);

        Intent myIntent = new Intent(this.getActivity(), TestCameraActivity.class);
        myIntent.putExtras(options);

        this.startActivityForResult(myIntent, REQUEST_AR_ACTIVITY);
    }

    private void actionDelete() {
        final OldPictureEditionFragment self = this;

        AlertDialog dialog =
                new AlertDialog.Builder(this.getContext())
                        .setTitle(this.getResources().getString(R.string.label_confirmation))
                        .setMessage(this.getResources().getString(R.string.question_for_delete))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                self.confirmDeleteOldPicture();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
    }

    private void confirmDeleteOldPicture() {
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        AppBlocker.loading();

        MyApp.getCurrent().getAppRequests().getCommonRequests().delete(REQUEST_ID_OLD_PICTURE, currentUser, this.oldPicture, this);
    }

    private boolean validateFields() {
        if (this.oldPicture.getPosition().getStartPosition() == null/* || this.oldPicture.getPosition().getStartPosition().isZero()*/) {
            Toast.makeText(this.getContext(), "You need to pick a start position.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.oldPicture.getPosition().getTargetPosition() == null/* || this.oldPicture.getPosition().getTargetPosition().isZero()*/) {
            Toast.makeText(this.getContext(), "You need to pick a target position.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.imageData.getData() == null || this.imageData.getData().length() == 0) {
            Toast.makeText(this.getContext(), "You need to pick an image.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.oldPicture.getName().isInvalid()) {
            Toast.makeText(this.getContext(), "You need to specify a valid name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (this.oldPicture.getDescription().isInvalid()) {
            Toast.makeText(this.getContext(), "You need to specify a valid description.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void actionSave() {
        if (!this.validateFields()) {
            return;
        }

        this.verifyRegionKey(this.oldPicture.getPosition().getStartPosition());
        this.verifyRegionKey(this.oldPicture.getPosition().getTargetPosition());

        if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_CREATE) {
            this.actionCreate();
        } else {
            this.actionUpdate();
        }     
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

    private void verifyRegionKey(GPSPosition position) {
        String currentRegionKey = position.getRegionKey();

        if (currentRegionKey == null || currentRegionKey.length() == 0) {
            currentRegionKey = this.getRegionKeyFor(this.touristicPlace.getPosition().toLatLng());
            position.setRegionKey(currentRegionKey);
        }
    }

    private void actionUpdate() {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        this.imageData.setDescription(null);
        this.imageData.setAuthor(null);

        AppBlocker.loading();
        MyApp.getCurrent().getAppRequests().getCommonRequests().update(REQUEST_ID_IMAGE, currentUser, this.imageData, this);
    }

    private void actionCreate() {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        this.imageData.setDescription(null);
        this.imageData.setAuthor(null);

        AppBlocker.loading();
        MyApp.getCurrent().getAppRequests().getCommonRequests().create(REQUEST_ID_IMAGE, currentUser, this.imageData, this);
    }

    private void removeFromUI() {
        this.navigationManager.popFragment(true);
    }

    private void actionPickPositions() {
        SerializableMapScenario scenario = new SerializableMapScenario();
        SerializableMapMarker markerStart = new SerializableMapMarker();
        SerializableMapMarker markerTarget = new SerializableMapMarker();
        SerializableMapMarker markerTouristicPlace = new SerializableMapMarker();

        LatLng startPosition;
        LatLng targetPosition;

        scenario.setCenterLatitude(this.touristicPlace.getPosition().getLatitude());
        scenario.setCenterLongitude(this.touristicPlace.getPosition().getLongitude());

        if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_CREATE) {
            if (oldPicture.getPosition().getStartPosition() == null) {
                startPosition = SphericalUtil.computeOffset(new LatLng(scenario.getCenterLatitude(), scenario.getCenterLongitude()), 50, 0);
            } else {
                startPosition = oldPicture.getPosition().getStartPosition().toLatLng();
            }
            if (oldPicture.getPosition().getTargetPosition() == null) {
                targetPosition = SphericalUtil.computeOffset(new LatLng(scenario.getCenterLatitude(), scenario.getCenterLongitude()), 100, 0);
            } else {
                targetPosition = oldPicture.getPosition().getTargetPosition().toLatLng();
            }
        } else {
            startPosition = this.oldPicture.getPosition().getStartPosition().toLatLng();
            targetPosition = this.oldPicture.getPosition().getTargetPosition().toLatLng();
        }

        markerStart.setLatitude(startPosition.latitude);
        markerStart.setLongitude(startPosition.longitude);
        markerStart.setTitle(this.getString(R.string.label_marker_start_position));
        markerStart.setMarker(MarkersManager.EMarker.MARKER_PICK_START);

        markerTarget.setLatitude(targetPosition.latitude);
        markerTarget.setLongitude(targetPosition.longitude);
        markerTarget.setTitle(this.getString(R.string.label_marker_target_position));
        markerTarget.setMarker(MarkersManager.EMarker.MARKER_PICK_TARGET);

        markerTouristicPlace.setLatitude(scenario.getCenterLatitude());
        markerTouristicPlace.setLongitude(scenario.getCenterLongitude());
        markerTouristicPlace.setTitle(LanguageManager.translate(this.touristicPlace.getName()));
        markerTouristicPlace.setMarker(MarkersManager.EMarker.MARKER_TOURISTIC_PLACE);

        scenario.getDraggableMarkers().add(markerStart);
        scenario.getDraggableMarkers().add(markerTarget);
        scenario.getStaticMarkers().add(markerTouristicPlace);

        MapForMarkerFragment picker = MapForMarkerFragment.newInstance(scenario, !LoginUserManager.getCurrent().isCurrentUserAnEditor());
        picker.setListener(this);
        this.loadFragment(picker);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_OLD_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

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

        if (requestCode == REQUEST_AR_ACTIVITY && resultCode == TestCameraActivity.RETURN_CODE) {
            ARPosition modified = (ARPosition)data.getSerializableExtra(TestCameraActivity.ARG_TARGET_POSITION_OBJECT_KEY);
            this.oldPicture.setPosition(modified);
        }
    }

    private void updateUI() {
        String name = LanguageManager.translate(this.oldPicture.getName());
        String description = LanguageManager.translate(this.oldPicture.getDescription());
        String imageBase64 = this.imageData.getData();

        if (name == null || name.trim().length() == 0) {
            name = this.getResources().getString(R.string.label_put_a_name);
        }

        if (description == null || description.trim().length() == 0) {
            description = this.getResources().getString(R.string.label_put_a_description);
        }

        if (imageBase64 == null || imageBase64.length() == 0) {
            this.imgPicture.setImageDrawable(this.getResources().getDrawable(R.mipmap.ic_no_photo, this.getActivity().getTheme()));
        } else {
            this.currentOldPictureBitmap = BitmapTools.decodeFromBase64(imageBase64);
            this.imgPicture.setImageBitmap(this.currentOldPictureBitmap);
        }

        this.txtName.setText(name);
        this.txtDescription.setText(description);
    }

    @Override
    public void onChange(StringEditorDialogFragment target, int targetField, int targetIndex, MultiLanguageString values) {
        switch (targetField) {
            case R.id.txt_editOldPicture_name:
                this.oldPicture.setName(values);
                break;
            case R.id.txt_editOldPicture_Description:
                this.oldPicture.setDescription(values);
                break;
        }
        this.updateUI();
    }

    @Override
    public void onChange(MapForMarkerFragment target, List<SerializableMapMarker> markers) {
        SerializableMapMarker markerStart = markers.get(0);
        SerializableMapMarker markerTarget = markers.get(1);

        GPSPosition positionStart = markerStart.getGPSPosition();
        GPSPosition positionTarget = markerTarget.getGPSPosition();

        positionStart.setRegionKey(this.getRegionKeyFor(positionStart.toLatLng()));
        positionTarget.setRegionKey(this.getRegionKeyFor(positionTarget.toLatLng()));

        /*
        try {
            positionStart.setRegionKey(MyApp.getCurrent().getGeocoderTools().getRegionKey(positionStart.toLatLng()));
            positionTarget.setRegionKey(MyApp.getCurrent().getGeocoderTools().getRegionKey(positionTarget.toLatLng()));
        } catch (Exception error) {
            Toast.makeText(this.getContext(), this.getString(R.string.error_message_google_maps_geocoder), Toast.LENGTH_LONG).show();
        }
        */

        this.oldPicture.getPosition().setStartPosition(positionStart);
        this.oldPicture.getPosition().setTargetPosition(positionTarget);
    }

    @Override
    public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        switch (requestIdentifier) {
            case REQUEST_ID_IMAGE:
                if (requestType == ERequestType.CREATE) {
                    this.imageData.setCreateInformation(result);
                    this.oldPicture.setImage(result.getId());
                    commonsDao.insert(this.imageData, ImageData.class);
                    MyApp.getCurrent().getAppRequests().getCommonRequests().create(REQUEST_ID_OLD_PICTURE, currentUser, this.oldPicture, this);
                } else if (requestType == ERequestType.UPDATE){
                    this.imageData.setUpdateInformation(result);
                    MyApp.getCurrent().getAppRequests().getCommonRequests().update(REQUEST_ID_OLD_PICTURE, currentUser, this.oldPicture, this);
                    commonsDao.update(this.imageData, ImageData.class);
                } else if (requestType == ERequestType.DELETE) {
                    commonsDao.remove(this.imageData, ImageData.class);
                }
                break;
            case REQUEST_ID_OLD_PICTURE:
                if (requestType == ERequestType.CREATE) {
                    this.oldPicture.setCreateInformation(result);
                    commonsDao.insert(this.oldPicture, OldPicture.class);
                } else if (requestType == ERequestType.UPDATE){
                    this.oldPicture.setUpdateInformation(result);
                    commonsDao.update(this.oldPicture, OldPicture.class);
                } else if (requestType == ERequestType.DELETE) {
                    MyApp.getCurrent().getAppControllers().getOldPictureController().delete(this.oldPicture);
                }
                MyApp.getCurrent().updateLastModification();
                this.removeFromUI();
                AppBlocker.finished();
                break;
        }
    }

    @Override
    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
        switch (requestIdentifier) {
            case REQUEST_ID_IMAGE:
                switch (requestType) {
                    case CREATE:
                        Toast.makeText(this.getContext(), "Error, cannot create the image.", Toast.LENGTH_LONG).show();
                        break;
                    case UPDATE:
                        Toast.makeText(this.getContext(), "Error, cannot update the image.", Toast.LENGTH_LONG).show();
                        break;
                    case DELETE:
                        if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                            MyApp.getCurrent().getAppDaos().getCommonsDao().remove(this.imageData, ImageData.class);
                        }
                        Toast.makeText(this.getContext(), "Error, cannot delete the image.", Toast.LENGTH_LONG).show();
                        break;
                }
            case REQUEST_ID_OLD_PICTURE:
                switch (requestType) {
                    case CREATE:
                        Toast.makeText(this.getContext(), "Error, cannot create the old picture.", Toast.LENGTH_LONG).show();
                        break;
                    case UPDATE:
                        Toast.makeText(this.getContext(), "Error, cannot update the old picture.", Toast.LENGTH_LONG).show();
                        break;
                    case DELETE:
                        if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                            MyApp.getCurrent().getAppControllers().getOldPictureController().delete(this.oldPicture);
                            this.removeFromUI();
                        }
                        Toast.makeText(this.getContext(), "Error, cannot delete the old picture.", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
        AppBlocker.finished();
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        if (this.currentEditionMode == CommonEnumerators.EEditionMode.MODE_CREATE) {
            return resources.getString(R.string.title_fragment_old_picture_creation);
        }  else {
            if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                return resources.getString(R.string.title_fragment_old_picture_edition);
            }
            return resources.getString(R.string.title_fragment_old_picture_view);
        }
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }
}

