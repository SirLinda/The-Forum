<?php

// Connect to the database here

$conn = new mysqli($servername, $db_username, $db_password, $db_name);
if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
}

$questionId = $_POST['question_id'];
$username = $_POST['username'];

// Find user ID
$query = "SELECT user_id FROM User WHERE username = '$username'";
$result = $conn->query($query);

if ($result) {
	$row = $result->fetch_assoc();
	$userID = intval($row["user_id"]);

	// Check if the user has already voted for the question
	$checkVoteQuery = "SELECT * FROM QuestionVote WHERE question_id_vote_FK = $questionId AND user_id_vote_FK = $userID";
	$checkVoteResult = mysqli_query($conn, $checkVoteQuery);

	if (mysqli_num_rows($checkVoteResult) == 0) {
		// Insert a new vote
		$insertVoteQuery = "INSERT INTO QuestionVote (user_id_vote_FK, question_id_vote_FK) VALUES ($userID, $questionId)";
		mysqli_query($conn, $insertVoteQuery);

		// Update the vote count in the que table
		$updateVoteCountQuery = "UPDATE Question SET vote_count = vote_count + 1 WHERE id = $questionId";
		mysqli_query($conn, $updateVoteCountQuery);

		echo "Vote recorded successfully!";
	}
	else {
		echo "You have already voted for this question.";
	}
}
else {
	echo "Error executing query: " . $conn->error;
}

// Close always
$conn->close();
?>

