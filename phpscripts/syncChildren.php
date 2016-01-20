<?php

//File: syncChildren.php
//Purpose: syncs the local db table children with the remote table 
//Features Incomplete: Security of having the database info hardcoded in 
//Dependencies: LocalDB.java
//Known Bugs: No known bugs
//General Comments: Can possibly refactored more, but due to unfamilarity with PHP it was not done. As well there has not been excessive testing there may be bugs and logical errors 

$response = array();
$dbconn;

// Failure = 0
// Success = 1
// Value has been deleted from remoteDB = 2
// No updates needed = 3
// Local DB is out of date = 4
// Conflicting primary key(s) 5

function unsetResponseArray(){
	unset($response["success"]);
	unset($response["message"]);
}

function DatabaseConnect(){
	$dbconn = pg_connect("host=cmpt371g2.usask.ca dbname=daviddatabase user=DavidThai password=showmethemoney")
    or die('Could not connect: ' . pg_last_error());
}
function insertIntoRemoteDB($id,$firstname,$lastname, $gender, $birthdate, $address, $postalcode, $phonenum, $lastmodified){
	
	// connecting to db
	DatabaseConnect();

	// postgres sql inserting a new row
	$sqlString="INSERT INTO children(child_id, child_firstname, child_lastname, child_gender, child_birthdate, child_address, child_postalcode, child_phonenum, existsinremotedb, lastmodified) VALUES ($id,$firstname,$lastname, $gender, $birthdate, $address, $postalcode, $phonenum, 'True', $lastmodified);";
	$result = pg_query($sqlString);

	// check if row inserted or not
	if ($result) {
		// successfully inserted into database
		$response["success"] = 1;
		$response["message"] = "Institution successfully created.";

		// echoing JSON response
		echo json_encode($response);
		unsetResponseArray();
	} else {
		// failed to insert row
		$response["success"] = 0;
		$response["message"] = "Oops! An errors occurred";

		// echoing JSON response
		echo json_encode($response); 
		unsetResponseArray();
		
	}
} 

/*
 *	The primary keys is  institution id is needed for the where clause
 *	perform an update
 */

function updateRemoteDB($id, $columnName, $columnValue){
	// connecting to db
	DatabaseConnect();
	// and existsInDB='$isInDBCheck'
	$query = "UPDATE children set $columnName='$columnValue' where child_id='$id';";
	$result = pg_query($query) or die('Query failed: ' . pg_last_error());
}

function isDifFromRemoteDB($remoteDBValue, $valueToBeInserted){
	if($remoteDBValue===$valueToBeInserted){
		return false;
	}else{
		return true;
	}
}

function localDBDateisNewer($dateFromRemoteDB, $dateFromLocalDB){
	if( strtotime($dateFromRemoteDB) < strtotime($dateFromLocalDB)){
		return true;
	}
	elseif( strtotime($dateFromRemoteDB) > strtotime($dateFromLocalDB)){
		return false;
	}
}


function queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck){ 
	// connecting to db
	DatabaseConnect();
	
	$query = "SELECT *  FROM children where child_id='$id' and existsinremotedb='$isInDBCheck' ;";
	$result = pg_query($query) or die('Query failed: ' . pg_last_error());
		// user node
	unset($response["success"]);
	unset($response["message"]);
	
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
	
	// temp user array
		
	// In $row[] the actual column names from the remote database must be within the squre brackets
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

	$isNewer=localDBDateisNewer($children["$lastModifiedColName"], $lastmodifiedCheck);
	

	 //	Primary keys is child_id so they are not allowed to be changed

	
	//If data in the local database is newer 
	
	if($isNewer){
	
		//If first name has been updated
		
		if(isDifFromRemoteDB($children["$FnameColName"],$firstnameCheck)){
			updateRemoteDB($id,$children["$FnameColName"],$firstnameCheck);
			
			//Recursive call to check for any other updated values	
			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		} 
		// If phone number has been changed
		elseif(isDifFromRemoteDB($children["$LnameColName"],$lastnameCheck)){
			updateRemoteDB($id, $children["$LnameColName"],$lastnameCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		}
		
		// If privilege of user has been changed
		elseif(isDifFromRemoteDB($children["$genderColName"],$genderCheck)){
			updateRemoteDB($id, $children["$genderColName"],$genderCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		}
		elseif(isDifFromRemoteDB($children["$birthdateColName"], $birthdateCheck)){
	
			updateRemoteDB($id, $children["$birthdateColName"],$birthdateCheck);
		
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		}

		elseif(isDifFromRemoteDB($children["$addressColName"], $addressCheck)){

			updateRemoteDB($id, $children["$addressColName"],$addressCheck);

			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		}
		elseif(isDifFromRemoteDB($children["$postalcodeColName"], $postalcodeCheck)){
			updateRemoteDB($id, $children["$postalcodeColName"],$postalcodeCheck);

			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		}
		elseif(isDifFromRemoteDB($children["$phonenumColName"],$phonenumCheck)){
			updateRemoteDB($id, $children["$phonenumColName"],$phonenumCheck );

			queryRemoteDBForUpdatedValues($id,$firstnameCheck,$lastnameCheck, $genderCheck, $birthdateCheck, $addressCheck, $postalcodeCheck, $phonenumCheck, $isInDBCheck, $lastmodifiedCheck);
		} 
		// Else no other change detected, syncing modified date with remote database		
		else{
			updateRemoteDB($id, $children["$lastModifiedColName"],$lastmModifiedCheck);
			
		} 


	}
	
	//If data in the local database is NOT newer
	elseif(!$isNewer){
		
		array_push($response["children"], $children);
		// success tag for android application to handle
		$response["success"] = 4;
		$response["message"] ="Local DB is not newer";
		echo json_encode($response);
		unsetResponseArray();
	}
	
	//No updates needed
	else{
		unset($response["success"]);
		unset($response["message"]);
		$response["success"]=3;
		$respone["message"]="No updates needed";
		
		// echoing JSON response
		echo json_encode($response);
		unsetResponseArray();
	} 
		
}
function alreadyExistsInRemoteDB($idCheck){
		DatabaseConnect();
		$query = "SELECT *  FROM children where child_id='$idCheck';";
		$result = pg_query($query) or die('Query failed: ' . pg_last_error());
		
		if(pg_num_rows($result)==1){
			return true;
		}
		else {
			return false;
		}
}

//Checking of all the data being sent to the remote database is not null

if (isset($_POST['child_id']) &&  isset($_POST['child_firstname']) && isset($_POST['child_lastname']) && isset($_POST['child_gender']) && isset($_POST['child_birthdate']) && isset($_POST['child_address']) && isset($_POST['child_postalcode']) && isset($_POST['child_phonenum']) && isset($_POST['existsinremotedb']) &&  isset($_POST['lastmodified'])) {
	
		$id = $_POST['child_id'];
		$firstname= $_POST['child_firstname'];
		$lastname = $_POST['child_lastname'];
		$gender = $_POST['child_gender'];
		$birthdate = $_POST['child_birthdate'];
		$address = $_POST['child_address'];
		$postalcode = $_POST['child_postalcode'];
		$phonenum = $_POST['child_phonenum'];
		$existsInDB = $_POST['existsinremotedb'];
		$lastmodified = $_POST['lastmodified'];
		
		if($existsInDB==='True' || $existsInDB==='true'){
			if(alreadyExistsInRemoteDB($id)){
				queryRemoteDBForUpdatedValues($id,$firstname,$lastname, $gender, $birthdate, $address, $postalcode, $phonenum, $existsInDB, $lastmodified);
			}
			//If the value is true but does not exist in the remote database means that the local database is out of date
			elseif(!alreadyExistsInRemoteDB($id)){
				
				$response["success"] = 2;
				$response["message"] = "Value has been deleted from remote database";		
				echo json_encode($response); 
				unsetResponseArray();
			}
			
		}
		elseif($existsInDB==='False' || $existsInDB==='false'){
			if(alreadyExistsInRemoteDB($id)){
				// success tag for android application to handle
				$response = array();
				$response["success"] = 5;
				$response["message"] ="Conflicting Primary Key";
				echo json_encode($response);
				unsetResponseArray();	
			}
			else{
				insertIntoRemoteDB($id,$firstname,$lastname, $gender, $birthdate, $address, $postalcode, $phonenum, $lastmodified);
			}
			
		}
		else{
		    $response["success"] = 0;
			$response["message"] = "An unexpected error occurred";
			echo json_encode($response);
			unsetResponseArray();
		}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing";

    // echoing JSON response
    echo json_encode($response);
	unsetResponseArray();
}

?>