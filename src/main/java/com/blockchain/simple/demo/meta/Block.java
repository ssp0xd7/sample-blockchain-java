package com.blockchain.simple.demo.meta;

/**
 * 区块结构，去掉了nonce，难度，交易等属性
 *
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
public class Block implements Bean {
    private static final long serialVersionUID = -3949928276618945162L;

    /**
     * index
     */
    private long index;

    /**
     * 上一区块hash
     */
    private String preHash;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * data
     */
    private String data;

    /**
     * 当前区块hash
     */
    private String hash;

    public Block() {}

    public Block(long index, String preHash, long timestamp, String data, String hash) {
        this.index = index;
        this.preHash = preHash;
        this.timestamp = timestamp;
        this.data = data;
        this.hash = hash;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
