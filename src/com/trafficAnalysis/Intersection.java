package com.trafficAnalysis;

import java.util.UUID;

public class Intersection {
    private final UUID uuid;
    private final Node inNorth;
    private final Node inEast;
    private final Node inSouth;
    private final Node inWest;
    private final Node outNorth;
    private final Node outEast;
    private final Node outSouth;
    private final Node outWest;

    Intersection(IntersectionBuilder builder){
        this.uuid = builder.uuidBuilder;
        this.inNorth = builder.inNorthBuilder;
        this.inEast = builder.inEastBuilder;
        this.inSouth = builder.inSouthBuilder;
        this.inWest = builder.inWestBuilder;
        this.outNorth = builder.outNorthBuilder;
        this.outEast = builder.outEastBuilder;
        this.outSouth = builder.outSouthBuilder;
        this.outWest = builder.outWestBuilder;
    }

    void simulate(){

    }

    public UUID getUuid(){ return uuid; }

    public Node getInNorth() {
        return inNorth;
    }

    public Node getInEast() {
        return inEast;
    }

    public Node getInSouth() {
        return inSouth;
    }

    public Node getInWest() {
        return inWest;
    }

    public Node getOutNorth() {
        return outNorth;
    }

    public Node getOutEast() {
        return outEast;
    }

    public Node getOutSouth() {
        return outSouth;
    }

    public Node getOutWest() {
        return outWest;
    }

    public static class IntersectionBuilder{
        private UUID uuidBuilder;
        private Node inNorthBuilder;
        private Node inEastBuilder;
        private Node inSouthBuilder;
        private Node inWestBuilder;
        private Node outNorthBuilder;
        private Node outEastBuilder;
        private Node outSouthBuilder;
        private Node outWestBuilder;

        public IntersectionBuilder(){
            uuidBuilder = UUID.randomUUID();
        }

        public IntersectionBuilder inN(Node inN){
            this.inNorthBuilder = inN;
            return this;
        }

        public IntersectionBuilder inE(Node inE){
            this.inEastBuilder = inE;
            return this;
        }

        public IntersectionBuilder inS(Node inS){
            this.inSouthBuilder = inS;
            return this;
        }

        public IntersectionBuilder inW(Node inW){
            this.inWestBuilder = inW;
            return this;
        }

        public IntersectionBuilder outN(Node outN){
            this.outNorthBuilder = outN;
            return this;
        }

        public IntersectionBuilder outE(Node outE){
            this.outEastBuilder = outE;
            return this;
        }

        public IntersectionBuilder outS(Node outS){
            this.outSouthBuilder = outS;
            return this;
        }

        public IntersectionBuilder outW(Node outW){
            this.outWestBuilder = outW;
            return this;
        }

        public Intersection build(){
            Intersection intersection = new Intersection(this);
            validateIntersection(intersection);
            return intersection;
        }

        private void validateIntersection(Intersection intersection){
            StringBuilder stringBuilder = new StringBuilder();


            if(stringBuilder.toString().equals("")){
                System.out.println("Intersection validated correctly");
            }
            else{
                System.out.println(stringBuilder.toString());
            }

        }
    }

}
