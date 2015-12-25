#!/bin/bash -
#
# This script will build and install the most recent versions
# of the dependencies this project.
#
projectDir=`pwd`
echo "Project Root Directory: $projectDir"

cd "/tmp/"
tempDir="`mktemp -d`"
cd "$tempDir"
tempDir=`pwd`
echo "Temporary Directory for Building Dependencies: $projectDir"

for project in "utils-base"
do
	cd "$tempDir"
	mkdir "$project"
	cd "$project"
	echo "Building '$project' in Directory: `pwd`"
	wget -nv -O "master.tar.gz" "https://codeload.github.com/optimizationBenchmarking/$project/tar.gz/master"
	tar -xf "master.tar.gz"
	cd "$project-master"
	mvn -Dmaven.javadoc.skip=true -Dmaven.test.skip.exec=true compile package install
done

cd "/tmp/"
rm -rf "$tempDir"

cd "$projectDir"
