$(document).ready(function() {
    // Handle search form submission
    $('searchForm').submit(function(e) {
        e.preventDefault();
        let searchVal = $('searchInput').val();

        if (!searchVal || searchVal === '') {
            // Add a blue border to the input to indicate an error
            $('searchInput').addClass('input-error');
        } else {
            // Remove the blue border if it exists
            $('searchInput').removeClass('input-error');

            // Perform AJAX search request here
        }
    });

    // Handle insert form submission
    $('insertForm').submit(function(e) {
        e.preventDefault();
        let insertVal = $('insertInput').val();

        if (!insertVal || insertVal === '') {
            // Add a blue border to the input to indicate an error
            $('insertInput').addClass('input-error');
        } else {
            // Remove the blue border if it exists
            $('insertInput').removeClass('input-error');

            // Perform AJAX insert request here
        }
    });
});

