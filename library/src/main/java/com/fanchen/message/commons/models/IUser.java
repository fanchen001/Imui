package com.fanchen.message.commons.models;


public interface IUser {

    /**
     * User id.
     * @return user id, unique
     */
    String getId();

    /**
     * Display name of user
     * @return display name
     */
    String getDisplayName();

    /**
     * Get user avatar file path.
     * @return avatar file path
     */
    String getAvatarFilePath();

}
