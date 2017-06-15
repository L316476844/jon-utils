package org.jon.lv.loadbalance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Package org.jon.lv.loadbalance.IpHashRobin
 * @Description: 负载均衡-hash算法
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 14:20
 * version V1.0.0
 */
public class IpHashRobin {

    static Map<String,Integer> serverWeigthMap  = new HashMap<String,Integer>();
    static{
        serverWeigthMap.put("192.168.1.12", 1);
        serverWeigthMap.put("192.168.1.13", 1);
        serverWeigthMap.put("192.168.1.14", 2);
        serverWeigthMap.put("192.168.1.15", 2);
        serverWeigthMap.put("192.168.1.16", 3);
        serverWeigthMap.put("192.168.1.17", 3);
        serverWeigthMap.put("192.168.1.18", 1);
        serverWeigthMap.put("192.168.1.19", 2);
    }

    /**
     * 获取请求服务器地址
     * @param remoteIp 负载均衡服务器ip
     * @return
     */
    public static String ipHash(String remoteIp)
    {
        //重新建立一個map,避免出現由於服務器上線和下線導致的並發問題
        Map<String,Integer> serverMap  = new HashMap<String,Integer>();
        serverMap.putAll(serverWeigthMap);
        //獲取ip列表list
        Set<String> keySet = serverMap.keySet();
        ArrayList<String> keyList = new ArrayList<String>();
        keyList.addAll(keySet);

        int hashCode = remoteIp.hashCode();
        int serverListSize = keyList.size();
        int serverPos = Math.abs(hashCode % serverListSize);

        return keyList.get(serverPos);
    }

    public static void main(String[] args) {
        String serverIp = ipHash("10.1.90.216");
        System.out.println(serverIp);
    }
}
