<?php
//File: getAllUsers.php
//Purpose: pulls the remote database table users 
//Features Incomplete: Security of having the database info hardcoded in 
//Dependencies: LocalDB.java
//Known Bugs: No known bugs
//General Comments: Can possibly refactored more, but due to unfamilarity with PHP it was not done. As well there has not been excessive testing there may be bugs and logical errors 

// Connecting, selecting database
$dbconn = pg_connect("host=cmpt371g2.usask.ca dbname=daviddatabase user=DavidThai password=showmethemoney") 
	or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = "SELECT *  FROM users;";
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// array for JSON response
$response = array();


// check for empty result
if (pg_num_rows($result) > 0) {
	

    $response["users"] = array();
	$usernameColName="username";
	$passwordColName="password";
	$firstnameColName="firstname";
	$lastnameColName="lastname";
	$phonenumColName="phonenum";
	$privilegeColName="privilege";
	$existsColName="existsinremotedb";
	$lastModifiedColName="lastmodified";
	
	// looping through all results
    while ($row = pg_fetch_array($result)) {
		// temp user array
		$users = array();
		$users["$usernameColName"] = $row["$usernameColName"];
		$users["$passwordColName"] = $row["$passwordColName"];
		$users["$firstnameColName"] = $row["$firstnameColName"];
		$users["$lastnameColName"] = $row["$lastnameColName"];
		$users["$phonenumColName"]= $row["$phonenumColName"];
		$users["$privilegeColName"] = $row["$privilegeColName"];
		$users["$existsColName"] = $row ["$existsColName"];
		$users["$lastModifiedColName"] = $row["$lastModifiedColName"];
		
		// push single user into final response array
		array_push($response["users"], $users);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No users found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>