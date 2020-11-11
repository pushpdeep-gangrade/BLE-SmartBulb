# Bluetooth Low Energy (BLE) SmartBulb

## Authors
- Pushdeep Gangrade
- Katy Mitchell
- Valerie Ray
- Rockford Stoller

# Project Wiki

## App Information
- This app communicates with a smart BLE bulb and is also capable of beeping and measuring temperature.
- Smart BLE Bulb Android Simulator: The video demo uses an Android app that simulates Smart BLE bulb functionalities. To install the app on an Android phone, go to https://www.dropbox.com/s/la97tu8fz4f7iqr/app-release.apk?dl=0 
- Bulb Characteristic (READ/WRITE):	Write 0 will turn the bulb OFF and Write 1 will turn the bulb ON
- Temperature Characteristic (READ/NOTIFY)
- Beep Characteristic (READ/WRITE): Write 0 will stop the beeping and Write 1 will start the beeping
- This app connects to the Smart BLE bulb by searching for peripherals that provide the SERVICE UUID (as is displayed on the Smart BLE Bulb app provided).
- The app periodically listens to the temperature notifications and updates the displayed temperature.


## User Side
- When the app is run for the first time, the user is prompted to allow app permissions, such as wifi and bluetooth.
- Users can turn off and on the bulb.
- Users can trigger the beeping of the bulb by pressing "Beep."
<br>
<img src="https://github.com/pushpdeep-gangrade/Chatroom/blob/master/screen_images/Login.png" width=150>
</br>
- Users can turn the bulb on and off by pressing "ON" and "OFF."
<br>
<img src="https://github.com/pushpdeep-gangrade/Chatroom/blob/master/screen_images/Login.png" width=150>
 </br>
 
## Video Demo
https://www.youtube.com/watch?v=m2kgSmo4jNc 