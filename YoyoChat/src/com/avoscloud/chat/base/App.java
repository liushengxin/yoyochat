package com.avoscloud.chat.base;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import com.avos.avoscloud.*;
import com.avoscloud.chat.avobject.*;
import com.avoscloud.chat.service.ChatService;
import com.avoscloud.chat.service.GroupService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.ui.activity.LoginActivity;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.chat.util.PhotoUtil;
import com.baidu.mapapi.SDKInitializer;
import com.avoscloud.chat.util.AVOSUtils;
import com.avoscloud.chat.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.*;

/**
 * Created by lzw on 14-5-29.
 */
public class App extends Application {
  public static final String DB_NAME = "chat.db3";
  public static final int DB_VER = 2;
  public static boolean debug = true;
  public static App ctx;
  public static Session session;
  private static Map<String, User> usersCache = new HashMap<String, User>();
  public static Map<String, ChatGroup> chatGroupsCache = new HashMap<String, ChatGroup>();
  List<User> friends = new ArrayList<User>();

  @Override
  public void onCreate() {
    super.onCreate();
    ctx = this;
    Utils.fixAsyncTaskBug();
    AVOSCloud.initialize(this, "15ykzm07ybn8iyzvgqwjkwvru1n6n1ik4dbxtm4ev2fpskyw",
            "4oe37qwlsc652gtluyzci95n6rf74y14aanakdb949xxz6xg");
    User.registerSubclass(User.class);
    User.registerSubclass(AddRequest.class);
    User.registerSubclass(ChatGroup.class);
    User.registerSubclass(Avatar.class);
    User.registerSubclass(UpdateInfo.class);

    AVInstallation.getCurrentInstallation().saveInBackground();
    PushService.setDefaultPushCallback(ctx, LoginActivity.class);
    //AVOSUtils.showInternalDebugLog();
    AVOSCloud.setDebugLogEnabled(true);
    if (App.debug) {
      Logger.level = Logger.VERBOSE;
    } else {
      Logger.level = Logger.NONE;
    }
    initImageLoader(ctx);
    initBaidu();
    openStrictMode();
  }

  public void openStrictMode() {
    if (App.debug) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
          .detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()   // or .detectAll() for all detectable problems
          .penaltyLog()
          .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
              //.penaltyDeath()
          .build());
    }
  }

  private void initBaidu() {
    SDKInitializer.initialize(this);
  }

  public static App getInstance() {
    return ctx;
  }

  /**
   * 初始化ImageLoader
   */
  public static void initImageLoader(Context context) {
    File cacheDir = StorageUtils.getOwnCacheDirectory(context,
        "leanchat/Cache");
    ImageLoaderConfiguration config = PhotoUtil.getImageLoaderConfig(context, cacheDir);
    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(config);
  }

  public static User lookupUser(String userId) {
    return usersCache.get(userId);
  }

  public static void registerUserCache(String userId, User user) {
    usersCache.put(userId, user);
  }

  public static void registerUserCache(User user) {
    registerUserCache(user.getObjectId(), user);
  }

  public static void registerBatchUserCache(List<User> users) {
    for (User user : users) {
      registerUserCache(user);
    }
  }

  public static ChatGroup lookupChatGroup(String groupId) {
    return chatGroupsCache.get(groupId);
  }

  public static void registerChatGroupsCache(List<ChatGroup> chatGroups) {
    for (ChatGroup chatGroup : chatGroups) {
      chatGroupsCache.put(chatGroup.getObjectId(), chatGroup);
    }
  }

  public List<User> getFriends() {
    return friends;
  }

  public void setFriends(List<User> friends) {
    this.friends = friends;
  }
}
