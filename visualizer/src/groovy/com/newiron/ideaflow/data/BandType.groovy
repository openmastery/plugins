package com.newiron.ideaflow.data


enum BandType {
    Conflict("#ff0078","#ff4ca0"),
    Learning("#520ce8","#8654ef"),
    Rework("#ffcb01","#ffda4d"),
    Interval("#ffffff", "#ffffff")

    String color
    String highlight
    BandType(color, highlight) {
        this.color = color;
        this.highlight = highlight;
    }
}
