$(function() {
    $('#datepicker')
		.datepicker({
			startDate: startDate(), 
			language: i18n.language
		})
		.on('changeDate', function(ev){
			var locally_formatted_date = $('#datepicker').data('date');
			var selected_date = ev.date;
			var sort_format = formatForSort(selected_date);
			
			// Add date to list only if not already selected. 
			if ($('#' + sort_format).length == 0) {
	    		$('#choices:not(:has(ul))').append("<ul></ul>"); // Init list if not already done.
				$('#choices ul').append(
					'<li class="choice" id="' + sort_format + '">' + 
						'<a href="#" title="Remove"><i class="icon-trash remove-choice"></i></a> ' + locally_formatted_date + 
						'<input type="hidden" name="date_choice[x]" value="' + formatToISOFormat(selected_date) + '"/>' +
					'</li>'
				);
	    		organize();
			}
		});
	
	$(document).on('click', '.remove-choice', function(e) {
	    $(this).parents('.choice').remove();
	    organize();
	});
	
	var organize = function() {
		// Sort items by ascending date.
		var items = $('#choices ul').children('li').remove();
		items.sort(function(a, b) {
			return parseInt(a.id) > parseInt(b.id);
		});
		$('#choices ul').append(items); 
		
		// Renumber items.
		var count = 0;
	    $('#choices').each(function(i) {
	        $('input[type=hidden]', this).each(function() {
	            $(this).attr('name', $(this).attr('name').replace(/date_choice\[.+?\]/g, 'date_choice[' + count + ']'))
	        	count++
	        })
	    })
	}
	
	function startDate() {
		var date = new Date();
		date.setDate(date.getDate() - 1);
		return date;
	}
	
	function formatToISOFormat(date) {
		var day = date.getDate(); if (day < 10) { day = '0' + day};
		var month = date.getMonth(); month++; if (month < 10) { month = '0' + month };
		var year = date.getFullYear();
		return year + '-' + month + '-' + day;
	}
	
	function formatForSort(date) {
		var day = date.getDate(); if (day < 10) { day = '0' + day};
		var month = date.getMonth(); month++; if (month < 10) { month = '0' + month };
		var year = date.getFullYear();
		return '' + year + month + day;
	    }
})
