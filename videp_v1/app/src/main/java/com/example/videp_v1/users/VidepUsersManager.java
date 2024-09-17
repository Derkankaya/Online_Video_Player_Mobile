package com.example.videp_v1.users;

import java.util.HashMap;

public class VidepUsersManager {
//singleton class
    private static VidepUsersManager instance = new VidepUsersManager();

    private static HashMap<String, VidepUsersClass> videpUsersClassHashMap = new HashMap<>();
    private VidepUsersManager() {

    }

    public static synchronized VidepUsersManager getInstance()
    {
        return instance;
    }

    public VidepUsersClass getLoggedManager(String uuid) {
        return videpUsersClassHashMap.get(uuid);
    }

    public void setLoggedManager(String uuid, VidepUsersClass videpUsersClass) {
        videpUsersClassHashMap.put(uuid, videpUsersClass);
    }
}
