# Version 1.1

- Fixing scope of the dependencies from "runtime" to "compile" when published to Maven repository

[teleops-v1.1.zip](https://github.com/pinorobotics/teleops/raw/main/teleops/release/teleops-v1.1.zip)

# Version 1

- Fixing "stdout needs to be consumed first, otherwise method may block forever"
- Accept JRosClient as part of MoveItServoClient constructor
- Adding javadoc support
- Updating copyright
- Adding documentation
- Define TeleopsClient and extract all movements logic under it so it can be used from Java code (JShell)
- Moving TeleopTwistKeyboardApp class to separate package
- Adding support for joint rotation
- Adding debug mode
- Initial commit

[teleops-v1.0.zip](https://github.com/pinorobotics/teleops/raw/main/teleops/release/teleops-v1.0.zip)
