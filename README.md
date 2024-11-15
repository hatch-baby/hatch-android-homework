# Hatch Android Homework Project

Hi Candidate,

We're excited to move to the next step of the process with you. This exercise helps us understand how you approach building a simple Android application that consumes a piece of legacy code we’re providing. Please aim to complete the following in 1-2 hours, focusing on the essential parts.

If you have any questions or need clarification, feel free to reach out—it’s OK to ask!

## Evaluation Requirements

- Provide a working project that we can open and run in Android Studio.
- We’ll review your code structure and approach. Use modern architectural patterns and include comments on any interesting parts of the code.
- Code should be written in Kotlin and use Jetpack Compose.
- Note: We know this timeframe is short. Send whatever you have after 2 hours, even if it’s incomplete. We value your time and effort.
- While we are sharing the interface and some documentation with you the actual implementations are obfuscated intentionally, we want to see how well you adapt to dealing with legacy code as well as confirming your debugging skills will let you figure out what needs to be changed.
- We love code and talking about all aspects of software development. We may discuss your project in a follow-up session.
- Be ready to explain how your code works, why you made various (technical) design decisions, and how various areas of the code or functionality can be improved or extended.

## Functional Requirements

### Core Requirements

1. **List Devices**:
    - Use `ConnectivityClient#discoverDevices()` to list available devices.
    - Display basic device information (name, RSSI) and order devices by RSSI, with the strongest signal at the top.
    - Provide an indicator in the UI when data is loading or unavailable.

2. **Refresh List**:
    - The list should update every 10 seconds to reflect any new or removed devices.

3. **Detail Screen (Basic)**:
    - When a device is tapped, show a secondary screen displaying additional device information (name, RSSI). There is no need to perform live updates on this screen.
    - Make use of `ConnectivityClient` operations such as `connectToDeviceBy` to fetch and display this information.

### Nice-to-Have (Only if you have time)

- Once you know the `Device#id` you can perform more `ConnectivityClient` operations such: `connectToDeviceBy`, `updateDeviceName`, `disconnectFromDevice`. 
- Allow the user to rename the device on the detail screen, and make sure the name updates on the list screen when the user returns to it.
- Use the operations as you see fit within your time constraints.

## Additional Information:

- `device-client-lib` is included in the base project in `.aar` format.
- JAR sources are included in the libs folder in case you want to load them in Android Studio directly.