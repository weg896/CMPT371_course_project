<?php

//File: syncInstitutions.php
//Purpose: syncs the local db table institutions with the remote table 
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
function insertIntoRemoteDB($id,$name,$address, $phonenum, $description, $lastmodified){
	
	// connecting to db
	DatabaseConnect();

	// postgres sql inserting a new row
	$sqlString="INSERT INTO institutions (institution_id, institution_name, institution_address, institution_phonenum, institution_description, existsinremotedb, lastmodified) VALUES('$id','$name','$address', '$phonenum','$description','True','$lastmodified');";
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
	$query = "UPDATE institutions set $columnName='$columnValue' where institution_id='$id' ;";
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


function queryRemoteDBForUpdatedValues($idCheck,$nameCheck,$addressCheck, $phonenumCheck, $descriptionCheck, $isInDBCheck,$lastmodifiedCheck){
	// connecting to db
	DatabaseConnect();
	
	$query = "SELECT *  FROM institutions where institution_id='$id' and existsinremotedb='$isInDBCheck' ;";
	$result = pg_query($query) or die('Query failed: ' . pg_last_error());
	
	$response["institutions"] = array();
 
	$idColName="institution_id";
	$nameColName="institution_name";
	$addressColName="institution_address";
	$phonenumColName="institution_phonenum";
	$descriptionColName="institution_description";
	$existsColName="existsinremotedb";
	$lastModifiedColName="lastmodified";
	

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
	
	$isNewer=localDBDateisNewer($institutions["$lastModifiedColName"], $lastmodifiedCheck);
	
	/*
	 *	Primary keys are username, first name, and last name so they are not allowed to be changed
	 */
	
	//If data in the local database is newer
	if($isNewer){
	
		//If password has been updated
		if(isDifFromRemoteDB($institutions["$nameColName"],$nameCheck)){
			updateRemoteDB($idCheck, $institutions["$nameColName"],$nameCheck);
			
			//Recursive call to check for any other updated values	
			queryRemoteDBForUpdatedValues($idCheck,$nameCheck,$addressCheck, $phonenumCheck, $descriptionCheck, $isInDBCheck,$lastmodifiedCheck);
		}
		
		// If phone number has been changed
		elseif(isDifFromRemoteDB($institutions["$addressColName"],$addressCheck)){
			updateRemoteDB($idCheck, $institutions["$addressColName"],$addressCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($idCheck,$nameCheck,$addressCheck, $phonenumCheck, $descriptionCheck, $isInDBCheck,$lastmodifiedCheck);
		}
		
		// If privilege of user has been changed
		elseif(isDifFromRemoteDB($institutions["$phonenumColName"],$phonenumCheck)){
			updateRemoteDB($idCheck, $institutions["$phonenumColName"],$phonenumCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($idCheck,$nameCheck,$addressCheck, $phonenumCheck, $descriptionCheck, $isInDBCheck,$lastmodifiedCheck);
		}
		elseif(isDifFromRemoteDB($institutions["$descriptionColName"], $descriptionCheck)){
	
			updateRemoteDB($idCheck, $institutions["$descriptionColName"],$descriptionCheck);
		
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($idCheck,$nameCheck,$addressCheck, $phonenumCheck, $descriptionCheck, $isInDBCheck,$lastmodifiedCheck);
		}
		// Else no other change detected, syncing modified date with remote database
		else{
			updateRemoteDB($idCheck, $institutions["$lastModifiedColName"],$lastmModifiedCheck);
			
		}
		

	}
	
	//If data in the local database is NOT newer
	elseif(!$isNewer){
		
		array_push($response["institutions"], $institutions);
		// success tag for android application to handle
		$response["success"] = 4;
		$response["message"] ="Local DB is not newer";
		echo json_encode($response);
		unsetResponseArray();
	}
	
	//No updates needed
	else{
		$response["success"]=3;
		$respone["message"]="No updates needed";
		
		// echoing JSON response
		echo json_encode($response);
		unsetResponseArray();
	}
		

}
function alreadyExistsInRemoteDB($idCheck){
		DatabaseConnect();
		$query = "SELECT *  FROM institution where institution_id='$idCheck' ;";
		$result = pg_query($query) or die('Query failed: ' . pg_last_error());
		
		if(pg_num_rows($result)==1){
			return true;
		}
		else {
			return false;
		}
}

//Checking of all the data being sent to the remote database is not null
if (isset($_POST['institution_id']) ||  isset($_POST['institution_name']) || isset($_POST['institution_address']) || isset($_POST["institution_phonenum"]) || isset($_POST["institution_description"]) || isset($_POST["existsinremotedb"]) || isset($_POST['lastmodified'])) {
	
		$id = $_POST['institution_id'];
		$name= $_POST['institution_name'];
		$address = $_POST['institution_address'];
		$phonenum = $_POST['institution_phonenum'];
		$description = $_POST['institution_description'];
		$existsInDB = $_POST['existsinremotedb'];
		$lastmodified = $_POST['lastmodified'];
		
		if($existsInDB==='True' || $existsInDB==='true'){
			if(alreadyExistsInRemoteDB($id)){
				queryRemoteDBForUpdatedValues($id,$name,$address,$phonenum, $description, $existsInDB, $lastmodified);
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
				$response["success"] =5;
				$response["message"] ="Conflicting Primary Key";
				echo json_encode($response);
				unsetResponseArray();	
			}
			else{
				insertIntoRemoteDB($id,$name,$address, $phonenum, $description, $lastmodified);
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