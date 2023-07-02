<?php

// Connect to the database here

// Get the registration data from user
$username = $_POST['username'];
$password = $_POST['password'];
$email = $_POST['email'];

$conn = new mysqli($servername, $db_username, $db_password, $db_name);

if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
}

// Prepare the SQL statement
$stmt = $conn->prepare("INSERT INTO User (username, password, email) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $username, $password, $email);

// Execute the SQL statement
if ($stmt->execute()) {
	// Registration successful
	$response = array('status' => 'success', 'message' => 'Registration successful');
}
else {
	// Registration unsuccessful
	$response = array('status' => 'error', 'message' => 'Registration failed. Please try again.');
	// You can also include additional error information if needed, e.g., $response['error'] = $conn->error;
}

// Close connections
$stmt->close();
$conn->close();

// JSON them up
header('Content-Type: application/json');
echo json_encode($response);
?>

