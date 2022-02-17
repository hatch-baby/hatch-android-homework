# Hatch Android Homework Project

Hi Candidate,

We're excited to move to the next step of the process with you. This exercise helps us understand how you would build a simple Android application that will consume a piece of legacy code that we are providing to you in a base Android Studio project. At a high level, you will constantly discover and list a set of devices available to you by our library on the screen, ordering at the top the ones with the stronger signal strength. Additionally, tapping on any of the listed devices should start a secondary screen where more library operations can be performed in order to display all device information on the screen. If you have time as nice to have, allow the user to rename the device from this screen. There are specific detailed instructions below. Please read everything thoroughly before you start. We ask that you don't spend more than 4 hours on this exercise, as we'd like to see what you can complete in the timeframe. If you have any questions or are unsure about something and need clarification, feel free to reach out - it’s OK to ask!

## Evaluation Requirements

- We want to have a working project that we can open and run in Android Studio to test and evaluate.
- We will be evaluating your code, so make sure you structure your project logically and use modern architectural design patterns. Explain interesting bits of code with comments.
- While this code is meant to be evaluated, we obviously do not expect production quality code, comments, etc. Focus on the important areas.
- While we are sharing the interface and some documentation with you the actual implementations are obfuscated intentionally, we want to see how well you adapt to dealing with legacy code as well as confirming your debugging skills will let you figure out what needs to be changed.
- We value your time. This should take around 3 hours, but please do not spend more than 4 - if it’s taking longer, just send us what you have.
- Code should be written using Kotlin (preferred) or Java and modern layout techniques (such as Jetpack Compose, ConstraintLayouts, RecyclerViews, etc).
- We love code and talking about all aspects of software development. We may discuss your project in a follow-up session.
- Be ready to explain how your code works, why you made various (technical) design decisions, and how various areas of the code or functionality can be improved or extended.

## Functional Requirements

- You should be able to list all devices by using `ConnectivityClient#discoverDevices()`, even if there are thousands.
- Keep the list of devices up to date by constantly rediscovering, make sure you call `ConnectivityClient#discoverDevices()` every 10 seconds or so. Your UI should refresh automatically every time the devices get updated.
- The list should display the Devices with the higher RSSI on the top of the list.
- The user should have some information or indication in the UI while particular data is loading or if it is not available. As well as any kind of indicator in failure cases.
- Your Device cell component should show the name, RSSI, and the readable date-time format from when it was last connected.
- Once you know the `Device#id` you can perform more `ConnectivityClient` operations such: `connectToDeviceBy`, `updateDeviceName`, `disconnectFromDevice`. Use these operations in your secondary screen to display all Device information. Device state properties will change over time (while in connected state) so make sure your secondary screen stays up to date with those changes.
- As an optional requirement if you have time, in the secondary screen show the device name in an input field that the user can use to rename it, use `ConnectivityClient#updateDeviceName`, the updated name should be reflected when the user goes back to the list screen.
- Please not that there is no expectation for you to spend time on:
- Tablet layout, just focus on phone layout.
- Localization/globalization.
- Figuring out the underlying `ConnectivityClient` implementation, just work with the code available to you and the provided documentation.
- We may discuss these topics in follow-up conversations, but do not spend time on them in this project.

## Additional Information:

- `device-client-lib` included in the base project in `.aar` format.
- JAR sources are included in the libs folder in case you want to load them in Android Studio directly. Here are the online versions of the interface and model if you have trouble getting access to the sources file: https://gist.github.com/JogahCR/a656357259787d8170e629f5b6e8617a
