package com.tesis.yudith.showmethepast.requests.tools;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tesis.yudith.showmethepast.requests.AdminRequests;
import com.tesis.yudith.showmethepast.requests.CommonRequests;
import com.tesis.yudith.showmethepast.requests.UserRequests;

public class ApplicationRequests {
    private UserRequests userRequests;
    private CommonRequests commonRequests;
    private AdminRequests adminRequest;

    public ApplicationRequests(Context context, String serverUrlBase) {
        RequestQueue requestQueue = SharedRequestQueue.getCurrent(context);

        this.userRequests = new UserRequests(requestQueue, serverUrlBase);
        this.commonRequests = new CommonRequests(requestQueue, serverUrlBase);
        this.adminRequest = new AdminRequests(requestQueue, serverUrlBase);
    }

    public UserRequests getUserRequests() {
        return this.userRequests;
    }
    public CommonRequests getCommonRequests() {
        return this.commonRequests;
    }

    public AdminRequests getAdminRequest() {
        return adminRequest;
    }

    public void setAdminRequest(AdminRequests adminRequest) {
        this.adminRequest = adminRequest;
    }
}
