package com.tesis.yudith.showmethepast;

import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.collections.ImageData;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.domain.collections.local.ConfigCollection;
import com.tesis.yudith.showmethepast.domain.sync.CombinedSyncResults;
import com.tesis.yudith.showmethepast.domain.sync.ProcessedSyncResult;
import com.tesis.yudith.showmethepast.domain.sync.SynchronizationResult;
import com.tesis.yudith.showmethepast.exceptions.RequestException;
import com.tesis.yudith.showmethepast.helpers.JsonTools;
import com.tesis.yudith.showmethepast.requests.CommonRequests;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.ESyncType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.requests.tools.IRequestSyncListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncDataActivity extends AppCompatActivity implements View.OnClickListener, IRequestSyncListener<CombinedSyncResults>,IRequestListener<MongoCollection> {

    private final int REQUEST_ID_CREATIONS = 10001;
    private final int REQUEST_ID_EDITIONS = 10002;
    private final int REQUEST_ID_DELETIONS = 10003;

    Button btnStart;
    ProgressBar progressGeneral;
    ProgressBar progressSpecific;
    TextView lblGeneral;
    TextView lblSpecific;
    TextView lblLastSync;
    TextView lblServerUrl;

    private boolean isSyncInProgress;
    private Date dateSyncStart;
    private Date dateLastSync;

    private Map<Class<? extends MongoCollection>, CombinedSyncResults> syncInformation;
    private Map<Class<? extends MongoCollection>, ProcessedSyncResult> processedInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        this.isSyncInProgress = false;
        this.linkControls();
        this.setTitle(getResources().getString(R.string.title_fragment_synchronization));
    }

    private void linkControls() {
        this.btnStart = (Button)this.findViewById(R.id.btn_syncActivity_start);
        this.progressGeneral = (ProgressBar)this.findViewById(R.id.progress_syncActivity_general);
        this.progressSpecific = (ProgressBar)this.findViewById(R.id.progress_syncActivity_specific);
        this.lblGeneral = (TextView)this.findViewById(R.id.lbl_syncActivity_general);
        this.lblSpecific = (TextView)this.findViewById(R.id.lbl_syncActivity_specific);
        this.lblServerUrl = (TextView)this.findViewById(R.id.lbl_syncActivity_serverUrl);

        //this.lblServerUrl.setText(MyApp.getCurrent().getConfiguration().readServerAddress());
        this.lblServerUrl.setText(this.getResources().getString(R.string.label_smtp_server));

        this.btnStart.setOnClickListener(this);

        this.lblLastSync = (TextView)this.findViewById(R.id.lbl_syncActivity_lastSyncDate);

        Date targetDate = this.getLastSyncDate();
        this.displayLastSyncDate(targetDate);
    }

    private void displayLastSyncDate(Date targetDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.getString(R.string.format_datetime));
        String dateString = this.getString(R.string.label_never);
        if (targetDate.getTime() != 0) {
            dateString = dateFormat.format(targetDate);
        }
        this.lblLastSync.setText(this.getResources().getString(R.string.template_last_sync_date, dateString));
    }

    @Override
    public void onBackPressed() {
        if (this.isSyncInProgress) {
            Toast.makeText(this.getApplicationContext(), this.getString(R.string.message_wait_for_sync), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_syncActivity_start:
                this.startSync();
                break;
        }
    }

    private Date getLastSyncDate() {
        ConfigCollection configuration = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(ConfigCollection.class);
        if (configuration == null) {
            return new Date(0);
        } else {
            return new Date(configuration.getLastSynchronization());
        }
    }

    private void writeLastSyncDate(Date targetDate) {
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        ConfigCollection configuration = commonsDao.findOne(ConfigCollection.class);
        if (configuration == null) {
            configuration = new ConfigCollection();
            configuration.generateRandomId();
            configuration.setLastSynchronization(targetDate.getTime());
            commonsDao.insert(configuration, ConfigCollection.class);

        } else {
            configuration.setLastSynchronization(targetDate.getTime());
            commonsDao.update(configuration, ConfigCollection.class);
        }
    }

    private void startSync() {
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        if (currentUser == null) {
            Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.message_login_required), Toast.LENGTH_SHORT).show();
            return;
        }

        this.syncInformation = new HashMap<>();

        this.dateSyncStart = new Date();
        this.dateLastSync = this.getLastSyncDate();
        this.isSyncInProgress = true;
        this.btnStart.setEnabled(false);

        this.getSyncInformationFor(TouristicPlace.class);
        this.setSpecificProgress(0, 3);
    }

    private void getSyncInformationFor(Class<?> targetClass) {
        this.lblGeneral.setText(R.string.label_sync_getting_information);
        this.progressGeneral.setProgress(0);

        MyApp myApp = MyApp.getCurrent();
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        myApp.getAppRequests().getCommonRequests().processSyncRequest(0, currentUser, targetClass, this.dateLastSync, this);
    }

    private void setSpecificProgress(int numerator, int denominator) {
        String description = String.format("%s/%s", numerator, denominator);
        if (numerator == 0 && denominator == 0) {
            this.progressSpecific.setProgress(0);
        } else {
            this.progressSpecific.setProgress(100 * numerator / denominator);
        }
        this.lblSpecific.setText(description);
    }

    @Override
    public void OnSyncInformationComplete(Class<?> type, int requestIdentifier, CombinedSyncResults result) {
        if (type == TouristicPlace.class) {
            this.setSpecificProgress(1, 3);
            this.syncInformation.put(TouristicPlace.class, result);
            this.getSyncInformationFor(OldPicture.class);
        } else if (type == OldPicture.class) {
            this.setSpecificProgress(2, 3);
            this.syncInformation.put(OldPicture.class, result);
            this.getSyncInformationFor(ImageData.class);
        } else if (type == ImageData.class) {
            this.setSpecificProgress(3, 3);
            this.syncInformation.put(ImageData.class, result);
            this.startAllTransferences();
        }
    }

    private void startAllTransferences() {
        this.lblGeneral.setText(R.string.label_sync_processing_information);
        this.progressGeneral.setProgress(25);

        this.prepareInformation();

        this.lblGeneral.setText(R.string.label_sync_downloading_documents);
        this.progressGeneral.setProgress(50);

        this.setSpecificProgress(0, this.processedInformation.get(TouristicPlace.class).getModifications().size());

        if (!this.getNewChild(TouristicPlace.class)) {
            if (!this.getNewChild(OldPicture.class)) {
                if (!this.getNewChild(ImageData.class)) {
                    this.finalizeModifications();
                }
            }
        }
    }


    @Override
    public void OnComplete(ERequestType requestType, int requestIdentifier, MongoCollection result) {
        boolean existsMoreData;
        this.saveModification(result);
        existsMoreData = getNewChild(result.getClass());

        this.setSpecificProgress(
                this.processedInformation.get(result.getClass()).getCurrentModification(),
                this.processedInformation.get(result.getClass()).getModifications().size()
        );

        if (!existsMoreData) {
            if (result.getClass() == TouristicPlace.class) {
                if (!getNewChild(OldPicture.class)) {
                    if (!getNewChild(ImageData.class)) {
                        this.finalizeModifications();
                    }
                }
            } else if (result.getClass() == OldPicture.class) {
                if (!getNewChild(ImageData.class)) {
                    this.finalizeModifications();
                }
            } else if (result.getClass() == ImageData.class) {
                this.finalizeModifications();
            }
        }
    }

    private void finalizeModifications() {
        this.lblGeneral.setText(R.string.label_sync_removing_documents);
        this.progressGeneral.setProgress(75);
        this.applyDeletions();

        this.lblGeneral.setText(R.string.label_sync_completed);
        this.progressGeneral.setProgress(100);

        this.writeLastSyncDate(this.dateSyncStart);
        this.displayLastSyncDate(this.dateSyncStart);
    }

    private void applyDeletions() {
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        for(Map.Entry<Class<? extends MongoCollection>, ProcessedSyncResult> item : this.processedInformation.entrySet()) {
            Class<? extends MongoCollection> targetType = item.getKey();
            ProcessedSyncResult items = item.getValue();
            for(MongoCollection itemForDelete : items.getDeletions()) {
                commonsDao.remove(itemForDelete.getId(), targetType);
            }
        }

        this.isSyncInProgress = false;
        this.btnStart.setEnabled(true);
    }

    private void saveModification(MongoCollection result) {
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        MongoCollection existingOne = commonsDao.findOne(result.getId(), result.getClass());

        if (existingOne != null) {
            commonsDao.remove(existingOne.getId(), result.getClass());
        }

        if (result.getClass() == TouristicPlace.class) {
            commonsDao.insert((TouristicPlace) result, TouristicPlace.class);
        } else if (result.getClass() == OldPicture.class) {
            commonsDao.insert((OldPicture) result, OldPicture.class);
        } else if (result.getClass() == ImageData.class) {
            commonsDao.insert((ImageData) result, ImageData.class);
        }
    }

    @Override
    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
        this.cleanForError();
        Toast.makeText(this.getApplicationContext(), "Error in the synchronization", Toast.LENGTH_LONG).show();
    }

    private <T extends MongoCollection> boolean getNewChild(Class<T> targetType) {
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        CommonRequests commonRequests = MyApp.getCurrent().getAppRequests().getCommonRequests();

        ProcessedSyncResult syncResult =  this.processedInformation.get(targetType);

        if (syncResult.getCurrentModification() >= syncResult.getModifications().size()) {
            // All items were synced
            return false;
        }

        String currentDocumentId = syncResult.getModifications().get(syncResult.getCurrentModification()).getId();

        syncResult.setCurrentModification(syncResult.getCurrentModification() + 1);
        commonRequests.get(0, currentUser, currentDocumentId, targetType, this);

        return true;
    }

    private void prepareInformation() {
        this.processedInformation = new HashMap<>();

        this.addProcessedInformation(this.processedInformation, TouristicPlace.class);
        this.addProcessedInformation(this.processedInformation,OldPicture.class);
        this.addProcessedInformation(this.processedInformation, ImageData.class);

        //this.showPlanning();
    }

    /*
    private void showPlanning() {
        int modifications = 0;
        int deletions = 0;

        for(Map.Entry<Class<? extends MongoCollection>, ProcessedSyncResult> item : this.processedInformation.entrySet()) {
            modifications += item.getValue().getModifications().size();
            deletions += item.getValue().getDeletions().size();
        }

        //Toast.makeText(this.getApplicationContext(), "Modifications: " + modifications + "\nDeletions:" + deletions, Toast.LENGTH_SHORT).show();
    }
    */

    /**
     * Adds to the target map the combined information to sync
     * @param target
     * @param targetType
     * @param <T>
     */
    private <T extends MongoCollection> void addProcessedInformation(Map<Class<? extends MongoCollection>, ProcessedSyncResult> target, Class<T> targetType) {
        target.put(targetType, this.processCombinedList(this.syncInformation.get(targetType), targetType));
    }

    /**
     * Invokes the methods to combine and resume the modifications
     * @param combinedSyncResults
     * @param targetType
     * @param <T>
     * @return
     */
    private <T extends MongoCollection> ProcessedSyncResult processCombinedList(CombinedSyncResults combinedSyncResults, Class<T> targetType) {
        ProcessedSyncResult result = new ProcessedSyncResult();
        Map<String, MongoCollection> modifications = this.combineCreationsEditions(combinedSyncResults);
        modifications = this.cleanExistingModifications(modifications, targetType);
        result.setModifications(new ArrayList<>(modifications.values()));
        result.setDeletions(this.cleanExistingDeletions(combinedSyncResults.getDeletions(), targetType));
        return result;
    }

    private <T extends MongoCollection> List<MongoCollection> cleanExistingDeletions(List<MongoCollection> deletions, Class<T> targetType) {
        List<MongoCollection> result = new ArrayList<>();
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        for(MongoCollection document : deletions) {
            MongoCollection existingDocument = commonsDao.findOne(document.getId(), targetType);
            if (existingDocument != null) {
                result.add(document);
            }
        }
        return result;
    }

    /**
     * Cleans the existing modifications based in is existence and the createdAt and updatedAt fields
     * @param modifications
     * @param targetType
     * @param <T>
     * @return
     */
    private <T extends MongoCollection> Map<String, MongoCollection> cleanExistingModifications(Map<String, MongoCollection> modifications, Class<T> targetType) {
        Map<String, MongoCollection> result = new HashMap<>();
        CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
        MongoCollection serverDocument;

        for(Map.Entry<String, MongoCollection> item : modifications.entrySet()) {
            serverDocument = item.getValue();
            MongoCollection existingOne = commonsDao.findOne(item.getKey(), targetType);
            if (existingOne == null) {
                result.put(item.getKey(), item.getValue());
            } else {
                if (!this.areDatesEqual(serverDocument.getCreatedAt(), existingOne.getCreatedAt())
                        || !this.areDatesEqual(serverDocument.getUpdatedAt(), existingOne.getUpdatedAt())) {
                    result.put(item.getKey(), item.getValue());
                }
            }
        }

        return result;
    }

    /**
     * Dates comparison helper
     * @param a
     * @param b
     * @return
     */
    private boolean areDatesEqual(Date a, Date b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }
        return a.getTime() == b.getTime();
    }

    /**
     * Combines the creations with the editions
     * @param combinedSyncResults
     * @return
     */
    private Map<String, MongoCollection> combineCreationsEditions(CombinedSyncResults combinedSyncResults) {
        Map<String, MongoCollection> combined = new HashMap<>();

        for(MongoCollection document : combinedSyncResults.getCreations()) {
            combined.put(document.getId(), document);
        }

        for(MongoCollection document : combinedSyncResults.getEditions()) {
            if (!combined.containsKey(document.getId())) {
                combined.put(document.getId(), document);
            }
        }
        return combined;
    }

    /**
     * Callback for bad sync requests
     * @param type
     * @param requestIdentifier
     * @param volleyError
     * @param error
     */
    @Override
    public void OnSyncInformationError(Class<?> type, int requestIdentifier, VolleyError volleyError, Exception error) {
        this.cleanForError();
        Toast.makeText(this.getApplicationContext(), "Error getting the information!!!", Toast.LENGTH_SHORT).show();
    }

    private void cleanForError() {
        this.isSyncInProgress = false;
    }
}
