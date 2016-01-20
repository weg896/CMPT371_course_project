<?php
//File: getAllInstitutions.php
//Purpose: pulls the remote database table institutions 
//Features Incomplete: Security of having the database info hardcoded in 
//Dependencies: LocalDB.java
//Known Bugs: No known bugs
//General Comments: Can possibly refactored more, but due to unfamilarity with PHP it was not done. As well there has not been excessive testing there may be bugs and logical errors 

// Connecting, selecting database
$dbconn = pg_connect("host=cmpt371g2.usask.ca dbname=daviddatabase user=DavidThai password=showmethemoney")
	or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT * FROM institutions';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// array for JSON response
$response = array();


// check for empty result
if (pg_num_rows($result) > 0) {
    // looping through all results
    // products node
	$response["institutions"] = array();
	
	$idColName="institution_id";
	$nameColName="institution_name";
	$addressColName="institution_address";
	$phonenumColName="institution_phonenum";
	$descriptionColName="institution_description";
	$existsColName="existsinremotedb";
	$lastModifiedColName="lastmodified";
 
    while ($row = pg_fetch_array($result)) {	
	
		// temp user array
		$institutions = array();
		$institutions["$idColName"] = $row["$idColName"];
		$institutions["$nameColName"] = $row["$nameColName"];
		$institutions["$addressColName"] = $row["$addressColName"];
		$institutions["$phonenumColName"]= $row["$phonenumColName"];
		$institutions["$descriptionColName"] = $row["$descriptionColName"];
		$institutions["$existsColName"] = $row ["$existsColName"];
		$institutions["$lastModifiedColName"] = $row["$lastModifiedColName"];
		
		// push single user into final response array
		array_push($response["institutions"], $institutions);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No institutions found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>