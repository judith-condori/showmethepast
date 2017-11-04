package com.tesis.yudith.showmethepast.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.AppBlocker;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.domain.CommonEnumerators;
import com.tesis.yudith.showmethepast.domain.SearchUsersResult;
import com.tesis.yudith.showmethepast.domain.collections.UserInformation;
import com.tesis.yudith.showmethepast.requests.tools.ERequestType;
import com.tesis.yudith.showmethepast.requests.tools.IRequestListener;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

import java.util.ArrayList;
import java.util.List;

public class EditUsersFragment extends Fragment implements
        INavigationChild,
        View.OnClickListener,
        SearchView.OnQueryTextListener,
        IRequestListener<SearchUsersResult>,
        AdapterView.OnItemLongClickListener {

    public final static String TAG_FRAGMENT_EDIT_USERS = "TAG_FRAGMENT_EDIT_USERS";
    private final int REQUEST_SEARCH_USERS  = 0;
    private final int REQUEST_EDIT_USER  = 1;

    SearchView searchHint;
    ListView listViewUsers;
    List<UserInformation> currentUsersList;

    public EditUsersFragment() {
        // Required empty public constructor
    }

    public static EditUsersFragment newInstance() {
        EditUsersFragment fragment = new EditUsersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_users, container, false);
        this.linkControls(view);

        return view;
    }

    private void linkControls(View view) {
        this.searchHint = (SearchView) view.findViewById(R.id.search_editUsers_main);
        this.listViewUsers = (ListView) view.findViewById(R.id.list_editUsers_main);

        this.searchHint.setOnSearchClickListener(this);
        this.searchHint.setOnQueryTextListener(this);

        this.listViewUsers.setOnItemLongClickListener(this);
        this.searchInServer("");
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
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_edit_users);
    }

    @Override
    public String getFragmentTag() {
        return TAG_FRAGMENT_EDIT_USERS;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String hint) {
        this.searchInServer(hint);
        return false;
    }

    private void searchInServer(String hint) {
        AppBlocker.loading();
        UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        MyApp.getCurrent().getAppRequests().getAdminRequest().searchUsers(REQUEST_SEARCH_USERS, currentUser, hint, this);
    }

    @Override
    public void OnComplete(ERequestType requestType, int requestIdentifier, SearchUsersResult result) {
        if (this.getActivity() != null) {
            this.currentUsersList = result.getResult();
            this.updateList();
        }
        AppBlocker.finished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateList() {
        ArrayList<String> currentAdapterArray = new ArrayList<>();

        for(int i = 0; i < this.currentUsersList.size(); i++) {
            currentAdapterArray.add(this.currentUsersList.get(i).getEmail() + " [" + this.currentUsersList.get(i).getRole() + "]");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, currentAdapterArray);
        this.listViewUsers.setAdapter(arrayAdapter);
    }

    @Override
    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
        AppBlocker.finished();
        Toast.makeText(this.getActivity().getApplicationContext(), this.getString(R.string.error_message_server_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final UserInformation currentUser = LoginUserManager.getCurrent().getUserInformation();
        final UserInformation targetUser = currentUsersList.get(position);

        final String[] options = new String[]{
            this.getString(R.string.label_role_client),
            this.getString(R.string.label_role_editor),
            this.getString(R.string.label_role_admin),
        };
/*
        final CommonEnumerators.EUserRole[] roles = new CommonEnumerators.EUserRole[]{
            CommonEnumerators.EUserRole.ROLE_CLIENT,
            CommonEnumerators.EUserRole.ROLE_EDITOR,
            CommonEnumerators.EUserRole.ROLE_ADMIN,
        };
*/

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.getActivity());
        builderSingle.setTitle(R.string.title_information_list_options);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_single_choice);
        arrayAdapter.addAll(options);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AppBlocker.loading();

                CommonEnumerators.EUserRole targetRole = CommonEnumerators.EUserRole.values()[which];
                MyApp.getCurrent().getAppRequests().getAdminRequest().editUserRole(REQUEST_EDIT_USER, currentUser, targetUser.getId(), targetRole, new IRequestListener<Void>() {
                    @Override
                    public void OnComplete(ERequestType requestType, int requestIdentifier, Void result) {
                        AppBlocker.finished();
                        searchInServer(searchHint.getQuery().toString());
                    }

                    @Override
                    public void OnError(ERequestType requestType, int requestIdentifier, VolleyError volleyError, Exception error) {
                        AppBlocker.finished();
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_message_server_error), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        builderSingle.show();

        return false;
    }
}
