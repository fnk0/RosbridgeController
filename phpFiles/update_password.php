<?php require("config.php"); ?>
<?php require("functions.php"); ?>
<?php

	if(!empty($_POST)) {

		 if(empty($_POST['password']) || empty($_POST["username"])) {
			// Generate data that will be in the JSON response
			$response["success"] = 0;
			$response["message"] = "Please complete all required fields.";
			die(json_encode($response));
		} 

		$salt = generate_salt();
		$pepper = generate_pepper();
		$fullPassword = "" . $pepper . $_POST['password'] . $salt;
		$hashedPassword = hash("sha512", $fullPassword);

		// If it's here without diying , now it's time to create a new user.
		$query = "UPDATE users SET hashed_password = :hashed_password, salt = :salt, pepper = :pepper WHERE username = :username";
					
		$query_params = array(
			':hashed_password' => $hashedPassword,
			':salt' => $salt,
			':pepper' => $pepper,
			':username' =>  strtolower($_POST['username']),
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
		$response["message"] = "Password Sucessfully Updated!";
		die(json_encode($response));
	} else {
?>


<form action="update_password.php" method="post">

<input type="text" name="email" value="new@test.com" />
<input type="text" name="password" value="newpass" />

<input type="submit" value="Submit" />
</form>

<?php } ?>













