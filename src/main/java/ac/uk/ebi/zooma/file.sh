#!/bin/ksh
file="/users/dipo/Documents/ScAtlas1/ACTUAL_ONTOLOGY/CLO.txt"

while IFS= read line
do

	tmpfile=$(mktemp /tmp/abc-script.txt)
	exec 3>"$tmpfile"
	rm "$tmpfile"
	: ...
	echo foo >&3

        # display $line or do something with $line
	echo "$line"
done <"$file"
