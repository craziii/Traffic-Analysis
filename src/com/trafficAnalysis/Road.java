package com.trafficAnalysis;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.UUID;

public class Road{

    Dictionary<UUID,Node> nodesInRoad;
    Node firstNode;

    public Road(int nodes){
        Node node = new Node();

        addNodeToRoad(node);
    }

    void findFirstNode(){
        Iterator<UUID> uuidIterator = nodesInRoad.keys().asIterator();
        while(uuidIterator.hasNext()){
            uuidIterator.next();
            Node temp = nodesInRoad.get(uuidIterator);
            if(temp.getNodeBefore() == null){
                firstNode = temp;
                break;
            }
        }
    }

    void addNodeToRoad(Node node){
        nodesInRoad.put(node.getUuid(),node);
    }

}
