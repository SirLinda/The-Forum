<?php
// Start the session
session_start();

// Connect to the database here

$conn = mysqli_connect($servername, $db_username, $db_password, $db_name);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

// Check if the login form is submitted
if ($_SERVER["REQUEST_METHOD"] == "POST") {

	// Retrieve the username and password from the request
	$username = $_POST["username"];
	$password = $_POST["password"];

	// Check if the username and password are valid
	$query = "SELECT * FROM User WHERE username = '$username' AND password = '$password'";
	$result = mysqli_query($conn, $query);

	if (mysqli_num_rows($result) > 0) {
		//User exits

		//Track session
		$_SESSION['username'] = $username;

		// Return success response with session data
		$response = array("success" => true, "message" => "Login successful", "user_id" => $_SESSION['username']);
		echo json_encode($response);
	}
	else {
		// Invalid username or password, return error response
		$response = array("success" => false, "message" => "Invalid username or password");
		echo json_encode($response);
	}
}

mysqli_close($conn);
?>

