package com.blockchain.simple.demo.meta;

import java.io.Serializable;

/**
 * p2p 消息
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 8984846337653482102L;

    /**
     * msg type
     */
    private MsgType type;

    /**
     * data
     */
    private String data;

    public Message() {}

    public Message(MsgType type) {
        this.type = type;
    }

    public Message(MsgType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
