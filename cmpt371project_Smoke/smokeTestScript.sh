#!/bin/bash

#File: smokeTestScript.sh
#Purpose: for run the smoke test code 
#Features Completed: test the exist device that specify, build the code, 
#					install the code, run the somke test
#Features Incomplete: the deceiveID need to change (use "adb devices" in command line) 
#Dependencies: all development code and smoke test code, apache ant, 
# file ¡°android¡± from android sdk linux version and it should place in sdk directory/skd/tools/ folder
#Known Bugs: none
#General Comments: 
# can use TortoiseGit as trigger, or copy to /.git/hooks and rename to "pre-push"
# there are three log files will be created by running this script
# smoketestlog.txt -- the log contain the newest ran smoke test case info 
# smoketestcode.txt -- just for temperary stroing the specific number, use for determining next step
# autoLog.txt -- the compile/debug/update log that from the offical command will go to this file

#make sure in the right path
cd cmpt371project_Smoke

# the deceive that for using in the smoke test
deviceID="D6OKCY036715"

# test a specific deceive is already started and connected
adb -s $deviceID shell getprop sys.boot_completed > smoketestcode.txt

# read and write the connection code twice 
# so that to filter some useless charactors
read connection_code < smoketestcode.txt
echo $connection_code > smoketestcode.txt 
read real_connection_code < smoketestcode.txt

exceptedConnectCode="1"
if [ $exceptedConnectCode != $real_connection_code ]; then
	echo "smoke test-------can not connect to $deviceID"
	echo "smoke test-------please make sure that the emulator/deceive already started."
	exit 1
fi

echo "smoke test-------connect to $deviceID success"
echo "smoke test-------Now, compile and install the development part......"

##############################################################################

expectedExitCode=0

# go to development directory
# to make sure under the right directory
cd ../cmpt371project/

# clean the old development apk files
# the old files could let the compile failure
test -e bin/cmpt371-debug.apk > autoLog.txt
if [ $expectedExitCode -eq $? ]; then 
	echo "smoke test-------clean the old development apk files"
	ant clean >> autoLog.txt
fi

# update development files such as *.properties and the *.xml
# make sure these files are associated with the local envirement
android update project -p ./ -t 1 >> autoLog.txt
if [ $expectedExitCode -ne $? ]; then 
	echo "smoke test-------update the development files fail"
	exit 1
fi

# compile the development codes
ant debug >> autoLog.txt
if [ $expectedExitCode -ne $? ]; then
	echo "smoke test-------compile development codes error"
	echo "smoke test-------is it the original project path name cmpt371project"
	echo "smoke test-------is it the project folder and the test folder under the same directory"
	exit 1
fi

# install the development code to the deveice
adb -s $deviceID install -r bin/cmpt371-debug.apk >> autoLog.txt
if [ $expectedExitCode -ne $? ]; then
	echo "smoke test-------cannot install the original project apk to deceive $deviceID"
	echo "smoke test-------or go to and run \cmpt371project\development.cmd" 
	echo "smoke test-------the batch script calling has some problem "
	exit 1
fi

echo "smoke test-------development code finish compile and install"
echo "smoke test-------Now, compile and install the test part......"

##############################################################################

# go to smoke test directory
# to make sure under the right directory
cd ../cmpt371project_Smoke/

# clean the old test apk file
# the old files could let the compile failure
test -e bin/cmpt371project_SmokeTest-debug.apk >> autoLog.txt
if [ $expectedExitCode -eq $? ]; then
	echo "smoke test-------clean the old test apk files"
	ant clean >> autoLog.txt
fi

# update test files such as *.properties and the *.xml
# make sure these files are associated with the local envirement
android update test-project -p ./ -m ../cmpt371project/ >> autoLog.txt

# compile the test codes
ant debug >> autoLog.txt
if [ $expectedExitCode -ne $? ]; then
	echo "smoke test-------compile test codes error"
	echo "smoke test-------is it the original project path name cmpt371project"
	echo "smoke test-------is it the project folder and the test folder under the same directory"
	exit 1
fi

# install the test codes to the deceive 
adb -s $deviceID install -r bin/cmpt371project_SmokeTest-debug.apk >> autoLog.txt
if [ $expectedExitCode -ne $? ]; then
	echo "smoke test-------cannot install the test apk to deceive $deviceID"
	exit 1
fi

echo "smoke test-------test code finish compile and install"
echo "smoke test-------Now, smoke testing......"

##############################################################################

# run the smoke test

# use the array to stall smoke test cases
testCase[0]="AdminTest"
testCase[1]="ChildrenListTest"
testCase[2]="LocationListTest"
testCase[3]="LoginTest"
testCase[4]="ResTest"

expectedLineCount=8

# run all smoke test cases which in the array
# the via adb, call the test function then the output will goto "smoketestlog"
# get the smoke test output and count them, then compare with the expected counting value.  
for i in {0..4}
do
	adb -s $deviceID shell am instrument -e class com.example.cmpt371project.test.${testCase[$i]} -w com.example.cmpt371project.test/android.test.InstrumentationTestRunner > smoketestlog.txt
	wc -l smoketestlog.txt | cut -d " " -f 7 > smoketestcode.txt
	read line_count < smoketestcode.txt 
	if [ $expectedLineCount -ne $line_count ]; then
		echo "smoke test-------fail in ${testCase[$i]} edit test"
		exit 1
	fi
done

echo "smoke test-------Finish, every thing is fine"
exit 0
