package com.trafficAnalysis;

import java.util.UUID;

public class Intersection {
    private final UUID uuid;
    private final Road inNorth;
    private final Road inEast;
    private final Road inSouth;
    private final Road inWest;
    private final Road outNorth;
    private final Road outEast;
    private final Road outSouth;
    private final Road outWest;

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

    public Road getInNorth() {
        return inNorth;
    }

    public Road getInEast() {
        return inEast;
    }

    public Road getInSouth() {
        return inSouth;
    }

    public Road getInWest() {
        return inWest;
    }

    public Road getOutNorth() {
        return outNorth;
    }

    public Road getOutEast() {
        return outEast;
    }

    public Road getOutSouth() {
        return outSouth;
    }

    public Road getOutWest() {
        return outWest;
    }

    public static class IntersectionBuilder{
        private final UUID uuidBuilder;
        private Road inNorthBuilder;
        private Road inEastBuilder;
        private Road inSouthBuilder;
        private Road inWestBuilder;
        private Road outNorthBuilder;
        private Road outEastBuilder;
        private Road outSouthBuilder;
        private Road outWestBuilder;

        public IntersectionBuilder(){
            uuidBuilder = UUID.randomUUID();
        }

        public IntersectionBuilder inN(Road inN){
            this.inNorthBuilder = inN;
            return this;
        }

        public IntersectionBuilder inE(Road inE){
            this.inEastBuilder = inE;
            return this;
        }

        public IntersectionBuilder inS(Road inS){
            this.inSouthBuilder = inS;
            return this;
        }

        public IntersectionBuilder inW(Road inW){
            this.inWestBuilder = inW;
            return this;
        }

        public IntersectionBuilder outN(Road outN){
            this.outNorthBuilder = outN;
            return this;
        }

        public IntersectionBuilder outE(Road outE){
            this.outEastBuilder = outE;
            return this;
        }

        public IntersectionBuilder outS(Road outS){
            this.outSouthBuilder = outS;
            return this;
        }

        public IntersectionBuilder outW(Road outW){
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
