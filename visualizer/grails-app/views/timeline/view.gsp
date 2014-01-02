<%--
  Created by IntelliJ IDEA.
  User: katrea
  Date: 12/31/13
  Time: 7:08 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <g:javascript library="monolith"/>
    <r:layoutResources/>
</head>

<body>
<div id="timelineHolder">
</div>

<g:javascript>

    function refreshTimeline() {
        $.ajax({
            type: 'GET',
            url: '/visualizer/timeline/showTimeline',
            success: drawTimeline,
            error: handleError
        });
    }

    function handleError(XMLHttpRequest, textStatus, errorThrown) {
        alert(textStatus + ":" + errorThrown);
    }


    var sideMargin = 40;
    var bottomMargin = 30;
    var bandMargin = 20;
    var topMargin = bottomMargin;
    var height = 180;
    var width = 800;

    function drawTimeline(data) {
        var stage = new Kinetic.Stage({
            container: 'timelineHolder',
            width: width,
            height: height
        });

        var secondsPerUnit = data.end.offset / (width - (2 * sideMargin));
        drawEventsLayer(stage, data.events, secondsPerUnit);
        drawTimebandsLayer(stage, data.timeBands, secondsPerUnit);
        drawMainTimeline(stage, data);
        drawWindow(stage);
        alert('done');
    }

    function drawWindow(stage) {
        var windowScale = 5;
        var strokeWidth = 3;
        var windowWidth = (width - (sideMargin * 2)) / 5;
        var layer = new Kinetic.Layer();
        var rect = new Kinetic.Rect({
            x: sideMargin,
            y: topMargin + bandMargin - windowScale,
            width: windowWidth,
            height: height - bottomMargin - topMargin - bandMargin + windowScale*2,
            fill: "rgba(255,255,0, .1)",
            stroke: "rgba(30,255,30, 1)",
            strokeWidth: strokeWidth,
            draggable: true,
            dragBoundFunc: function(pos) {
                var newX = pos.x;
                var newY = pos.y;
                if (newX < sideMargin) {
                    newX = sideMargin;
                } else if (newX > (width - windowWidth - sideMargin)) {
                    newX = (width - windowWidth - sideMargin);
                }
                return {
                    x: newX,
                    y: this.getAbsolutePosition().y
                }
            }
        });

        rect.on('mouseover touchstart', function () {
            this.setFill(highlight);
            layer.draw();
        });

        rect.on('mouseout touchend', function () {
            this.setFill(color);
            layer.draw();
        });

        layer.add(rect);
        stage.add(layer);
    }

    function drawMainTimeline(stage, data) {
        var layer = new Kinetic.Layer();
        var tickHeight = 10;
        var tickMargin = 5;
        var startTickLabel = new Kinetic.Text({
            x: sideMargin - tickMargin,
            y: height - bottomMargin,
            text: data.start.shortTime,
            fontSize: 13,
            align: 'right',
            fontFamily: 'Calibri',
            fill: 'black'
        });
        startTickLabel.setOffset({x: startTickLabel.getWidth()});

        var endTickLabel = new Kinetic.Text({
            x: width - sideMargin + tickMargin,
            y: height - bottomMargin,
            text: data.end.shortTime,
            fontSize: 13,
            fontFamily: 'Calibri',
            fill: 'black'
        });

        layer.add(createMainLine(tickHeight));
        layer.add(startTickLabel);
        layer.add(endTickLabel);
        stage.add(layer);
    }

    function createMainLine(tickHeight) {
        return new Kinetic.Line({
            points: [
                [sideMargin, height - bottomMargin + tickHeight],
                [sideMargin, height - bottomMargin],
                [width - sideMargin, height - bottomMargin],
                [width - sideMargin, height - bottomMargin + tickHeight]
            ],
            stroke: 'black',
            strokeWidth: 3,
            lineCap: 'square',
            lineJoin: 'round'
        });
    }

    function drawEventsLayer(stage, events, secondsPerUnit) {
        var layer = new Kinetic.Layer();
        for (var i = 0; i < events.length; i++) {
            drawEvent(layer, events[i], secondsPerUnit);
        }
        stage.add(layer);
    }
    function drawEvent(layer, event, secondsPerUnit) {
        var offset = Math.round(event.offset / secondsPerUnit) + sideMargin;
        var tickHeight = 15;
        var tickMargin = 3;

        var eventLine = new Kinetic.Line({
            points: [
                [offset, topMargin],
                [offset, height - bottomMargin + tickHeight]
            ],
            stroke: 'gray',
            strokeWidth: 2,
            lineCap: 'square'
        });

        var tickLabel = new Kinetic.Text({
            x: offset,
            y: height - tickHeight + tickMargin,
            text: event.shortTime,
            align: 'center',
            fontSize: 13,
            fontFamily: 'Calibri',
            fill: 'black'
        });

        tickLabel.setOffset({x: tickLabel.getWidth() / 2});

        layer.add(eventLine);
        layer.add(tickLabel);

    }

    function drawTimebandsLayer(stage, bands, secondsPerUnit) {
        var layer = new Kinetic.Layer();
        for (var i = 0; i < bands.length; i++) {
            drawTimeband(layer, bands[i], secondsPerUnit);
        }
        stage.add(layer);
    }

    function drawTimeband(layer, band, secondsPerUnit) {
        var offset = Math.round(band.offset / secondsPerUnit) + sideMargin;
        var size = Math.round(band.duration / secondsPerUnit);

        var color
        var highlight
        if (band.bandType == "Conflict") {
            color = "rgba(255,0,120, 1)";
            highlight = "rgba(255,0,120, .7)";
        } else if (band.bandType == "Learning") {
            color = "rgba(82,12,232, 1)";
            highlight = "rgba(82,12,232, .7)";
        } else if (band.bandType == "Rework") {
            color = "rgba(255,203,1, 1)";
            highlight = "rgba(255,203,1, .7)";
        }

        var rect = new Kinetic.Rect({
            x: offset,
            y: topMargin + bandMargin,
            width: size,
            height: height - bottomMargin - topMargin - bandMargin,
            fill: color,
            stroke: highlight,
            strokeWidth: 1
        });

        rect.on('mouseover touchstart', function () {
            this.setFill(highlight);
            layer.draw();
        });

        rect.on('mouseout touchend', function () {
            this.setFill(color);
            layer.draw();
        });

        layer.add(rect);
    }

    refreshTimeline();

</g:javascript>
<r:layoutResources/>

</body>
</html>