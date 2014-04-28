<?php require("config.php"); ?>
<?php require("functions.php"); ?>
<?php

	if(!empty($_POST)) {
		 if(empty($_POST['username'])) {
			// Generate data that will be in the JSON response
			$response["success"] = 0;
			$response["message"] = "Please complete all required fields.";
			die(json_encode($response));
		} 

		// If it's here without diying , now it's time to create a new user.
		$query = "INSERT INTO robots  
						(username, 
						robot_name, 
						robot_url, 
						robot_port,
						default_angular, 
						max_angular, 
						default_velocity, 
						max_velocity )  
					VALUES 
						(:username, 
						:robot_name, 
					  	:robot_url, 
					  	:robot_port, 
						:default_angular, 
					  	:max_angular,  
						:default_velocity, 
						:max_velocity )";
		
		$query_params = array(
			':username' => strtolower($_POST['username']),
			':robot_name' => $_POST['robot_name'],
			':robot_url' => $_POST['robot_url'],
			':robot_port' => $_POST['robot_port'],
			':default_angular' => $_POST['default_angular'],
			':max_angular' => $_POST['max_angular'],
			':default_velocity' => $_POST['default_velocity'],
			':max_velocity' => $_POST['max_velocity'],
		);

		// time to run the query and create the robot
		try {
	        $stmt   = $db->prepare($query);
	        $result = $stmt->execute($query_params);
		} catch(PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = "Database error 2, please try again!!" + $ex->getCode();;
			die(json_encode($response));
		}
		// If didn't died yet the robot was succesfully added!!
		$response["success"] = 1;
		$response["message"] = "Robot Successfully Added.";
		die(json_encode($response));
	} else {
?>
<h1> New Robot </h1>
	<form action="new_robot.php" method="post">
	 	Username:<br />
        <input type="text" name="username" placeholder="username" />
        <br /><br />
        Robot Name:<br />
        <input type="text" name="robot_name" placeholder="robot_name" />
        <br /><br />
        Robot URL:<br />
        <input type="text" name="robot_url" placeholder="robot_url" value="" />
        <br /><br />
        port:<br />
        <input type="text" name="robot_port" placeholder="robot_port" />
        <br /><br />
        default_velocity:<br />
        <input type="text" name="default_velocity" placeholder="default_velocity" />
        <br /><br />
        default_velocity:<br />
        <input type="text" name="default_velocity" placeholder="default_velocity" />
        <br /><br />
        max_velocity:<br />
        <input type="text" name="max_velocity" placeholder="max_velocity" />
        <br /><br />
        default_angular:<br />
        <input type="text" name="default_angular" placeholder="default_angular" />
        <br /><br />
        interval_angular:<br />
        <input type="text" name="interval_angular" placeholder="interval_angular" />
        <br /><br />
        max_angular:<br />
        <input type="text" name="max_angular" placeholder="max_angular" />
        <br /><br />
        <input type="submit" value="Add Robot" />
    </form>
<?php } ?>