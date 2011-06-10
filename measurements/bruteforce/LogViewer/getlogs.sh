#!/bin/bash

IFS=$'\n'
while read line; do    
    sudo scp -r -i /home/melvold/.ec2/ec2-keypair2.pem ubuntu@$line:/home/ubuntu/hadoop/logs/userlogs/ $line
done <$1
