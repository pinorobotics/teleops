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
package pinorobotics.teleops.app.keyprocessors;

import id.xfunction.logging.XLogger;
import java.util.List;
import pinorobotics.teleops.TeleopsClient;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JointJogKeyProcessor {
    private static final XLogger LOGGER = XLogger.getLogger(JointJogKeyProcessor.class);
    private double[][] incVelocities, decVelocities;
    private int numOfJoints;
    private TeleopsClient client;
    private boolean isReversed;

    public JointJogKeyProcessor(TeleopsClient client, List<String> joints) {
        this.client = client;
        numOfJoints = joints.size();
        incVelocities = new double[numOfJoints][];
        decVelocities = new double[numOfJoints][];
        for (int i = 0; i < numOfJoints; i++) {
            incVelocities[i] = new double[numOfJoints];
            incVelocities[i][i] = 1;

            decVelocities[i] = new double[numOfJoints];
            decVelocities[i][i] = -1;
        }
    }

    public boolean process(int key) {
        LOGGER.fine("New key request key={0}", key);
        int jointNum = key - '0';
        if (jointNum < 0 || jointNum >= numOfJoints) return false;
        client.move(isReversed ? decVelocities[jointNum] : incVelocities[jointNum]);
        return true;
    }

    public void reverse() {
        isReversed = !isReversed;
    }
}
