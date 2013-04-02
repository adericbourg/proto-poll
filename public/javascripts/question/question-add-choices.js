$(function() {
	$(document).on('click', '.remove-choice', function(e) {
        $(this).parents('.choice').remove()
        renumber()
    });
    
    $(document).on('click', '.add-choice', function(e) {
    	addChoice();
    });
    
    $(document).on('keypress', '.last-input', function(e) {
    	// While input is not empty, "Tab" key press will add new input.
    	if (e.keyCode === 9 && $(this).val() != "") {
    		addChoice();
    		$('#choices .choice input:last').focus();
        	return false;
    	}
    });
    
    $('#form').submit(function() {
        $('.choice_template').remove()
    });

    function addChoice() {
    	var template = $('.choice_template')
        template.before('<div class="choice">' + template.html() + '</div>')
        renumber()
    }
    
    var renumber = function(choice) {
    	var count = 0;
        $('#choices .choice').each(function(i) {
            $('input', this).each(function() {
            	count++
                $(this).attr('name', $(this).attr('name').replace(/choice\[.+?\]/g, 'choice[' + count + ']'))
                $(this).attr('placeholder', i18n.choice_placeholder + ' ' + count)
                $(this).removeClass("last-input")
            })
        })
        
        $('#choices .choice input:last').addClass("last-input")
        
        if (count > 10) {
        	$('#manage-bottom').show()
        } else {
        	$('#manage-bottom').hide()
        }
    }
    
    renumber()
})
