package com.server.onlineup.common.constant;

public enum  RoomEnum implements BaseEnum {
    // Success enum
    CREATE_SUCCESS("Create room successfully", "create_success"),
    JOIN_SUCCESS("Join room successfully", "join_success"),
    UPDATE_SUCCESS("Update room successfully","update_success"),
    UPDATE_HOST_SUCCESS("Update host successfully", "update_host_success"),
    UPDATE_CO_HOST_SUCCESS("Update co-host successfully", "update_co_host_success"),

    // Error enum
    CREATE_FAIL("Fail to create room","create_fail"),
    JOIN_FAIL("Fail to join room","join_fail"),

    ROOM_NOT_EXIST("Room does not exist", "room_not_exist"),
    ROOM_NOT_START("Room have not started yet", "room_not_start"),
    ROOM_TIME_OUT("Room was ended", "room_time_out"),

    //
    USER_ALREADY_IN_ROOM("User has already be in room", "user_already_in_room"),
    USER_ALREADY_CO_HOST("User has already be an admin room", "user_already_co_host"),
    USER_NOT_EXIST("User does not exist","user_not_exist"),
    NOT_ROOM_ADMIN("Only room admin can do this action", "not_room_admin");

    RoomEnum(String desc, String descCode) {
        this.desc = desc;
        this.code = descCode;
    }

    private String desc;
    private String code;
    private String extra;

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getExtra() {
        return this.extra;
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }
}
