# CMPT371_course_project
A cmpt371 course project (Nutristep Survey Android Application)

Milestone 2: https://github.com/AuntEight/somethingtest (my other github account, Deprecated)

### Keys of this project

* Built an Android mobile application for children health survey
* Position in build master whose jobs are setting up development platform and writing smoke test
* Set up postgreSQL database, phppgadmin and Bitnamin lapp stack on Ubuntu server
* Wrote automatic smoke test in batch script and shell script associated with Android SDK and Robotium
* Managed GitLab and Github branches submission and logging
* Done with Da Tao, Bo Dong, Xingze Guo, Yang zeng, David Thai, Spencer Ondrusek, and Josh Wilson in spring 2014

### Screen Shot

<img src="./screenShot/2014_1_survey_1" alt="preview">
<img src="./screenShot/2014_1_survey_2" alt="preview">

-------------------------------------------------------------

### Log

Update at 2014 April 09 19:07

This is the final milestone for the CMPT371 group 2 project

This project has 4 part
	development code	(cmpt371project)      	application code
	test code       	(cmpt371project_test) 	for test the application
	smoke test code 	(cmpt371project_Smoke)	for daily commit test
	php script      	(phpscripts)          	place them to the remote server side

there are some bugs in each part, but we don't have time to fix
each .java or .sh or .php files have the description on the top line of them
the gitlab (version 6.5.1 2ffa03a) that currently using also has some bugs,

more detail for the bugs would be in the reports 

-------------------------------------------------------------

update at 2014 March 08 17:30

This is the cmpt371 group 2 project for milestone 3

The major project codes are in cmpt371project.
The smoke test codes are in cmpt371project_Smoke.
The test codes are in cmpt371project_Test.

For run the project, 
1), compile the codes in cmpt371project directory
2), make sure an emulator/device connecting
3), install the .apk to a android emulator/device

For run the smoke test,
1), make sure an emulator/device connecting
2), make sure the smoketest.cmd indicate the right emulator/device
3), run smoketest.cmd
4), the smoke test errors are in smoketestdata.txt

For run the test,
1), make sure cmpt371project and cmpt371project_Test are inport to eclipse
2), make sure an emulator/device connecting
3), run android junit test on cmpt371project_Test

-------------------------------------------------

Hi guys,

This is the main branch for our cmpt371 project
It contain the codes from milestone 2 
both develop codes and test codes

All of these file have been tested,
and does not have any big problem.

first uplode 2014-02-23 Sunday
