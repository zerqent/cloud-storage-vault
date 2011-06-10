#!/bin/bash

old_filename=$(basename $1)
extension=${old_filename##*.}
old_filename=${old_filename%.*}
new_filename="${old_filename}_${2}.${extension}"

IFS=$'\n'
counter=1
nline=""
while read line; do    
    if [ $[$counter%$2] != 0 ]; then
        nline="$nline$line "
    else
        nline="$nline$line"
        echo $nline >> $new_filename
        nline=""
    fi
    let counter=counter+1
done <$1

if [ "$nline" != "" ]; then
    nline=$(echo ${nline%\ })
    echo $nline >> $new_filename
fi
