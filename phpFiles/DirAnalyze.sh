#! /bin/csh 
#Author : Marcus Gabilheri
#Section: 401 
#Description : this program analyzes all the .java and .class files in a directory and tells
# the number of the files and another information like permissions, etc..  

echo "<< CS2351 Directory Analysis >> Date:" `date |cut -c1-20`
echo '================================================================'
 
## set variables 
set dirCount = 0 
set plainCount = 0 
set execPermission = 0 
set readPermission = 0
set writePermission = 0
set zeroLength = 0 
set fileNum = 0
set diskUsage = 0	
set currentDir = `pwd` 
set currentFile
set newDir = $1 
set a = 0
set finalArray = 

echo "A. Current Directory: " `pwd` 

mkdir $newDir 

echo "New dir:" $newDir
echo "B. New Directory Created: " `pwd`"/"$newDir 

cp $currentDir/*.java $newDir 
cp $currentDir/*.class $newDir 
 
## Change directory to pgm02 
 
cd $newDir 
echo "C. File Information"
##count number file in your new Directory 
 
foreach file (*) 
	#echo $file
	@ fileNum++
	set currentFile = `du -b "$file" | cut -f1`

	@ diskUsage = $diskUsage + $currentFile

	set finalArray = ( $finalArray $currentFile )

	if( -d $file ) then 
		@ dirCount++
	else if( -f $file ) then 
		@ plainCount++ 
	else 
		@ zeroLength=0 
	endif 

	# Different if statements because a file might have all three permissions. 
	# If i use only else if statements only of them is executed.
	if ( -w $file ) then
		@ writePermission++
	endif
	if ( -r $file ) then
		@ readPermission++
	endif
	if ( -x $file ) then
		@ execPermission++
	endif
end 
 	
 	set array = `ls --sort=size`
 	echo "\t0) Total Number of files: " $fileNum "files" 
	echo "\t1) Directory files: " $dirCount 
	echo "\t2) Plain text files: " $plainCount 
	echo "\t3) File have read permissions: " $readPermission 
	echo "\t4) File have write permissions: " $writePermission 
	echo "\t5) File have execute permissions : " $execPermission
	echo "\t6) File have length of zero: " $zeroLength
	echo "E. Disk Usage: " $diskUsage
	echo "F. 5 Biggest Files in Directory: "

	set increment = 1

	while ($increment < 6)
		echo "\t"`du -b "$array[$increment]" | cut -f1` "\t" $array[$increment]
		@ increment++
	end






