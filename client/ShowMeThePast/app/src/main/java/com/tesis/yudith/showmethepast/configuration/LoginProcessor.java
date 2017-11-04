package com.tesis.yudith.showmethepast.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.helpers.VolleyErrorTools;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;

public class LoginProcessor implements GoogleApiClient.OnConnectionFailedListener {

    public interface ILoginProcessListener {
        void onCompleteLogin(UserInformation userInformation);
        void onGoogleConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    private final int REQUEST_ID_LOGIN = 0;

    private FragmentActivity targetActivity;
    private GoogleApiClient googleApiClient;

    private ILoginProcessListener loginProcessListener;

    /*
    private static LoginProcessor currentProcessor;

    public static synchronized LoginProcessor getNewLoginProcessor(FragmentActivity targetActivity, ILoginProcessListener loginProcessListenner) {
        if (currentProcessor  == null) {
            currentProcessor = new LoginProcessor(targetActivity, loginProcessListenner);
        }
        return currentProcessor;
    }
*/

    public LoginProcessor(FragmentActivity targetActivity, ILoginProcessListener loginProcessListenner) {
        this.targetActivity = targetActivity;
        this.loginProcessListener = loginProcessListenner;
        this.googleApiClient = this.getGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //String errorMessage = this.targetActivity.getResources().getString(R.string.error_message_google_login_error);
        //AppBlocker.finished();
        //Toast.makeText(targetActivity.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        if (this.loginProcessListener != null) {
            this.loginProcessListener.onGoogleConnectionFailed(connectionResult);
        }
    }

    private GoogleApiClient getGoogleApiClient() {
        GoogleSignInOptions googleOptions = LoginUserManager.getCurrent().getGoogleConfiguration(this.targetActivity);
        return new GoogleApiClient.Builder(targetActivity)
                .enableAutoManage(targetActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleOptions)
                .build();
    }

    public void startLogin() {
        final LoginProcessor self = this;
        UserInformation lastUser = MyApp.getCurrent().getAppControllers().getUserController().getLastUser();

        // User doesn't exists, we need a complete login process
        if (lastUser == null) {
            this.newCompleteLogin();
            return;
        }

        AppBlocker.loading();

        MyApp.getCurrent().getAppRequests().getUserRequests().getStatus(lastUser, new IRequestListener<UserInformation>() {
            @Override
            public void OnComplete(ERequestType requestType, int requestIdentifier, UserInformation serverUser) {
                MyApp.getCurrent().getAppControllers().getUserController().storeUserResult(serverUser);

                // If token expired then we need to do a complete login process
                if (!serverUser.getSmtpToken().isAlive()) {
                    self.newCompleteLogin();
                } else {
                    Auth.GoogleSignInApi.silentSignIn(googleApiClient);
                    self.completeLogin(serverUser);
                }
            }

            @Override
            public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                // If the user doesn't exists for the server we need to do a complete login
                if (VolleyErrorTools.isHttpNotFound(volleyError)) {
                    self.newCompleteLogin();
                } else {
                    self.completeLogin(null);
                    Toast.makeText(targetActivity.getApplicationContext(), targetActivity.getResources().getString(R.string.error_message_server_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void completeLogin(UserInformation userInformation) {
        if (this.loginProcessListener != null) {
            Auth.GoogleSignInApi.silentSignIn(this.googleApiClient);
            this.loginProcessListener.onCompleteLogin(userInformation);
        }
    }

    private void newCompleteLogin() {
        //MyApp.getCurrent().getAppControllers().getUserController().removeAll();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(this.googleApiClient);

        if (opr.isDone()) {
            // Users cached credentials are valid, GoogleSignInResult containing ID token
            // is available immediately. This likely means the current ID token is already
            // fresh and can be sent to your server.
            GoogleSignInResult result = opr.get();
            this.processSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently and get a valid
            // ID token. Cross-device single sign on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    processSignInResult(result);
                }
            });
        }
    }

    public void processSignInResult(GoogleSignInResult result) {
        final LoginProcessor self = this;
        if (result.isSuccess()) {
            String idToken = result.getSignInAccount().getIdToken();

            MyApp.getCurrent().getAppRequests().getUserRequests().loginUser(idToken, new IRequestListener<UserInformation>() {
                @Override
                public void OnComplete(ERequestType requestType, int requestIdentifier,UserInformation userInformation) {
                    self.completeLogin(userInformation);
                }

                @Override
                public void OnError(ERequestType requestType, int requestIdentifier,VolleyError volleyError, Exception error) {
                    self.completeLogin(null);
                    Toast.makeText(self.targetActivity.getApplicationContext(), self.targetActivity.getResources().getString(R.string.error_message_server_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            self.completeLogin(null);
            //Toast.makeText(self.targetActivity.getApplicationContext(), self.targetActivity.getResources().getString(R.string.error_message_google_login_error), Toast.LENGTH_LONG).show();
        }
    }

    public Intent getSignInIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient);
    }

    private void waitForGoogleSignInApi() {
        try {
            Auth.GoogleSignInApi.silentSignIn(this.googleApiClient).wait();
            //Auth.GoogleSignInApi.wait();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void logout() {
        Log.i("smtp", "logout invoke");
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();

        Auth.GoogleSignInApi.signOut(this.googleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (currentUser != null) {
                        MyApp.getCurrent().getAppRequests().getUserRequests().logout(currentUser, new IRequestListener<Void>() {
                            @Override
                            public void OnComplete(ERequestType requestType, int requestIdentifier,Void result) {
                                completeLogin(null);
                            }

                            @Override
                            public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                                Toast.makeText(targetActivity.getApplicationContext(), targetActivity.getResources().getString(R.string.error_message_server_error), Toast.LENGTH_LONG).show();
                                completeLogin(null);
                            }
                        });
                    } else {
                        completeLogin(null);
                    }
                }
            });
    }

    public void destroy() {
        this.googleApiClient.stopAutoManage(this.targetActivity);
    }
}
