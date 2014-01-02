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

<g:javascript>
        $.ajax({
            type: 'GET',
            url: '/visualizer/timeline/showTimeline',
            success: drawTimeline,
            error: handleError
        });

        function handleError(XMLHttpRequest, textStatus, errorThrown) {
            alert(textStatus + ":" + errorThrown);
        }

        function drawTimeline(timelineData) {
            //document.getElementById("timeline").innerHTML = timeline.end.offset;
            var canvas = document.getElementById("timeline");
            var ctx = canvas.getContext("2d");
            var secondsPerUnit = timelineData.end.offset / (width - (2*sideMargin));
            drawTimeBands(ctx, timelineData.timeBands, secondsPerUnit);
            drawMainTimeline(ctx, timelineData.start, timelineData.end);
            drawEvents(ctx, timelineData.events, secondsPerUnit);
            alert('done');
        }

        var sideMargin = 40;
        var bottomMargin = 30;
        var topMargin = bottomMargin;
        var height = 180;
        var width = 800;

        function drawEvents(ctx, events, secondsPerUnit) {
            ctx.strokeStyle = "gray";
            ctx.lineWidth = 2;
            ctx.beginPath();
            for (var i = 0; i < events.length; i++) {
                drawEvent(ctx, events[i], secondsPerUnit);
            }
            ctx.stroke();
        }

        function drawEvent(ctx, event, secondsPerUnit) {
            var offset = Math.round(event.offset / secondsPerUnit) + sideMargin;
            var tickHeight = 15;
            var tickMargin = 14;

            ctx.moveTo(offset, topMargin);
            ctx.lineTo(offset, height - bottomMargin + tickHeight);
            drawBelowLabel(ctx, offset, tickHeight, event.shortTime);
        }

        function drawTick(ctx, xPosition, label) {
            ctx.font = "10pt Helvetica";
            ctx.fillStyle = "black";
            ctx.textAlign = "center";

        }

        function drawTimeBands(ctx, bands, secondsPerUnit) {
            for (var i = 0; i < bands.length; i++) {
                drawBand(ctx, bands[i], secondsPerUnit);
            }
        }

        function drawBand(ctx, band, secondsPerUnit) {
            var offset = Math.round(band.offset / secondsPerUnit) + sideMargin;
            var size =  Math.round(band.duration / secondsPerUnit);
            var bandMargin = 20;

            if (band.bandType == "Conflict") {
                ctx.fillStyle =  "rgba(255,0,120, 1)";
            } else if (band.bandType == "Learning") {
                ctx.fillStyle =  "rgba(82,12,232, 1)";
            } else if (band.bandType == "Rework") {
                ctx.fillStyle =  "rgba(255,203,1, 1)";
            }
            ctx.fillRect(offset, topMargin + bandMargin, size, height - bottomMargin - topMargin - bandMargin);
        }

        function drawMainTimeline(ctx, start, end) {
            ctx.strokeStyle = "black";//"rgba(128,128,255, 0.5)";
            ctx.lineWidth = 3;
            ctx.beginPath();
            var tickHeight = 10;

            ctx.moveTo(sideMargin, height - bottomMargin + tickHeight);
            ctx.lineTo(sideMargin, height - bottomMargin);
            ctx.lineTo(width - sideMargin, height - bottomMargin);
            ctx.lineTo(width - sideMargin, height - bottomMargin + tickHeight);

            drawLeftLabel(ctx, sideMargin, start.shortTime);
            drawRightLabel(ctx, width - sideMargin, end.shortTime);
            ctx.stroke();
        }

        function drawLeftLabel(ctx, xPosition, label) {
            var tickMargin = 5;
            ctx.font = "10pt Helvetica bold";
            ctx.fillStyle = "black";
            ctx.textAlign = "right";
            ctx.fillText(label, xPosition - tickMargin, height - bottomMargin + 10);
        }

        function drawRightLabel(ctx, xPosition, label) {
            var tickMargin = 5;
            ctx.font = "10pt Helvetica bold";
            ctx.fillStyle = "black";
            ctx.textAlign = "left";
            ctx.fillText(label, xPosition + tickMargin, height - bottomMargin + 10);
        }

        function drawBelowLabel(ctx, xPosition, tickHeight, label) {
            var tickMargin = 5;
            ctx.font = "10pt Helvetica bold";
            ctx.fillStyle = "black";
            ctx.textAlign = "center";
            ctx.fillText(label, xPosition, height - bottomMargin + tickHeight + 10 + tickMargin);
        }



        var can, ctx,
                minVal, maxVal,
                xScalar, yScalar,
                numSamples, y;
        // data sets -- set literally or obtain from an ajax call
        var dataName = [ "Human", "Chimp", "Dolphin", "Cat" ];
        var dataValue = [ 11000, 6200, 5800, 300 ];

        function init() {
            // set these values for your data
            numSamples = 4;
            maxVal = 12000;
            var stepSize = 1000;
            var colHead = 50;
            var rowHead = 60;
            var margin = 10;
            var header = "Millions"
            can = document.getElementById("chart");
            ctx = can.getContext("2d");
            ctx.fillStyle = "black"
            yScalar = (can.height - colHead - margin) / (maxVal);
            xScalar = (can.width - rowHead) / (numSamples + 1);
            ctx.strokeStyle = "rgba(128,128,255, 0.5)"; // light blue line
            ctx.beginPath();
            // print  column header
            ctx.font = "14pt Helvetica"
            ctx.fillText(header, 0, colHead - margin);
            // print row header and draw horizontal grid lines
            ctx.font = "12pt Helvetica"
            var count =  0;
            for (scale = maxVal; scale >= 0; scale -= stepSize) {
                y = colHead + (yScalar * count * stepSize);
                ctx.fillText(scale, margin,y + margin);
                ctx.moveTo(rowHead, y)
                ctx.lineTo(can.width, y)
                count++;
            }
            ctx.stroke();
            // label samples
            ctx.font = "14pt Helvetica";
            ctx.textBaseline = "bottom";
            for (i = 0; i < 4; i++) {
                calcY(dataValue[i]);
                ctx.fillText(dataName[i], xScalar * (i + 1), y - margin);
            }
            // set a color and a shadow
            ctx.fillStyle = "green";
            ctx.shadowColor = 'rgba(128,128,128, 0.5)';
            ctx.shadowOffsetX = 20;
            ctx.shadowOffsetY = 1;
            // translate to bottom of graph and scale x,y to match data
            ctx.translate(0, can.height - margin);
            ctx.scale(xScalar, -1 * yScalar);
            // draw bars
            for (i = 0; i < 4; i++) {
                ctx.fillRect(i + 1, 0, 0.5, dataValue[i]);
            }
        }

        function calcY(value) {
            y = can.height - value * yScalar;
        }
</g:javascript>
<div id="timelineHolder">
    <canvas id="timeline" height="180" width="800">
    </canvas>
</div>

<div id="container"></div>
<script defer="defer">
    function loadImages(sources, callback) {
        var images = {};
        var loadedImages = 0;
        var numImages = 0;
        // get num of sources
        for(var src in sources) {
            numImages++;
        }
        for(var src in sources) {
            images[src] = new Image();
            images[src].onload = function() {
                if(++loadedImages >= numImages) {
                    callback(images);
                }
            };
            images[src].src = sources[src];
        }
    }
    function draw(images) {
        var stage = new Kinetic.Stage({
            container: 'container',
            width: 578,
            height: 200
        });
        var layer = new Kinetic.Layer();

        var colorPentagon = new Kinetic.RegularPolygon({
            x: 80,
            y: stage.getHeight() / 2,
            sides: 5,
            radius: 70,
            fill: 'red',
            stroke: 'black',
            strokeWidth: 4,
            draggable: true
        });

        var patternPentagon = new Kinetic.RegularPolygon({
            x: 220,
            y: stage.getHeight() / 2,
            sides: 5,
            radius: 70,
            fillPatternImage: images.darthVader,
            fillPatternOffset: [-220, 70],
            stroke: 'black',
            strokeWidth: 4,
            draggable: true
        });

        var linearGradPentagon = new Kinetic.RegularPolygon({
            x: 360,
            y: stage.getHeight() / 2,
            sides: 5,
            radius: 70,
            fillLinearGradientStartPoint: [-50, -50],
            fillLinearGradientEndPoint: [50, 50],
            fillLinearGradientColorStops: [0, 'red', 1, 'yellow'],
            stroke: 'black',
            strokeWidth: 4,
            draggable: true
        });

        var radialGradPentagon = new Kinetic.RegularPolygon({
            x: 500,
            y: stage.getHeight() / 2,
            sides: 5,
            radius: 70,
            fillRadialGradientStartPoint: 0,
            fillRadialGradientStartRadius: 0,
            fillRadialGradientEndPoint: 0,
            fillRadialGradientEndRadius: 70,
            fillRadialGradientColorStops: [0, 'red', 0.5, 'yellow', 1, 'blue'],
            stroke: 'black',
            strokeWidth: 4,
            draggable: true
        });

        /*
         * bind listeners
         */
        colorPentagon.on('mouseover touchstart', function() {
            this.setFill('blue');
            layer.draw();
        });

        colorPentagon.on('mouseout touchend', function() {
            this.setFill('red');
            layer.draw();
        });

        patternPentagon.on('mouseover touchstart', function() {
            this.setFillPatternImage(images.yoda);
            this.setFillPatternOffset(-100, 70);
            layer.draw();
        });

        patternPentagon.on('mouseout touchend', function() {
            this.setFillPatternImage(images.darthVader);
            this.setFillPatternOffset(-220, 70);
            layer.draw();
        });

        linearGradPentagon.on('mouseover touchstart', function() {
            this.setFillLinearGradientStartPoint(-50);
            this.setFillLinearGradientEndPoint(50);
            this.setFillLinearGradientColorStops([0, 'green', 1, 'yellow']);
            layer.draw();
        });

        linearGradPentagon.on('mouseout touchend', function() {
            // set multiple properties at once with setAttrs
            this.setAttrs({
                fillLinearGradientStartPoint: [-50, -50],
                fillLinearGradientEndPoint: [50, 50],
                fillLinearGradientColorStops: [0, 'red', 1, 'yellow']
            });
            layer.draw();
        });

        radialGradPentagon.on('mouseover touchstart', function() {
            this.setFillRadialGradientColorStops([0, 'red', 0.5, 'yellow', 1, 'green']);
            layer.draw();
        });

        radialGradPentagon.on('mouseout touchend', function() {
            // set multiple properties at once with setAttrs
            this.setAttrs({
                fillRadialGradientStartPoint: 0,
                fillRadialGradientStartRadius: 0,
                fillRadialGradientEndPoint: 0,
                fillRadialGradientEndRadius: 70,
                fillRadialGradientColorStops: [0, 'red', 0.5, 'yellow', 1, 'blue']
            });
            layer.draw();
        });

        layer.add(colorPentagon);
        layer.add(patternPentagon);
        layer.add(linearGradPentagon);
        layer.add(radialGradPentagon);
        stage.add(layer);
    }
    var sources = {
        darthVader: 'http://www.html5canvastutorials.com/demos/assets/darth-vader.jpg',
        yoda: 'http://www.html5canvastutorials.com/demos/assets/yoda.jpg'
    };

    loadImages(sources, function(images) {
        draw(images);
    });

</script>

<r:layoutResources/>

</body>
</html>