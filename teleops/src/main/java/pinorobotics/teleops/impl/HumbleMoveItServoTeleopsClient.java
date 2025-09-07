/*
 * Copyright 2025 pinorobotics
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

import pinorobotics.jros2moveit.JRos2MoveItServoClient;
import pinorobotics.teleops.TeleopsClient;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class HumbleMoveItServoTeleopsClient implements TeleopsClient {
    private TeleopsClient client;
    private JRos2MoveItServoClient servoClient;
    private boolean isServoStarted;

    public HumbleMoveItServoTeleopsClient(
            TeleopsClient client, JRos2MoveItServoClient servoClient, boolean isServoStarted) {
        this.client = client;
        this.servoClient = servoClient;
        this.isServoStarted = isServoStarted;
    }

    @Override
    public void close() {
        servoClient.close();
    }

    @Override
    public void move(double velX, double velY, double velZ) {
        if (!isServoStarted) {
            servoClient.startServo();
            isServoStarted = true;
        }
        client.move(velX, velY, velZ);
    }

    @Override
    public void move(double... velocities) {
        if (!isServoStarted) {
            servoClient.startServo();
            isServoStarted = true;
        }
        client.move(velocities);
    }
}
