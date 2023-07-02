<?php
// Connect to the database here

$conn = new mysqli($servername, $db_username, $db_password, $db_name);
if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
}

// Fetch ALL questions
$sql = "SELECT * FROM Question";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
$questions = array();

// Fetch each question and ALL its replies
while ($row = $result->fetch_assoc()) {
	$question = array(
	"id" => $row["id"],
	"title" => $row["title"],
	"description" => $row["description"],
	"vote_count" => $row["vote_count"]
	);

	// Fetch the replies for the current question
	$replies = array();
	$repliesSql = "SELECT * FROM Reply WHERE question_id = '" . $row["id"] . "'";
	$repliesResult = $conn->query($repliesSql);
	//Nest replies
	while ($replyRow = $repliesResult->fetch_assoc()) {
		$replies[] = $replyRow["reply"];
	}

	$question["replies"] = $replies;
	$questions[] = $question;
}

// JSON them up and send the response
echo json_encode($questions);
}
// If Dololo
else {
	echo "No questions found";
}
$conn->close();
?>

