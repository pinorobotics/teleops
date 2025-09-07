/*
 * Copyright 2024 pinorobotics
 * 
 * Website: https://github.com/pinorobotics/teleops
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
package pinorobotics.teleops;

import id.jros2client.JRos2Client;
import id.jrosclient.JRosClient;
import id.jroscommon.RosRelease;
import java.util.List;
import pinorobotics.jros2moveit.JRos2MoveItFactory;
import pinorobotics.teleops.impl.HumbleMoveItServoTeleopsClient;
import pinorobotics.teleops.impl.JazzyMoveItServoTeleopsClient;
import pinorobotics.teleops.impl.TeleopsClientImpl;

/**
 * Factory methods for {@link TeleopsClient}
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class TeleopsClientFactory {

    /**
     * Default twist topic is a default topic of MoveIt Servo
     *
     * <p>Generally MoveIt Servo subscribes to this topic and process any incoming messages.
     */
    public static final String DEFAULT_TWIST_TOPIC_NAME = "/servo_node/delta_twist_cmds";

    /**
     * Default jog topic is a default topic of MoveIt Servo
     *
     * <p>Generally MoveIt Servo subscribes to this topic and process any incoming messages.
     */
    public static final String DEFAULT_JOG_TOPIC_NAME = "/servo_node/delta_joint_cmds";

    private JRos2MoveItFactory factory = new JRos2MoveItFactory();

    /** Creates generic {@link TeleopsClient} */
    public TeleopsClient createClient(
            JRosClient client,
            String frameName,
            List<String> joints,
            String twistTopicName,
            String jogTopicName) {
        return new TeleopsClientImpl(client, frameName, joints, twistTopicName, jogTopicName);
    }

    /**
     * Creates {@link TeleopsClient} for MoveIt Servo.
     *
     * <p>This client by default publishes all movement commands to MoveIt Servo topics.
     *
     * <p>In case of {@link RosRelease#ROS2_HUMBLE} it automatically starts MoveIt Servo node before
     * sending the messages (see {@link MoveItServoClient#startServo()})
     *
     * <p>In case of {@link RosRelease#ROS2_JAZZY} it automatically calls MoveIt Servo to switch to
     * the correct command type (see {@link MoveItServoClient#switchCommandType()})
     *
     * @param client ROS client
     * @param frameName name of the frame where to perform the movements
     * @param joints list of joints can be provided manually or by using {@link
     *     TeleopsUtils#readJoints(JRosClient, String)}
     * @see #DEFAULT_JOG_TOPIC_NAME
     * @see #DEFAULT_TWIST_TOPIC_NAME
     */
    public TeleopsClient createClientForServo(
            JRos2Client client, RosRelease rosRelease, String frameName, List<String> joints) {
        var teleopsClient =
                new TeleopsClientImpl(
                        client,
                        frameName,
                        joints,
                        DEFAULT_TWIST_TOPIC_NAME,
                        DEFAULT_JOG_TOPIC_NAME);
        var servoClient = factory.createMoveItServoClient(client, rosRelease);
        return switch (rosRelease) {
            case ROS2_HUMBLE ->
                    new HumbleMoveItServoTeleopsClient(teleopsClient, servoClient, false);
            default -> new JazzyMoveItServoTeleopsClient(teleopsClient, servoClient);
        };
    }

    /**
     * Creates {@link TeleopsClient} for MoveIt Servo running in ROS2 Jazzy
     *
     * @see #createClientForServo(JRos2Client, RosRelease, String, List)
     */
    public TeleopsClient createJazzyClientForServo(
            JRos2Client client, String frameName, List<String> joints) {
        var teleopsClient =
                new TeleopsClientImpl(
                        client,
                        frameName,
                        joints,
                        DEFAULT_TWIST_TOPIC_NAME,
                        DEFAULT_JOG_TOPIC_NAME);
        var servoClient = factory.createMoveItServoClient(client, RosRelease.ROS2_JAZZY);
        return new JazzyMoveItServoTeleopsClient(teleopsClient, servoClient);
    }

    /**
     * Creates {@link TeleopsClient} for MoveIt Servo running in ROS2 Humble
     *
     * @see #createClientForServo(JRos2Client, RosRelease, String, List)
     */
    public TeleopsClient createHumbleClientForServo(
            JRos2Client client, String frameName, List<String> joints, boolean isServoStarted) {
        var teleopsClient =
                new TeleopsClientImpl(
                        client,
                        frameName,
                        joints,
                        DEFAULT_TWIST_TOPIC_NAME,
                        DEFAULT_JOG_TOPIC_NAME);
        var servoClient = factory.createMoveItServoClient(client, RosRelease.ROS2_HUMBLE);
        return new HumbleMoveItServoTeleopsClient(teleopsClient, servoClient, isServoStarted);
    }
}
