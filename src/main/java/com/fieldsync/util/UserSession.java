package com.fieldsync.util;

import com.fieldsync.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession(User user){
        this.currentUser = user;
    }

    public static void login(User user){
        instance = new UserSession(user);
    }

    public static UserSession getInstance(){
        return instance;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public static User getLoggedInUser() {
        return (instance != null) ? instance.getCurrentUser() : null;
    }

    public static User getUser() {
        return getLoggedInUser();
    }

    public static void logout(){
        instance = null;
    }

    public static boolean isLoggedIn(){
        return instance != null;
    }
}