<script type="text/javascript">
    function refreshTimeline() {
        $.ajax({
            type: 'GET',
            url: '/visualizer/timeline/showTimeline',
            success: drawTimeline,
            error: handleError
        });
    }

    refreshTimeline();

</script>
