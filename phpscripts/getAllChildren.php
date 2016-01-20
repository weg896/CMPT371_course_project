<?php

//File: getAllChildren.php
//Purpose: pulls the remote database table children 
//Features Incomplete: Security of having the database info hardcoded in 
//Dependencies: LocalDB.java
//Known Bugs: No known bugs
//General Comments: Can possibly refactored more, but due to unfamilarity with PHP it was not done. As well there has not been excessive testing there may be bugs and logical errors 

// Connecting, selecting database
$dbconn = pg_connect("host=cmpt371g2.usask.ca dbname=daviddatabase user=DavidThai password=showmethemoney")
	or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT * FROM children';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// array for JSON response
$response = array();


// check for empty result
if (pg_num_rows($result) > 0) {

    $response["children"] = array();
	
  	$idColName="child_id";
	$FnameColName="child_firstname";
	$LnameColName="child_lastname";
	$genderColName="child_gender";
	$birthdateColName="child_birthdate";
	$addressColName="child_address";
	$postalcodeColName="child_postalcode";
	$phonenumColName="child_phonenum";
	$existsColName="existsinremotedb";
	$lastModifiedColName="lastmodified";

    // looping through all results
    while ($row = pg_fetch_array($result)) {
		
        // temp user array
		$children = array();
		$children["$idColName"] = $row["$idColName"];
		$children["$FnameColName"] = $row["$FnameColName"];
		$children["$LnameColName"] = $row["$LnameColName"];
		$children["$genderColName"] = $row["$genderColName"];
		$children["$birthdateColName"] = $row["$birthdateColName"];
		$children["$addressColName"] = $row["$addressColName"];
		$children["$postalcodeColName"] = $row["$postalcodeColName"];
		$children["$phonenumColName"] = $row["$phonenumColName"];
		$children["$existsColName"] = $row ["$existsColName"];
		$children["$lastModifiedColName"] = $row["$lastModifiedColName"];
 
		// push single user into final response array
		array_push($response["children"], $children);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No children found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>