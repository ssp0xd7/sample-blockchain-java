package com.blockchain.simple.demo.service.impl;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blockchain.simple.demo.meta.Block;
import com.blockchain.simple.demo.service.BlockService;
import com.blockchain.simple.demo.service.CryptoService;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
@Service("blockService")
public class BlockServiceImpl implements BlockService {

    @Autowired
    private CryptoService cryptoService;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

    /**
     * block chain
     */
    private static List<Block> blockChain;

    @PostConstruct
    public void init() {
        blockChain = Collections.synchronizedList(new LinkedList<Block>());
        blockChain.add(this.getFristBlock());
    }

    @Override
    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    @Override
    public Block generateNextBlock(String blockData) {
        Block previousBlock = this.getLatestBlock();
        long nextIndex = previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis();
        String nextHash = this.calculateHash(nextIndex, previousBlock.getHash(), nextTimestamp, blockData);
        return new Block(nextIndex, previousBlock.getHash(), nextTimestamp, blockData, nextHash);
    }

    @Override
    public void addBlock(Block newBlock) {
        if (this.isValidNewBlock(newBlock, getLatestBlock())) {
            blockChain.add(newBlock);
        }
    }

    @Override
    public void replaceChain(List<Block> newBlocks) {
        if (this.checkBlocks(newBlocks) && newBlocks.size() > blockChain.size()) {
            blockChain = newBlocks;
        } else {
            logger.warn("Received blockchain invalid.");
        }
    }

    @Override
    public List<Block> getBlockChain() {
        return blockChain;
    }

    /**
     * 添加创世区块
     * 
     * @return
     */
    private Block getFristBlock() {
        return new Block(1, "0", System.currentTimeMillis(), "Hello Block",
            "aa212344fc10ea0a2cb885078fa9bc2354e55efc81be8f56b66e4a837157662e");
    }

    /**
     * 计算hash
     * 
     * @param index
     * @param previousHash
     * @param timestamp
     * @param data
     * @return
     */
    private String calculateHash(long index, String previousHash, long timestamp, String data) {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append(previousHash).append(timestamp).append(data);
        return cryptoService.getSHA256(sb.toString());
    }

    /**
     * 是否是有效的新区块
     * 
     * @param newBlock
     * @param previousBlock
     * @return
     */
    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
            System.out.println("invalid index");
            logger.warn("invalid index. preIndex:{},newIndex:{}", previousBlock.getIndex(), newBlock.getIndex());
            return false;
        } else if (!previousBlock.getHash().equals(newBlock.getPreHash())) {
            logger.warn("invalid previoushash. preHash:{},newBlockPreHash:{}", previousBlock.getHash(),
                newBlock.getPreHash());
            return false;
        } else {
            String hash = this.calculateHash(newBlock.getIndex(), newBlock.getPreHash(), newBlock.getTimestamp(),
                newBlock.getData());
            if (!hash.equals(newBlock.getHash())) {
                logger.warn("invalid hash. hash:{},newBlockHash:{}", hash, newBlock.getHash());
                return false;
            }
        }
        return true;
    }

    /**
     * 批量校验
     * 
     * @param newBlocks
     * @return
     */
    private boolean checkBlocks(List<Block> newBlocks) {
        Block fristBlock = newBlocks.get(0);
        if (fristBlock.equals(this.getFristBlock())) {
            //相同的区块链
            return false;
        }
        //验证每一个区块的有效性
        for (Block newBlock: newBlocks) {
            if (this.isValidNewBlock(newBlock, fristBlock)) {
                fristBlock = newBlock;
            } else {
                return false;
            }
        }
        return true;
    }
}
