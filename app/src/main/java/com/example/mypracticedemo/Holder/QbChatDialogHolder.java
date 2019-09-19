package com.example.mypracticedemo.Holder;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QbChatDialogHolder {

   private static QbChatDialogHolder instance;
   private HashMap<String,QBChatDialog> qbChatDialogHashMap;


    public QbChatDialogHolder()
    {
        this.qbChatDialogHashMap = new HashMap<>();

    }
    public void putDialogs(List<QBChatDialog> dialogs)
    {
        for (QBChatDialog qbChatDialog:dialogs)
             putDialog(qbChatDialog);
    }

    public void putDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialogHashMap.put(qbChatDialog.getDialogId(),qbChatDialog);
    }

    public static synchronized QbChatDialogHolder getInstance()
   {
       QbChatDialogHolder qbChatDialogHolder;
       synchronized (QbChatDialogHolder.class)
       {
           if (instance == null)
           {
               instance = new QbChatDialogHolder();
           }
           qbChatDialogHolder = instance;
           return qbChatDialogHolder;
       }
   }

   public QBChatDialog getChatDialogById (String dialogId)
   {
       return qbChatDialogHashMap.get(dialogId);
   }

   public List<QBChatDialog>getChatDialogByIds(List<String> dialogs)
   {
       List<QBChatDialog> chatDialogs = new ArrayList<>();
       for (String id: dialogs)
       {
           QBChatDialog chatDialog = getChatDialogById(id);
           if (chatDialog != null)
                chatDialogs.add(chatDialog);
       }
       return chatDialogs;
   }

   public ArrayList<QBChatDialog> getAllChatDialogs()
   {
       ArrayList<QBChatDialog> qbChat = new ArrayList<>();
       for(String key:qbChatDialogHashMap.keySet())
           qbChat.add(qbChatDialogHashMap.get(key));

       return qbChat;
   }
}
