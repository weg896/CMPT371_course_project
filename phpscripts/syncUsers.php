<?php

//File: syncUsers.php
//Purpose: syncs the local db table users with the remote table 
//Features Incomplete: Secuirty of having the database info hardcoded in 
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
function insertIntoRemoteDB($username,$password,$firstname,$lastname, $phonenum, $privilege, $lastmodified){
	
	// connecting to db
	DatabaseConnect();

	// postgres sql inserting a new row
	$sqlString="INSERT INTO users (username, password, firstname, lastname, phonenum, privilege, existsinremotedb, lastmodified) VALUES('$username','$password','$firstname','$lastname','$phonenum', '$privilege','True','$lastmodified');";
	$result = pg_query($sqlString);

	// check if row inserted or not
	if ($result) {
		// successfully inserted into database
		$response["success"] = 1;
		$response["message"] = "User successfully created.";

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
 *	The primary keys are username, firstname and lastname they are needed for the where clause
 *	perform an update
 */

function updateRemoteDB($username, $firstname, $lastname, $columnName, $columnValue){
	// connecting to db
	DatabaseConnect();
	// and existsInDB='$isInDBCheck'
	$query = "UPDATE users set $columnName='$columnValue' where username='$username' and firstname='$firstnameCheck' and lastname='$lastnameCheck' ;";
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


function queryRemoteDBForUpdatedValues($usernameCheck ,$passwordCheck ,$firstnameCheck ,$lastnameCheck, $phonenumCheck, $privilegeCheck, $isInDBCheck, $lastmModifiedCheck){
	// connecting to db
	DatabaseConnect();
	
	$query = "SELECT *  FROM users where username='$usernameCheck' and firstname='$firstnameCheck' and lastname='$lastnameCheck' and existsInDB='$isInDBCheck' ;";
	$result = pg_query($query) or die('Query failed: ' . pg_last_error());
		
	$response["users"] = array();
	
	$usernameColName="username";
	$passwordColName="password";
	$firstnameColName="firstname";
	$lastnameColName="lastname";
	$phonenumColName="phonenum";
	$privilegeColName="privilege";
	$existsColName="existinremotedb";
	$lastModifiedColName="lastmodified";
	
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
	
	$isNewer=localDBDateisNewer($users["$lastModifiedColName"], $lastmModifiedCheck);
	
	/*
	 *	Primary keys are username, first name, and last name so they are not allowed to be changed
	 */
	
	//If data in the local database is newer
	if($isNewer){
	
		//If password has been updated
		if(isDifFromRemoteDB($users["$passwordColName"],$passwordCheck)){
			updateRemoteDB($usernameCheck,$firstnameCheck ,$lastnameCheck, $users["$passwordColName"],$passwordCheck);
			
			//Recursive call to check for any other updated values	
			queryRemoteDBForUpdatedValues($usernameCheck ,$passwordCheck ,$firstnameCheck ,$lastnameCheck, $phonenumCheck, $privilegeCheck, $isInDBCheck, $lastmModifiedCheck);
		}
		
		// If phone number has been changed
		elseif(isDifFromRemoteDB($users["$phonenumColName"],$phonenumCheck)){
			updateRemoteDB($usernameCheck,$firstnameCheck ,$lastnameCheck, $users["$phonenumColName"],$phonenumCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($usernameCheck ,$passwordCheck ,$firstnameCheck ,$lastnameCheck, $phonenumCheck, $privilegeCheck, $isInDBCheck, $lastmModifiedCheck);
		}
		
		// If privilege of user has been changed
		elseif(isDifFromRemoteDB($users["$privilegeColName"],$privilegeCheck)){
			updateRemoteDB($usernameCheck,$firstnameCheck ,$lastnameCheck, $users["$privilegeColName"],$privilegeCheck);
			
			//Recursive call to check for any other updated values
			queryRemoteDBForUpdatedValues($usernameCheck ,$passwordCheck ,$firstnameCheck ,$lastnameCheck, $phonenumCheck, $privilegeCheck, $isInDBCheck, $lastmModifiedCheck);
		}
		
		// Else no other change detected, syncing modified date with remote database
		else{
			updateRemoteDB($usernameCheck,$firstnameCheck ,$lastnameCheck, $users["$lastModifiedColName"],$lastmModifiedCheck);
			
		}
		

	}
	
	//If data in the local database is NOT newer
	elseif(!$isNewer){
		
		array_push($response["users"], $users);
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
function alreadyExistsInRemoteDB($usernameCheck,$firstnameCheck ,$lastnameCheck){
		DatabaseConnect();
		$query = "SELECT *  FROM users where username='$usernameCheck' and firstname='$firstnameCheck' and lastname='$lastnameCheck' ;";
		$result = pg_query($query) or die('Query failed: ' . pg_last_error());
		
		if(pg_num_rows($result)==1){
			return true;
		}
		else {
			return false;
		}
}


//Checking of all the data being sent to the remote database is not null
if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['firstname']) && isset($_POST["lastname"]) || isset($_POST["phonenum"]) && isset($_POST["privilege"]) && isset($_POST["existsinremotedb"]) || isset($_POST['lastmodified'])) {
	
		$username = $_POST['username'];
		$password= $_POST['password'];
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$phonenum = $_POST['phonenum'];
		$privilege = $_POST['privilege'];
		$existsInDB = $_POST['existsinremotedb'];
		$lastmodified = $_POST['lastmodified'];
		
		if($existsInDB==='True' || $existsInDB==='true'){
			if(alreadyExistsInRemoteDB($username,$firstname,$lastname)){
				queryRemoteDBForUpdatedValues($username,$password,$firstname,$lastname, $phonenum, $privilege, $existsInDB, $lastmodified);
			}
			//If the value is true but does not exist in the remote database means that the local database is out of date
			elseif(!alreadyExistsInRemoteDB($username,$firstname,$lastname)){
				
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
				insertIntoRemoteDB($username,$password,$firstname,$lastname, $phonenum, $privilege, $existsInDB, $lastmodified);
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
