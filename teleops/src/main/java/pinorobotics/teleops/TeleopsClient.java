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

import id.jros2messages.control_msgs.JointJogMessage;
import id.jros2messages.geometry_msgs.TwistStampedMessage;

/**
 * @see TeleopsClientFactory
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public interface TeleopsClient extends AutoCloseable {

    /**
     * Move robot in Cartesian space by publishing {@link TwistStampedMessage} with a given
     * velocities (m/s).
     */
    void move(double velX, double velY, double velZ);

    /**
     * Move robot joints by publishing {@link JointJogMessage}
     *
     * <p>Velocities for all joints must be specified.
     *
     * <p>For example, following velocities will move only first joint of the 6 DOF robot in the
     * opposite direction: 0, -1, 0, 0, 0, 0
     *
     * <p>See {@link TeleopsUtils#readJoints(id.jrosclient.JRosClient, String)} to find number of
     * joints available in the robot (in case number of joints is not known in advance)
     */
    void move(double... velocities);
}
