# Bluetooth Low Energy (BLE) SmartBulb

## Authors
- Pushdeep Gangrade
- Katy Mitchell
- Valerie Ray
- Rockford Stoller

## Project Wiki
- This assignment focuses communicating with a smart BLE bulb that is also capable of beeping and measuring temperature.
- Smart BLE Bulb Android Simulator: You are provided with an Android app that simulates the Smart BLE bulb functionalities. To install the app on an Android phone you should open the following link on an android phone https://www.dropbox.com/s/la97tu8fz4f7iqr/app-release.apk?dl=0 
- Bulb Characteristic (UUID: FB959362-F26E-43A9-927C-7E17D8FB2D8D):	READ/WRITE. Write 0 will turn the bulb OFF Write 1 will turn the bulb ON
- Temperature Characteristic (UUID: 0CED9345-B31F-457D-A6A2-B3DB9B03E39A):	READ/NOTIFY. 
- Beep Characteristic (UUID: EC958823-F26E-43A9-927C-7E17D8F32A90):	READ/WRITE. Write 0 will stop the beeping Write 1 will start the beeping
- This app connects to the Smart BLE bulb by searching for peripherals that provide the SERVICE UUID (as is displayed on the Smart BLE Bulb app provided).
- Users can turn off and on the bulb.
- The app periodically listens to the temperature notifications and updates the displayed temperature.
- Users can trigger the beeping of the bulb by pressing the lightbulb icon.
 
## Video Demo
https://www.youtube.com/watch?v=m2kgSmo4jNc 