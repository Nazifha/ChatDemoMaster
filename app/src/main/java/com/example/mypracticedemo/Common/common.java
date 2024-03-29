package com.example.mypracticedemo.Common;

import com.example.mypracticedemo.Holder.QBUsersHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class common {
    public static final String DIALOG_EXTRA = "Dialogs";

    public static final String UPDATE_MODE = "mode";

    public static final String UPDATE_DIALOG_EXTRA = "chatDialogs";

    public static final String UPDATE_ADD_MODE = "add";

    public static final String UPDATE_REMOVE_MODE = "remove";


    public static String createChatDialogName(List<Integer> qbUsers) {
        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUserByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for (QBUser user : qbUsers1)
            name.append(user.getFullName()).append(" ");
        if (name.length() > 30)
            name = name.replace(30, name.length() - 1, "...");
        return name.toString();
    }

    public static boolean isNullOrEmptyString(String content) {
        return (content != null && !content.trim().isEmpty() ? false : true);
    }

}
