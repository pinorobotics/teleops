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

import id.jrosclient.JRosClient;
import java.util.List;
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

    /**
     * Creates client which by default publishes all movement commands to MoveIt Servo topics.
     *
     * <p>Users suppose to start MoveIt Servo node before it can process the messages (see {@link
     * MoveItServoClient#startServo(id.jros2client.JRos2Client)})
     *
     * @param client ROS client
     * @param frameName name of the frame where to perform the movements
     * @param joints list of joints can be provided manually or by using {@link
     *     TeleopsUtils#readJoints(JRosClient, String)}
     * @see #DEFAULT_JOG_TOPIC_NAME
     * @see #DEFAULT_TWIST_TOPIC_NAME
     */
    public TeleopsClient createClient(JRosClient client, String frameName, List<String> joints) {
        return new TeleopsClientImpl(
                client, frameName, joints, DEFAULT_TWIST_TOPIC_NAME, DEFAULT_JOG_TOPIC_NAME);
    }

    /**
     * Creates client which by default publishes all movement commands to MoveIt Servo topics
     *
     * @see #DEFAULT_JOG_TOPIC_NAME
     * @see #DEFAULT_TWIST_TOPIC_NAME
     */
    public TeleopsClient createClient(
            JRosClient client,
            String frameName,
            List<String> joints,
            String twistTopicName,
            String jogTopicName) {
        return new TeleopsClientImpl(client, frameName, joints, twistTopicName, jogTopicName);
    }
}
