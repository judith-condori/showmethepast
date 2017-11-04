package com.tesis.yudith.showmethepast.domain;

public class CommonEnumerators {

    public enum  EEditionMode {
        MODE_CREATE,
        MODE_UPDATE_OR_VIEW
    }

    public enum EUserRole {
        ROLE_CLIENT(CommonConstants.USER_ROLE_CLIENT),
        ROLE_EDITOR(CommonConstants.USER_ROLE_EDITOR),
        ROLE_ADMIN(CommonConstants.USER_ROLE_ADMIN);

        String role;
        EUserRole(String role) {
            this.role = role;
        }

        EUserRole(int idx) {
            switch (idx) {
                default:
                case 0:
                    this.role = CommonConstants.USER_ROLE_CLIENT;
                    break;
                case 1:
                    this.role = CommonConstants.USER_ROLE_EDITOR;
                    break;
                case 2:
                    this.role = CommonConstants.USER_ROLE_ADMIN;
                    break;
            }
        }

        public String getValue() {
            return this.role;
        }
    }
}
