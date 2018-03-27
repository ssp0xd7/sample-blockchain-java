package com.blockchain.simple.demo.service;

import java.util.List;
import com.blockchain.simple.demo.meta.Block;

/**
 * 区块service
 *
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
public interface BlockService {

    /**
     * 查询最后一个区块
     * 
     * @return
     */
    Block getLatestBlock();

    /**
     * 生成下一个区块
     * 
     * @param blockData
     * @return
     */
    Block generateNextBlock(String blockData);

    /**
     * 添加新区块
     * 
     * @param newBlock
     */
    void addBlock(Block newBlock);

    /**
     * 链替换
     * 
     * @param newBlocks
     */
    void replaceChain(List<Block> newBlocks);

    /**
     * 获取所有块
     */
    List<Block> getBlockChain();
}
