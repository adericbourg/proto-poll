$(function() {
	$(document).on('click', '.answer', function(e) {
    	$(this).find("input").each(function() { 
    		this.checked = !this.checked; 
		});
    	return false;
    });
	
	$(document).on('click', 'td.username', function(e) {
    	$(this).children("input").first().focus();
    });
	
    $(".answer input").click(function(e) {
		e.stopPropagation();
		var val = $(this).val();
	});
});
