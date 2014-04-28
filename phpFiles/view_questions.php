<?php require("config.php"); ?>
<?php require("functions.php"); ?>
<?php
	
	if(!empty($_POST)) {

		$query = "SELECT * FROM users WHERE username = :username";

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

		function getNum($num1, $num2) {
			if($num1 != $num2) {
				return $num2;
			} else {
				$num2 = rand(1, 3);
				getNum($num1, $num2);
			}
		}
		$rows = $stmt->fetchAll();

		$randomNum1 = rand(1,3);
		$randomNum2 = rand(1,3);

		$randomNum2 = getNum($randomNum1, $randomNum2);

		$security_question_one = "security_question" . $randomNum1;
		$security_question_two = "security_question" . $randomNum2;

		$security_answer_one = "answer" . $randomNum1;
		$security_answer_two = "answer" . $randomNum2;

		if($rows) {
			$response["success"] = 1;
			$response["message"] = "Questions";
			$response["questions"] = array();

			foreach ($rows as $row) {
				# code...
				$questions = array();
				$questions["number1"] = $randomNum1;
				$questions["number2"] = $randomNum2;
				$questions[$security_question_one] = $row[$security_question_one];
				$questions[$security_question_two] = $row[$security_question_two];
				$questions[$security_answer_one] = $row[$security_answer_one];
				$questions[$security_answer_two] = $row[$security_answer_two];
				array_push($response["questions"], $questions);
			}

			echo json_encode($response);
		} else {
			$response["success"] = 0;
			$response["message"] = "No person Available";
			die(json_encode($response));
		}
	} else {
?>

<h1> Person </h1>
<form action="view_questions.php" method="post">

<input type="text" name="username" value="" />

</form>

<?php } ?>