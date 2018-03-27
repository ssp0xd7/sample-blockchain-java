package com.blockchain.simple.demo.service.impl;

import java.security.MessageDigest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.blockchain.simple.demo.service.CryptoService;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
@Service("cryptoService")
public class CryptoServiceImpl implements CryptoService {
    @Override
    public String getSHA256(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            System.out.println("getSHA256 is error" + e.getMessage());
        }
        return encodeStr;
    }

    @Override
    public String getSHA256(String str, String nonce, long difficulty) {
        return null;
    }

    /**
     * byte to hex_str
     *
     * @param bytes
     * @return
     */
    private String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(300);
        String t;
        for (byte b: bytes) {
            t = Integer.toHexString(b & 0xFF);
            if (t.length() < 2) {
                sb.append("0");
            }
            sb.append(t);
        }
        return sb.toString();
    }
}
