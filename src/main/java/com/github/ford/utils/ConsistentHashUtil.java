/**
 * qccr.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.github.ford.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * 一致性哈希工具类
 *
 * @author wgf
 * @version $$Id: ConsistentHashUtil, v 0.1 2016年07月28日 下午5:35 wgf Exp $$
 */
public class ConsistentHashUtil {

    /**
     * 节点列表
     *
     */
    private final List<String>                nodeList;

    /**
     *
     * 虚拟节点因子，虚拟节点数=实际节点数*replicateFactor
     */
    private int                               replicateFactor = 1;

    /**
     * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
     */
    private SortedMap<Integer, String> virtualNodes    = new TreeMap<Integer, String>();

    private final ReentrantReadWriteLock reentrantReadWriteLock=new ReentrantReadWriteLock();

    public ConsistentHashUtil(List<String> nodeList){
        this(nodeList,1);
    }

    public ConsistentHashUtil(List<String> nodeList, int replicateFactor) {
        if (nodeList == null || nodeList.size() == 0 || replicateFactor < 1) {
            throw new IllegalArgumentException("nodeList is null or replicateFactor<1.");
        }
        this.nodeList = new LinkedList<String>(nodeList);
        this.replicateFactor = replicateFactor;

        for (String node : nodeList) {
            addVirtualNode(node);
        }
    }

    /**
     * 添加一个新节点
     *
     * @param node
     */
    public void addNode(String node){
        reentrantReadWriteLock.writeLock().lock();
        try{

            if(node==null){
                throw new IllegalArgumentException("node is null.");
            }
            if(nodeList.contains(node)){
                return;
            }
            addVirtualNode(node);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * 删除一个节点
     *
     * @param node
     */
    public void removeNode(String node){
        reentrantReadWriteLock.writeLock().lock();
        try{
            if(nodeList.remove(node)){
                removeVirtualNode(node);
            }

        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * 得到应当路由到的结点
     */
    public String getNode(String key) {
        reentrantReadWriteLock.readLock().lock();
        try{
            // 得到带路由的结点的Hash值
            int hash = getHash(key);
            // 得到大于该Hash值的所有Map
            SortedMap<Integer, String> subMap = virtualNodes.tailMap(hash);
            // 第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            // 返回对应的虚拟节点名称，这里字符串稍微截取一下
            String virtualNode = subMap.get(i);
            return virtualNode.substring(0, virtualNode.indexOf("&&"));
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }

    }

    /**
     * 根据实际节点添加虚拟节点
     *
     * @param node
     */
    private void addVirtualNode(String node){
        for (int i = 0; i < replicateFactor; i++) {
            String virtualNodeName = node + "&&VN" + String.valueOf(i);
            int hash = getHash(virtualNodeName);
            virtualNodes.put(hash, virtualNodeName);
        }
    }

    /**
     * 根据实际节点删除虚拟节点
     *
     * @param node
     */
    private void removeVirtualNode(String node){
        for (int i = 0; i < replicateFactor; i++) {
            String virtualNodeName = node + "&&VN" + String.valueOf(i);
            int hash = getHash(virtualNodeName);
            virtualNodes.remove(hash, virtualNodeName);
        }
    }

    /**
     * 使用FNV1_32_HASH算法计算节点的Hash值
     */
    private int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

}
