<?php require("config.php"); ?>
<?php
	if(!empty($_POST)) {
		// gets user's info based on username
		$query = "SELECT * FROM users WHERE username = :username";
		$query_params = array(
			":username" => $_POST['username'],
		);

		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = "Database Error 1. Please try again!";
			die(json_encode($response)); 
		}

		$login_ok = false;

		$row = $stmt->fetch();

		if($row) {
			$pepper = $row['pepper'];
			$salt = $row['salt'];
			$fullPassword = "" . $pepper . $_POST['password'] . $salt;
			$hashedPassword = hash("sha512", $fullPassword);

			if($hashedPassword === $row['hashed_password']) {
				$login_ok = true;
			}
		}
		
		if($login_ok) {
			$response["success"] = 1;
			$response["message"] = "Login successful!";
			die(json_encode($response));
		} else {
			$response["success"] = 0;
			$response["message"] = "Invalid credentials";
			die(json_encode($response));
		}
	} else {
?>
	<h1> Login </h1>
	<form action="login.php" method="post">
	 Username:<br />
        <input type="text" name="username" placeholder="username" />
        <br /><br />
        Password:<br />
        <input type="password" name="password" placeholder="password" value="" />
        <br /><br />
        <input type="submit" value="Login" />
    </form>
    <a href="new_user.php">Register</a>
<?php	} ?>


