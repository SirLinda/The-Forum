<?php
// Perform any necessary validation or sanitization of the input data

// Connect to the database here

$conn = mysqli_connect($servername, $db_username, $db_password, $db_name);

// Check the connection
if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
}

// Get the reply
$questionId = $_POST['question_id'];
$reply = $_POST['reply'];

// Prepare the SQL statement
$sql = "INSERT INTO Reply (question_id, reply) VALUES (?, ?)";

// Create a prepared statement
$stmt = $conn->prepare($sql);

// Bind the parameters
$stmt->bind_param("is", $questionId, $reply);

// Execute
if ($stmt->execute()) {
	// Reply inserted successful
	$response = array("status" => "success", "message" => "Reply posted successfully");
}
else {
	// Failed to insert reply
	$response = array("status" => "error", "message" => "Failed to post reply");
}

// Close the prepared statement
$stmt->close();

// Close the database connection
$conn->close();

// Send the JSON response
header('Content-type: application/json');
echo json_encode($response);

?>

