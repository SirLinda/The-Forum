<?php

// Connect to the database here

$conn = mysqli_connect($servername, $db_username, $db_password, $db_name);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

// Retrieve question data from java
$title = $_POST["title"];
$description = $_POST["description"];
$username = $_POST["username"];

// Find user ID
$query = "SELECT user_id FROM User WHERE username = '$username'";
$result = $conn->query($query);

if ($result) {
	$row = $result->fetch_assoc();
	$userID = intval($row["user_id"]);

	// Prepare the SQL statement
	$query = "INSERT INTO Question (title, description, user_id) VALUES (?, ?, ?)";

	$stmt = mysqli_prepare($conn, $query);

	if ($stmt) {
		// Bind the parameters to the prepared statement
		mysqli_stmt_bind_param($stmt, "ssi", $title, $description, $userID);

		// Execute the statement
		if (mysqli_stmt_execute($stmt)) {
			// Question successfully
			$response = array(
			'success' => true,
			'message' => 'Question posted successfully.'
			);
			echo json_encode($response);
		}
		else {
			// Question unsuccessfully
			$response = array(
			'success' => false,
			'message' => 'Error posting the question.',
			'error' => mysqli_stmt_error($stmt)
			);
			echo json_encode($response);
		}

		// Close the statement
		mysqli_stmt_close($stmt);
	}
	else {
		// Error occurred while preparing the statement
		$response = array(
		'success' => false,
		'message' => 'Error preparing the statement.',
		'error' => mysqli_error($conn)
		);
		echo json_encode($response);
	}
}
else {
	// Error case
	$response = array(
	'success' => false,
	'message' => 'User does not exits.',
	'error' => mysqli_error($conn)
	);
	echo json_encode($response);
}

// Close the database connection
mysqli_close($conn);
?>

