# Networking easier for Android Wear
##Making a simple network app
It quite takes a lot time for me to manage the communication between device and wear. I mean when you want to call an API, for device, you just need to use functions in available libraries (Aquery, Ion, Volley...), but for Android Wear, I didn't find any library that support it. Then I have to open connection with device  and request and wait to get back the content from them. It takes me quite a lot of time for debugging to understand the guideline. Finally, here is the good news, I got 2 general classes that help us to implement the communication easier.

##General classes

* Device's side

Starting service in the Activity that you want to communicate with wear. Your service will be extend from my base service `BaseDataLayerListenerService` which has 2 important functions:

```
protected abstract void onMessageReceivedSuccess(MessageEvent m);
```

```
protected void sendToPairDevice(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback)
```
The function `onMessageReceivedSuccess` will listen message from wear, and the function `sendToPairDevice` will send message to the connected wear.

* Wear's side

Extending the Activity with class `BaseConnectionWearMobileActivity` to have 3 important functions:

```
protected abstract void onFinishSetupConnectionWearMobile();
```

```
protected abstract void onMessageReceivedSuccess(MessageEvent m);
```

```
protected void sendToPairDevice(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback)
```
The function `onFinishSetupConnectionWearMobile` will tell you when it connected with device, the function `onMessageReceivedSuccess` will listen message from device, and `sendToPairDevice` will send message to the connected device.

## Sample app
You can find my sample app in github: <https://github.com/lthung1504/nice-stop-android-wear>

The app show the list nearby venues around your current location by calling foursquare API from device and show it in the android wear.