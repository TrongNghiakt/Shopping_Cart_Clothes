$(function() {

	// User Register Validation
	var $userRegister = $("#userRegister");

	$userRegister.validate({
		rules: {
			name: {
				required: true,

			},
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNumber: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			password: {
				required: true,
				space: true

			},
			confirmpassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			},
			address: {
				required: true,


			},

			city: {
				required: true,


			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			}, img: {
				required: true,
			}

		},
		messages: {
			name: {
				required: 'Name required',

			},
			email: {
				required: 'Email name must be required',
				space: 'Space not allowed',
				email: 'Invalid email'
			},
			mobileNumber: {
				required: 'Mobile number must be required',
				space: 'Space not allowed',
				numericOnly: 'Invalid mob no',
				minlength: 'Min 10 digit',
				maxlength: 'Max 12 digit'
			},

			password: {
				required: 'Password must be required',
				space: 'Space not allowed'

			},
			confirmpassword: {
				required: 'Confirm password must be required',
				space: 'Space not allowed',
				equalTo: 'Password mismatch'

			},
			address: {
				required: 'Address must be required',


			},

			city: {
				required: 'City must be required',


			},
			state: {
				required: 'State must be required',


			},
			pincode: {
				required: 'Pincode must be required',
				space: 'Space not allowed',
				numericOnly: 'Invalid pincode'

			},
			img: {
				required: 'Image required',
			}
		}


	})



	// Orders Validation

	var $orders = $("#orders");

	$orders.validate({
		rules: {
			firstName: {
				required: true,

			},
			lastName: {
				required: true,

			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNo: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			address: {
				required: true,


			},

			city: {
				required: true,


			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			},
			paymentType: {
				required: true
			}
		},
		messages: {
			firstName: {
				required: 'First name required',

			},
			lastName: {
				required: 'Last name required',

			},
			email: {
				required: 'Email name must be required',
				space: 'Space not allowed',
				email: 'Invalid email'
			},
			mobileNo: {
				required: 'Mobile Number must be required',
				space: 'Space not allowed',
				numericOnly: 'Invalid Mobile Number',
				minlength: 'Min 10 digit',
				maxlength: 'Max 12 digit'
			}
			,
			address: {
				required: 'Address must be required',

			},

			city: {
				required: 'City must be required',

			},
			state: {
				required: 'State must be required',

			},
			pincode: {
				required: 'Pincode must be required',
				space: 'Space not allowed',
				numericOnly: 'Invalid pincode'

			},
			paymentType: {
				required: 'Select payment type'
			}
		}
	})



	// Reset Password Validation
	var $resetPassword = $("#resetPassword");

	$resetPassword.validate({

		rules: {
			password: {
				required: true,
				space: true

			},
			confirmPassword: {
				required: true,
				space: true,
				equalTo: '#password'

			}
		},
		messages: {
			password: {
				required: 'Password must be required',
				space: 'Space not allowed'

			},
			confirmPassword: {
				required: 'Confirm password must be required',
				space: 'Space not allowed',
				equalTo: 'Password mismatch'

			}
		}
	})



})

jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});
	
		jQuery.validator.addMethod('space', function(value, element) {
		return /^[^-\s]+$/.test(value);
	});

	jQuery.validator.addMethod('all', function(value, element) {
		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
	});


	jQuery.validator.addMethod('numericOnly', function(value, element) {
		return /^[0-9]+$/.test(value);
	});
