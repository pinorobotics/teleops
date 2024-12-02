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

import id.jros2messages.sensor_msgs.JointStateMessage;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubscriber;
import id.xfunction.function.Unchecked;
import id.xfunction.logging.XLogger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SynchronousQueue;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class TeleopsUtils {
    private static final XLogger LOGGER = XLogger.getLogger(TeleopsUtils.class);

    /**
     * Default topic which is used by joint_state_broadcaster and robot_state_publisher nodes to
     * publish the joint states
     */
    public static final String DEFAULT_JOINT_STATES_TOPIC_NAME = "/joint_states";

    /**
     * Reads all joints from the {@link JointStateMessage} messages published to the joint topic.
     *
     * @param client
     * @param jointStatesTopic see {@link #DEFAULT_JOINT_STATES_TOPIC_NAME}
     * @return
     */
    public List<String> readJoints(JRosClient client, String jointStatesTopic) {
        var q = new SynchronousQueue<List<String>>();
        client.subscribe(
                new TopicSubscriber<>(JointStateMessage.class, jointStatesTopic) {
                    @Override
                    public void onNext(JointStateMessage item) {
                        try {
                            q.put(Arrays.asList(item.name));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getSubscription().ifPresent(Subscription::cancel);
                    }
                });
        LOGGER.info(
                "Waiting for joints to be published to {0} (use disableJog option to change"
                        + " this)",
                jointStatesTopic);
        return Unchecked.get(q::take);
    }
}
