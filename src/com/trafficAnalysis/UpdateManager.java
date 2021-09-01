package com.trafficAnalysis;

import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

public class UpdateManager {
    Dictionary<UUID,Node> nodeDictionary;
    Dictionary<UUID,Road> roadDictionary;
    Dictionary<UUID,Intersection> intersectionDictionary;

    List<NodeMove> nodeMoveList;
    List<IntersectionMove> intersectionMoveList;

    public UpdateManager(){

    }

    void addNodeToDictionary(UUID uuid, Node node){
        nodeDictionary.put(uuid,node);
    }

    void addRoadToDictionary(UUID uuid, Road road){
        roadDictionary.put(uuid,road);
    }

    void addIntersectionToDictionary(UUID uuid, Intersection intersection){
        intersectionDictionary.put(uuid, intersection);
    }

    void updateCycle(){
        nodeMoveList.clear();
        intersectionMoveList.clear();
    }

    void runCycle(){

    }

    enum IntersectionMoveEnum {
        northToEast,
        northToSouth,
        northToWest,
        eastToSouth,
        eastToWest,
        eastToNorth,
        southToWest,
        southToNorth,
        southToEast,
        westToNorth,
        westToEast,
        westToSouth
    }

    enum NodeMoveEnum {
        noMove,
        move1,
        move2
    }

    void buildNodeMove(UUID node, NodeMoveEnum move){
        NodeMove nodeMove = new NodeMove(nodeDictionary.get(node), move);
        nodeMoveList.add(nodeMove);
    }

    void buildIntersectionMove(UUID intersection, IntersectionMoveEnum move){
        IntersectionMove intersectionMove = new IntersectionMove(intersectionDictionary.get(intersection), move);
        intersectionMoveList.add(intersectionMove);
    }

    private static class NodeMove {
        Node node;
        NodeMoveEnum move;

        NodeMove(Node n, NodeMoveEnum m){
            node = n;
            move = m;
        }
    }

    private static class IntersectionMove{
        Intersection intersection;
        IntersectionMoveEnum move;

        IntersectionMove(Intersection i, IntersectionMoveEnum m){
            intersection = i;
            move = m;
        }
    }
}
