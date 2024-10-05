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
package pinorobotics.teleops;

import id.jros2client.JRos2Client;
import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import pinorobotics.jros2services.JRos2ServiceClientFactory;
import pinorobotics.jrosservices.std_srvs.TriggerRequestMessage;
import pinorobotics.jrosservices.std_srvs.TriggerServiceDefinition;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MoveItServoClient {
    private static final XLogger LOGGER = XLogger.getLogger(MoveItServoClient.class);

    /** Send start request to servo_node */
    public void startServo(JRos2Client client) {
        try (var service =
                new JRos2ServiceClientFactory()
                        .createClient(
                                client,
                                new TriggerServiceDefinition(),
                                "/servo_node/start_servo")) {
            var response =
                    service.sendRequestAsync(new TriggerRequestMessage()).get(1, TimeUnit.MINUTES);
            LOGGER.fine("Servo node response: {0}", response);
            Preconditions.isTrue(
                    response.success, "Could not start servo node, reponse " + response);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Could not start servo node", e);
        }
    }
}
