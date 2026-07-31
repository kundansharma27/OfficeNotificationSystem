package com.office.notification.protocol;

import java.io.Serializable;

public class Packet implements Serializable {

    private  static  final long serialVersionUID = 1L;

    private  final  PacketType type;
    private final Object payload;

    public Packet(PacketType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public PacketType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
