<?php 

	function generate_salt() {

		$special_chars = array("@", "$", "#", "?", " ", "!", "*", "+", "=", "{", "}", "[", "]", "-", "(", "0", ",");
		$random_char = rand(0 , sizeof($special_chars) - 1);
		$random_number = rand(0, 9);
		$salt = "" . $special_chars[$random_char] . $random_number;

		return $salt;
	}

	function generate_pepper() {
		$letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		$count = 0;
		$pepper = "";

		while ($count < 2) {
			$random_letter = rand(0, strlen($letters) - 1);
			$pepper .= $letters[$random_letter];
			$count++;
		}
		
		return $pepper;
	}

	function default_value($var, $default) {
		return empty($var) ? $default : $var;
	}

?>