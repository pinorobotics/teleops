**teleops** - Java application for teleoperation of robotic arms in ROS2.

# Requirements

Java 22+

# Download

[Release versions](https://github.com/pinorobotics/teleops/teleops/releases)

# Documentation

[Documentation](http://pinoweb.freetzi.com/teleops)

[Development](DEVELOPMENT.md)

# Usage
```
teleops [ <OPTIONS> ]
```
By default **teleops** is configured to send all commands to MoveIt2 Servo.

Options:

-topic=<string> - topic name where to publish messages (default "/servo_node/delta_twist_cmds")
-frame=<string> - in what frame to operate (default is empty/none)
-startServo=<true|false> - in case of MoveIt2 Servo, before users can send any commands it [needs to be started](https://moveit.picknik.ai/humble/doc/examples/realtime_servo/realtime_servo_tutorial.html#launching-a-servo-node). This option allows to start MoveIt2 Servo before sending any commands to it.
-debug=<true|false> - enable debug mode when all debug logging is stored in "teleops-debug.log" file under system temporary directory

# Contacts

aeon_flux <aeon_flux@eclipso.ch>
