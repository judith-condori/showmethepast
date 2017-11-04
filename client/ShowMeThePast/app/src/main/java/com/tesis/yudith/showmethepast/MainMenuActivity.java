package com.tesis.yudith.showmethepast;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.tesis.yudith.showmethepast.aservices.AlertsByPositionService;
import com.tesis.yudith.showmethepast.configuration.AppBlocker;
import com.tesis.yudith.showmethepast.configuration.ILoginChangeListener;
import com.tesis.yudith.showmethepast.configuration.LoginProcessor;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.tools.AndroidServiceTools;
import com.tesis.yudith.showmethepast.view.AboutFragment;
import com.tesis.yudith.showmethepast.view.EditUsersFragment;
import com.tesis.yudith.showmethepast.view.OldPictureEditionFragment;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;
import com.tesis.yudith.showmethepast.view.navigation.INavigationManager;
import com.tesis.yudith.showmethepast.view.MainMapFragment;
import com.tesis.yudith.showmethepast.view.SensorsStatusFragment;
import com.tesis.yudith.showmethepast.view.SettingsFragment;
import com.tesis.yudith.showmethepast.view.TouristicPlaceEditionFragment;
import com.tesis.yudith.showmethepast.view.TouristicPlacesFragment;
import com.tesis.yudith.showmethepast.view.navigation.NavigationStack;
import com.tesis.yudith.showmethepast.view.user.UserManagementFragment;

public class MainMenuActivity extends AppCompatActivity implements
            NavigationView.OnNavigationItemSelectedListener,
            ILoginChangeListener,
            AppBlocker.IOnBlockListener,
            TouristicPlaceEditionFragment.OnTouristicPlaceEditionEvents,
            LoginProcessor.ILoginProcessListener,
            INavigationManager {

    private static int REQUEST_SYNC_DATA_ACTIVITY = 456;

    ImageView imageUserProfile;
    TextView txtUserName;
    TextView txtUserEmail;
    ConstraintLayout layoutBlocker;
    NavigationView navigationView;
    LoginProcessor loginProcessor;
    NavigationStack navigationStack;
    boolean loginAlreadyStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.navigationStack = new NavigationStack();

        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);

        this.navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        this.imageUserProfile = (ImageView)headerView.findViewById(R.id.img_mainMenu_userAvatar);
        this.txtUserName = (TextView)headerView.findViewById(R.id.txt_mainMenu_userName);
        this.txtUserEmail = (TextView)headerView.findViewById(R.id.txt_mainMenu_userEmail);
        this.layoutBlocker = (ConstraintLayout)this.findViewById(R.id.layout_main_activity_blocker);

        if (!this.loginAlreadyStarted) {
            this.loginAlreadyStarted = true;
            this.registerLoginListener();
            AppBlocker.setListener(this);
            this.startLoginProcessor();
        }

        if (!this.restoreByNotification(this.getIntent())) {
            this.loadFragment(MainMapFragment.newInstance());
        }

        this.filterNavigationItems();
    }

    private void filterNavigationItems() {
        boolean isAdmin =  LoginUserManager.getCurrent().isCurrentUserAnAdmin();
        Menu targetMenu = this.navigationView.getMenu();
        targetMenu.findItem(R.id.nav_menu_edit_users).setVisible(isAdmin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.loginProcessor.destroy();
        if (AndroidServiceTools.isMyServiceRunning(AlertsByPositionService.class)) {
            AlertsByPositionService.stopService();
        }
    }

    private boolean restoreByNotification(Intent intent) {

        String documentId = intent.getStringExtra(AlertsByPositionService.NOTIFICATION_DOCUMENT_ID);
        String documentType = intent.getStringExtra(AlertsByPositionService.NOTIFICATION_DOCUMENT_TYPE);
        String documentMode = intent.getStringExtra(AlertsByPositionService.NOTIFICATION_DOCUMENT_MODE);

        if (documentId == null) {
            return false;
        }

        //Toast.makeText(this, documentId, Toast.LENGTH_SHORT).show();

        if (documentMode.equals(AlertsByPositionService.MODE_OPEN_IN_MAP)) {
            if (documentType.equals(MongoCollection.getServerCollectionName(TouristicPlace.class))) {
                MainMapFragment mainMapFragment = MainMapFragment.newInstanceForTouristicPlace(documentId);
                this.loadFragment(mainMapFragment);
            } else if (documentType.equals(MongoCollection.getServerCollectionName(OldPicture.class))) {
                MainMapFragment mainMapFragment = MainMapFragment.newInstanceForOldPicture(documentId);
                this.loadFragment(mainMapFragment);
            }
        } else {
            if (documentType.equals(MongoCollection.getServerCollectionName(TouristicPlace.class))) {
                TouristicPlaceEditionFragment touristicPlaceEditionFragment = TouristicPlaceEditionFragment.newInstanceForViewUpdate(documentId);
                this.loadFragment(touristicPlaceEditionFragment);
            } else if (documentType.equals(MongoCollection.getServerCollectionName(OldPicture.class))) {
                OldPicture targetOldPicture = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(documentId, OldPicture.class);
                OldPictureEditionFragment oldPictureEditionFragment = OldPictureEditionFragment.newInstanceForUpdate(targetOldPicture.getTouristicPlace(), documentId);
                this.loadFragment(oldPictureEditionFragment);
            }
        }

        return true;
    }

    private void startLoginProcessor() {
        this.loginProcessor = /*LoginProcessor.getNewLoginProcessor(this, this); */ new LoginProcessor(this, this);
        MyApp.getCurrent().setCurrentLoginProcessor(this.loginProcessor);
        this.loginProcessor.startLogin();
    }

    private void registerLoginListener() {
        LoginUserManager.getCurrent().addLoginChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void loadFragment(INavigationChild targetFragment) {
        this.replaceFragment(targetFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            //case R.id.action_user_information:
            //    this.loadFragment(UserManagementFragment.newInstance());
            //    return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_menu_my_account:
                this.loadFragment(UserManagementFragment.newInstance());
                break;
            case R.id.nav_menu_edit_users:
                this.loadFragment(EditUsersFragment.newInstance());
                break;
            case R.id.nav_menu_map:
                this.loadFragment(MainMapFragment.newInstance());
                break;
            case R.id.na_menu_places:
                this.loadFragment(TouristicPlacesFragment.newInstance());
                break;
            case R.id.nav_menu_sensors:
                this.loadFragment(SensorsStatusFragment.newInstance());
                break;
            case R.id.nav_menu_settings:
                this.loadFragment(SettingsFragment.newInstance());
                break;
            case R.id.nav_menu_synchronization:
                //this.loadFragment(SynchronizationFragment.newInstance());
                this.actionSynchronization();
                break;
            case R.id.nav_menu_about:
                this.loadFragment(AboutFragment.newInstance());
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void actionSynchronization() {
        Intent myIntent = new Intent(this, SyncDataActivity.class);
        this.startActivityForResult(myIntent, REQUEST_SYNC_DATA_ACTIVITY);
    }

    @Override
    public void loginChanged(boolean logout) {
        UserInformation currentUserInformation = LoginUserManager.getCurrent().getUserInformation();

        if (logout) {
            this.txtUserName.setText(this.getResources().getString(R.string.label_no_logged_user_name));
            this.txtUserEmail.setText(this.getResources().getString(R.string.label_no_logged_user_email));
            this.imageUserProfile.setImageResource(R.mipmap.ic_no_photo);
        } else {

            Glide.with(this.getApplicationContext()).load(currentUserInformation.getPicture())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(160, 160)
                    .into(this.imageUserProfile);

            this.txtUserName.setText(currentUserInformation.getName());
            this.txtUserEmail.setText(currentUserInformation.getEmail());
        }

        this.filterNavigationItems();
    }

    @Override
    public void onAppBlockerChange(boolean visible) {
        if (visible) {
            this.layoutBlocker.setVisibility(View.VISIBLE);
            this.layoutBlocker.setClickable(true);
        } else {
            this.layoutBlocker.setVisibility(View.GONE);
            this.layoutBlocker.setClickable(false);
            this.navigationView.bringToFront();
        }
    }

    @Override
    public void onCompleteLogin(UserInformation userInformation) {
        if (userInformation == null) {
            LoginUserManager.getCurrent().logout();
        } else {
            MyApp.getCurrent().getAppControllers().getUserController().storeUserResult(userInformation);
            LoginUserManager.getCurrent().updateLogin(userInformation);
        }
        AppBlocker.finished();
        this.filterNavigationItems();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.closeAllNotification();

        this.restoreByNotification(intent);
    }

    void closeNotificationByID(int notificationID) {
        if (Context.NOTIFICATION_SERVICE != null) {
            String NS = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext()
                    .getSystemService(NS);
            nMgr.cancel(notificationID);
        }

    }

    void closeAllNotification() {
        if (Context.NOTIFICATION_SERVICE != null) {
            NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        }
    }

    @Override
    public void onGoogleConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this.getApplicationContext(), this.getResources().getString(R.string.error_message_google_login_error), Toast.LENGTH_LONG).show();
        AppBlocker.finished();
        this.filterNavigationItems();
    }

    @Override
    public void onTouristicPlaceEditionFinished(boolean needsUpdate) {
        TouristicPlacesFragment touristicPlacesFragment = (TouristicPlacesFragment)
                getSupportFragmentManager().findFragmentByTag(TouristicPlacesFragment.FRAGMENT_TAG_TOURISTIC_PLACES);
        if (touristicPlacesFragment != null) {
            touristicPlacesFragment.onTouristicPlaceEditionFinished(needsUpdate);
        } else {
            //Toast.makeText(this.getApplicationContext(), "The fragment is not loaded yet.", Toast.LENGTH_LONG).show();
        }
        this.filterNavigationItems();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.navigationStack.isEmpty() || this.navigationStack.isLastOne()) {
                this.confirmBackPressed();//super.onBackPressed();
            } else {
                this.popFragment(false);
            }
        }
    }

    private void confirmBackPressed() {
        AlertDialog dialog =
            new AlertDialog.Builder(this)
                .setTitle(this.getResources().getString(R.string.label_confirmation))
                .setMessage(this.getResources().getString(R.string.question_for_application_in_background))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainMenuActivity.super.onBackPressed();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public void replaceFragment(INavigationChild targetFragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

        if (targetFragment.getFragmentTag() != null) {
            ft.replace(R.id.main_container, (Fragment)targetFragment, targetFragment.getFragmentTag()).commit();
        } else {
            ft.replace(R.id.main_container, (Fragment) targetFragment).commit();
        }

        this.navigationStack.replaceChildren(targetFragment);
        this.setTitle(targetFragment.getNavigationTitle(this.getResources()));
    }

    @Override
    public void pushFragment(INavigationChild targetFragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        if (targetFragment.getFragmentTag() != null) {
            ft.add(R.id.main_container, (Fragment)targetFragment, targetFragment.getFragmentTag()).commit();
        } else {
            ft.add(R.id.main_container, (Fragment) targetFragment).commit();
        }
        this.navigationStack.pushChildren(targetFragment);
        this.setTitle(targetFragment.getNavigationTitle(this.getResources()));
    }

    @Override
    public void popFragment(boolean needsReload) {
        if (!this.navigationStack.isEmpty() && !this.navigationStack.isLastOne()) {
            INavigationChild currentChildren = this.navigationStack.popChildren();
            INavigationChild parentChildren = this.navigationStack.peekChildren();

            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            ft.remove((Fragment) currentChildren).commit();

            parentChildren.onChildrenClosed(currentChildren, needsReload);

            this.setTitle(parentChildren.getNavigationTitle(this.getResources()));
        }
    }
}
