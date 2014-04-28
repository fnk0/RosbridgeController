<?php require("config.php"); ?>
<?php require("functions.php"); ?>
<?php
	
	$query = "SELECT * FROM robots WHERE username = :username";
	$query_params = array(
		':username' => strtolower($_POST['username']),
	);

	try {
		$stmt = $db->prepare($query);
		$result = $stmt->execute($query_params);
	} catch(PDOException $ex) {
		$response["success"] = 0;
		$response["message"] = "Database error!!";
		die(json_encode($response));
	}

	$rows = $stmt->fetchAll();

	if($rows) {
		$response["success"] = 1;
		$response["message"] = "My robots";
		$response["robots"] = array();

		foreach ($rows as $row) {
			# code...
			$robots = array();
			$robots["id"] = $row["id"];
			$robots["robot_name"] = ucfirst($row["robot_name"]);
			$robots["robot_url"] = $row["robot_url"];
			$robots["robot_port"] = $row["robot_port"];
			$robots["username"] = $row["username"];	
			$robots["default_angular"] = $row["default_angular"];	
			$robots["max_angular"] = $row["max_angular"];	
			$robots["default_velocity"] = $row["default_velocity"];	
			$robots["max_velocity"] = $row["max_velocity"];	
			array_push($response["robots"], $robots);
		}
		echo json_encode($response);
	} else {
		$response["success"] = 0;
		$response["message"] = "No robots Available";
		die(json_encode($response));
	}
?>