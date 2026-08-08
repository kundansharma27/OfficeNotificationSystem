package com.office.notification.model;

import java.io.Serializable;

public class ClientRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String computerName;
    private final String ipAddress;
    private final String userName;

    public ClientRegistration(String computerName, String ipAddress, String userName) {

        this.computerName = computerName;
        this.ipAddress = ipAddress;
        this.userName = userName;
    }

    public String getComputerName() {
        return computerName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserName() {
        return userName;
    }
}