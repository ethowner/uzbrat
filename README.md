# Simple Device Info App

This example Android application displays basic device information such as the model, Android version and the local IP address.

It no longer requests sensitive permissions or performs background tasks.

The code is provided for demonstration purposes only.

## Web Dashboard

A simple Node.js server in the `server` directory accepts device info and displays
it in a web dashboard. To start it run:

```
node server/server.js
```

The Android app should POST JSON to `http://<server-address>:3000/api/device`
with the following fields:

```json
{ "model": "...", "android": "...", "ip": "..." }
```

Open `http://<server-address>:3000` in a browser to view registered devices.
