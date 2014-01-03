package com.newiron.ideaflow.data


enum BandType {
    Conflict(255,0,120),
    Learning(82,12,232),
    Rework(255,203,1),
    Interval(255,255,255)

    String color
    String highlight
    BandType(r, g, b) {
        color = "rgba($r,$g,$b,1)"
        highlight = "rgba($r,$g,$b,.6)"
    }
}
