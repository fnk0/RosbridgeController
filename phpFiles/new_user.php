<?php require("config.php"); ?>
<?php require("functions.php"); ?>
<?php

	if(!empty($_POST)) {

		 if(empty($_POST['password']) || empty($_POST["username"])) {
			// Generate data that will be in the JSON response
			$response["success"] = 0;
			$response["message"] = "Please complete all required fields.";
			die(json_encode($response));
		} elseif(strlen($_POST['password']) < 6) {
			$response["success"] = 0;
			$response["message"] = "The password needs to be at least 6 characters long";
			die(json_encode($response));
		}

		// If the page didn't died now checks if the user or password exists already
		$query = " SELECT * FROM users WHERE username = :username";
		$query_params = array(
			':username' => $_POST['username'],
		);

		// Run the query
		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch(PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = "Database Error1. Please try Again!";
			die(json_encode($response));
		}

		// fetch the data => fetch == array of returned data
		$row = $stmt->fetch();
		if($row) {
			$response["success"] = 0;
			$response["message"] = "I'm sorry this username is already in use";
			die(json_encode($response));
		}

		$salt = generate_salt();
		$pepper = generate_pepper();
		$fullPassword = "" . $pepper . $_POST['password'] . $salt;
		$hashedPassword = hash("sha512", $fullPassword);

		// If it's here without diying , now it's time to create a new user.
		$query = "INSERT INTO users 
							(username, hashed_password, date_registred, salt, pepper, security_question1, security_question2, security_question3, answer1, answer2, answer3) 
							VALUES 
							(:username, :pass, :date_registred, :salt, :pepper, :security_question1, :security_question2, :security_question3, :answer1, :answer2, :answer3) ";
		
		$query_params = array(
			':username' =>  strtolower($_POST['username']),
			':pass' => $hashedPassword,
			':date_registred' => ($_POST['date']),
			':salt' => $salt,
			':pepper' => $pepper,
			':security_question1' => strtolower($_POST['security_question1']),
			':security_question2' => strtolower($_POST['security_question2']),
			':security_question3' => strtolower($_POST['security_question3']),
			':answer1' => strtolower($_POST['answer1']),
			':answer2' => strtolower($_POST['answer2']),
			':answer3' => strtolower($_POST['answer3'])		
		);

		// time to run the query and create the user
		try {
	        $stmt   = $db->prepare($query);
	        $result = $stmt->execute($query_params);

		} catch(PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = "Database error 2, please try again!!";
			die(json_encode($response));
		}
		// If didn't died yet the user was succesfully added!!
		$response["success"] = 1;
		$response["message"] = "username Sucessfully added!";
		die(json_encode($response));

	} else {
?>
<h1> Register </h1>
<form action="new_user.php" method="post">
	First Name: <br />
	<input type="text" name="name" value="" /> <br /><br />
	Last Name: <br />
	<input type="text" name="last_name" value="" /> <br /><br />
	E-mail Address: <br />
	<input type="text" name="username" value="" /> <br /><br />
	Password: <br />
	<input type="password" name="password" value="" /> <br /> <br />
	<input type="submit" value="Register New User" />
</form>
<?php 
	}
?>