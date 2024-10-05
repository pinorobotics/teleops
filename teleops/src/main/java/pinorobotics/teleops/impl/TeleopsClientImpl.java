/*
 * Copyright 2024 teleops project
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
package pinorobotics.teleops.impl;

import id.jros2messages.control_msgs.JointJogMessage;
import id.jros2messages.geometry_msgs.TwistStampedMessage;
import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosmessages.geometry_msgs.TwistMessage;
import id.jrosmessages.geometry_msgs.Vector3Message;
import id.jrosmessages.primitives.Time;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import java.util.List;
import pinorobotics.teleops.TeleopsClient;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class TeleopsClientImpl implements TeleopsClient {
    private static final XLogger LOGGER = XLogger.getLogger(TeleopsClientImpl.class);
    private static final Vector3Message ZERO_VECTOR = new Vector3Message(0, 0, 0);
    private TopicSubmissionPublisher<TwistStampedMessage> publisherTwist;
    private TopicSubmissionPublisher<JointJogMessage> publisherJog;
    private String frameName;
    private StringMessage[] joints;

    /**
     * @param client
     * @param frameName perform movements in a given frame
     */
    public TeleopsClientImpl(
            JRosClient client,
            String frameName,
            List<String> joints,
            String twistTopicName,
            String jogTopicName) {
        this.frameName = frameName;
        publisherTwist = new TopicSubmissionPublisher<>(TwistStampedMessage.class, twistTopicName);
        publisherJog = new TopicSubmissionPublisher<>(JointJogMessage.class, jogTopicName);
        this.joints =
                joints.stream().map(j -> new StringMessage(j)).toArray(i -> new StringMessage[i]);
        client.publish(publisherTwist);
        client.publish(publisherJog);
    }

    @Override
    public void move(double velX, double velY, double velZ) {
        var message =
                new TwistStampedMessage()
                        .withHeader(
                                new HeaderMessage().withStamp(Time.now()).withFrameId(frameName))
                        .withTwist(
                                new TwistMessage()
                                        .withLinear(new Vector3Message(velX, velY, velZ))
                                        .withAngular(ZERO_VECTOR));
        LOGGER.info(message.toString());
        // publishing message
        publisherTwist.submit(message);
    }

    @Override
    public void move(double... velocities) {
        Preconditions.equals(
                joints.length,
                velocities.length,
                "mismatch between number of velocities and joints");
        var message =
                new JointJogMessage()
                        .withHeader(
                                new HeaderMessage().withStamp(Time.now()).withFrameId(frameName))
                        .withJointNames(joints)
                        .withVelocities(velocities);
        LOGGER.info(message.toString());
        // publishing message
        publisherJog.submit(message);
    }

    @Override
    public void close() throws Exception {
        publisherTwist.close();
        publisherJog.close();
    }
}
