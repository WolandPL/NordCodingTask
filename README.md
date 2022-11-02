# NordCodingTask

## Overview
This amazing application records information about incoming calls and makes it available via HTTP srever.

## Requirements
The following Android permissions are required:
`READ_PHONE_STATE, READ_CALL_LOG, READ_CONTACTS, INTERNET, ACCESS_NETWORK_STATE`

Besides that, on API 31 and above, `ROLE_CALL_SCREENING` role is required.

## HTTP API
HTTP server starts on app startup. After that, its address is displayed on the main screen.

The following network requests are supported by the app:

### root
e.g. `192.168.1.102:38067`

shows available services:
```json
{
    "start": "2022-11-02T16:06:28+0100",
    "services": [
        {
            "name": "status",
            "url": "http:///192.168.1.102:38067/status"
        },
        {
            "name": "log",
            "url": "http:///192.168.1.102:38067/log"
        }
    ]
}
```

### status
e.g. `192.168.1.102:38067/status`

shows the status of current ongoing call:
```json
{
    "ongoing": true,
    "number": "608387964"
}
```

### log
e.g. `192.168.1.102:38067/log`

shows all information about all logged phone calls

```json
[
    {
        "beginning": "2022-11-02T16:11:29+0100",
        "number": "608387964",
        "duration": 8,
        "timesQueried": 1
    }
]
```

## Tests
Sample tests are added in `data` and `app` modules.
`app` module contains UI tests - they couldn't be placed in the `ui` module because of the app architecture.

**CAUTION:**
Before running the UI tests (from the `app` module) on API 31+, please grant the `ROLE_CALL_SCREENING` role to the application (install and start it to see the required system dialog).
Permissions are automatically granted by the tests, but it's not possible for a role.
