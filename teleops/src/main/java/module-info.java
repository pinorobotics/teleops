/*
 * Copyright 2021 jrosmoveit project
 *
 * Website: https://github.com/pinorobotics/jrosmoveit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * <b>teleops</b> - Java module for teleoperation of robotic arms in ROS2.
 *
 * <ul>
 *   <li>Allows to publish {@link
 *       id.jros2messages.geometry_msgs.TwistStampedMessage.TwistStampedMessage} and {@link
 *       id.jros2messages.control_msgs.JointJogMessage} messages to ROS topics
 *   <li>Provides client to MoveIt2 Servo API (see {@link pinorobotics.teleops.MoveItServoClient})
 * </ul>
 *
 * @see <a href="http://pinoweb.freetzi.com/teleops">Documentation</a>
 * @see <a
 *     href="https://github.com/pinorobotics/teleops/blob/main/teleops/release/CHANGELOG.md">Download</a>
 * @see <a href="https://github.com/pinorobotics/teleops">GitHub repository</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
module teleops {
    requires jros2client;
    requires jros2services;
    requires id.xfunction;
    requires jrosclient;
    requires jros2messages;
    requires jrosservices;

    exports pinorobotics.teleops;
}
