#!/bin/sh -x
#
# To get this to work, I had to run the adb commands manually on the device,
# because when turning on USB Tethering, adb devices did not find the device.
# 
# 1. Turn on USB debugging
# 2. Get the IP addresses from the device
# 3. Turn on USB Tethering
# 4. Run the iptables ++ commands on the computer
#

sudo id

android=`adb shell ip addr list usb0 | grep 'inet ' | sed 's/^ *//g'`

a_ip=`echo $android | cut -d" " -f2 | cut -d/ -f1`
c_ip=`echo $a_ip | cut -d. -f-3`.1
broadcast=`echo $android | cut -d" " -f4`

echo "Android $a_ip broadcast $broadcast -> $c_ip"

# Next line was not needed for me:
# sudo ifconfig usb0 $c_ip broadcast $broadcast up

sudo sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"
sudo iptables -t nat -A POSTROUTING -s $a_ip -o eth0 -j MASQUERADE
sudo iptables --append FORWARD --in-interface usb0 -j ACCEPT

adb shell ip route add default via $c_ip
adb shell setprop net.dns1 8.8.8.8

adb shell ping -c 1 $c_ip
ping -c 1 $a_ip
