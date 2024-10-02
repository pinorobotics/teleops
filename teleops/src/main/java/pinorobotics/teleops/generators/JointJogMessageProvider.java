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
package pinorobotics.teleops.generators;

import id.jros2messages.control_msgs.JointJogMessage;
import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosmessages.primitives.Time;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.logging.XLogger;
import java.util.List;
import java.util.Optional;

public class JointJogMessageProvider {
    private static final XLogger LOGGER = XLogger.getLogger(JointJogMessageProvider.class);
    private String frameName;
    private StringMessage[] joints;
    private double[][] incVelocities, decVelocities;

    public record Command(int jointNum, boolean isBackward) {}

    public JointJogMessageProvider(String frameName, List<String> joints) {
        this.frameName = frameName;
        this.joints =
                joints.stream().map(j -> new StringMessage(j)).toArray(i -> new StringMessage[i]);
        incVelocities = new double[joints.size()][];
        decVelocities = new double[joints.size()][];
        for (int i = 0; i < joints.size(); i++) {
            incVelocities[i] = new double[joints.size()];
            incVelocities[i][i] = 1;

            decVelocities[i] = new double[joints.size()];
            decVelocities[i][i] = -1;
        }
    }

    public Optional<JointJogMessage> get(Command command) {
        LOGGER.fine("New message request command={0}", command);
        int jointNum = command.jointNum;
        if (jointNum < 0 || jointNum >= joints.length) return Optional.empty();
        return Optional.of(
                new JointJogMessage()
                        .withHeader(
                                new HeaderMessage().withStamp(Time.now()).withFrameId(frameName))
                        .withJointNames(joints)
                        .withVelocities(
                                command.isBackward
                                        ? decVelocities[jointNum]
                                        : incVelocities[jointNum]));
    }
}
