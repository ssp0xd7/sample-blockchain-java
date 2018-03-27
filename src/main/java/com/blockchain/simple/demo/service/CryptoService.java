package com.blockchain.simple.demo.service;

/**
 * 加密
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
public interface CryptoService {

    /**
     * 获取加密串
     *
     * @param str
     * @return
     */
    String getSHA256(String str);

    /**
     * 带有随机数，难度的加密，可以证明工作量
     *
     * @param str
     * @param nonce
     * @param difficulty
     * @return
     */
    // TODO: 23/01/2018 增加nonce、难度等属性
    String getSHA256(String str, String nonce, long difficulty);
}
