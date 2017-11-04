package com.tesis.yudith.showmethepast.view.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.AppBlocker;
import com.tesis.yudith.showmethepast.configuration.ILoginChangeListener;
import com.tesis.yudith.showmethepast.configuration.LoginProcessor;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

public class UserManagementFragment extends Fragment
        implements
            View.OnClickListener,
            LoginProcessor.ILoginProcessListener,
            INavigationChild,
            ILoginChangeListener {

    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    private static final String ARG_CLOSE_AT_FINISH_ID = "CLOSE_AT_FINISH_ID";

    private LinearLayout layoutSignOutAndDisconnect;
    private SignInButton btnSignIn;

    private ImageView imgProfilePicture;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private TextView txtUserRole;

    private Button btnSignOut;

    private LoginProcessor loginProcessor;

    public UserManagementFragment() {

    }

    public static UserManagementFragment newInstance(/*boolean closeAtFinish*/) {
        UserManagementFragment fragment = new UserManagementFragment();
        Bundle args = new Bundle();
        //args.putBoolean(ARG_CLOSE_AT_FINISH_ID, closeAtFinish);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            //this.closeAtFinish = this.getArguments().getBoolean(ARG_CLOSE_AT_FINISH_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        this.startView(view);

        LoginUserManager.getCurrent().addLoginChangeListener(this);

        if (currentUser == null) {
            //AppBlocker.loading();
            //this.loginProcessor.startLogin();
        } else {
            this.updateUI(true);
            this.displayLoginResults(currentUser);
        }

        return view;
    }

    private void startView(View view) {
        // Views
        this.layoutSignOutAndDisconnect = (LinearLayout)view.findViewById(R.id.sign_out_and_disconnect);
        this.btnSignIn = (SignInButton) view.findViewById(R.id.sign_in_button);
        this.btnSignOut = (Button)view.findViewById(R.id.sign_out_button);

        this.imgProfilePicture = (ImageView)view.findViewById(R.id.img_userManagement_profilePicture);
        this.txtUserName = (TextView)view.findViewById(R.id.txt_userManagement_userName);
        this.txtUserEmail = (TextView)view.findViewById(R.id.txt_userManagement_email);
        this.txtUserRole = (TextView)view.findViewById(R.id.txt_userManagement_role);

        this.btnSignIn.setOnClickListener(this);
        this.btnSignOut.setOnClickListener(this);

        this.loginProcessor = MyApp.getCurrent().getCurrentLoginProcessor();
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = loginProcessor.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.loginProcessor.destroy();
    }

    private void signOut() {
        AppBlocker.loading();
        this.loginProcessor.logout();
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            this.btnSignIn.setVisibility(View.GONE);
            this.layoutSignOutAndDisconnect.setVisibility(View.VISIBLE);
        } else {
            this.btnSignIn.setVisibility(View.VISIBLE);
            this.layoutSignOutAndDisconnect.setVisibility(View.GONE);

            this.txtUserName.setText(this.getResources().getString(R.string.label_no_logged_user_name));
            this.txtUserEmail.setText(this.getResources().getString(R.string.label_no_logged_user_email));
            this.imgProfilePicture.setImageResource(R.mipmap.ic_no_photo);
            this.txtUserRole.setText(this.getResources().getString(R.string.label_no_logged_user_name));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            this.loginProcessor.processSignInResult(result);
        }
    }

    private void completeLogin(UserInformation userInformation) {
        this.displayLoginResults(userInformation);

        if (userInformation != null) {
            LoginUserManager.getCurrent().updateLogin(userInformation);
            MyApp.getCurrent().getAppControllers().getUserController().storeUserResult(userInformation);
        } else {
            LoginUserManager.getCurrent().logout();
        }

        this.updateUI(userInformation != null);
        this.displayLoginResults(userInformation);
        AppBlocker.finished();
    }

    void displayLoginResults(UserInformation userInformation) {
        if (userInformation != null) {
            String imgUrl = userInformation.getPicture();
            String userName = userInformation.getName();
            String userEmail = userInformation.getEmail();
            String userRole = userInformation.getRole();

            Glide.with(this.getContext()).load(imgUrl)
                    //.thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(this.imgProfilePicture);

            this.txtUserName.setText(userName);
            this.txtUserEmail.setText(userEmail);
            this.txtUserRole.setText(userRole);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LoginUserManager.getCurrent().removeLoginChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                AppBlocker.loading();
                getIdToken();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    @Override
    public void onCompleteLogin(UserInformation userInformation) {
        this.completeLogin(userInformation);
    }

    @Override
    public void onGoogleConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this.getContext(), this.getResources().getString(R.string.error_message_google_login_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void loginChanged(boolean logout) {
        if (logout) {
            this.updateUI(false);
        } else {
            this.updateUI(true);
            this.displayLoginResults(LoginUserManager.getCurrent().getUserInformation());
        }
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_user_management);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }
}
