**teleops** - Java module for teleoperation of robotic arms in ROS2.
- Allows to publish geometry_msgs/TwistStamped and control_msgs/JointJog messages to ROS topics
- provides client to MoveIt2 Servo API (MoveItServoClient)

Users can use **teleops** in one of two ways:
- Java module/library, by calling **teleops** Java API from Java code or JShell
- standalone command-line application

# Requirements

- Java 22+

# Download

[Release versions](teleops/release/CHANGELOG.md)

Or if you plan to use it from Java code, you can add dependency to it as follows:

Gradle:

```
dependencies {
  implementation 'io.github.pinorobotics:teleops:1.0'
}
```

# Documentation

[Documentation](http://pinoweb.freetzi.com/teleops)

[Development](DEVELOPMENT.md)

# Usage
```
teleops [ <OPTIONS> ]
```

Options:
```
-twistTopic=<string>
-jogTopic=<string>
-jointStatesTopic=<string>
-enableJog=<true|false>
-frame=<string>
-startServo=<true|false>
-debug=<true|false>
```

See [Documentation](http://pinoweb.freetzi.com/teleops) for option details and examples.

# Contacts

aeon_flux <aeon_flux@eclipso.ch>
