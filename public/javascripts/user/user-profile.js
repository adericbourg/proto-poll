$(function() {
	$('#user-profile-tabs a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
	
	// ----------------------------
	//  User informations
	// ----------------------------
	
	// Put modal info link info form.
	$(document).ready(function() {
		registerGravatarTipLink();
	});

	function registerGravatarTipLink() {
		$('#avatarEmail_field .help-inline').append($('#gravatar-tip-link'));
	}
});
