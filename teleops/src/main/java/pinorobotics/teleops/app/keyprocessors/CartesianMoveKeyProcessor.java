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
package pinorobotics.teleops.app.keyprocessors;

import pinorobotics.teleops.TeleopsClient;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class CartesianMoveKeyProcessor {
    private TeleopsClient client;

    public CartesianMoveKeyProcessor(TeleopsClient client) {
        this.client = client;
    }

    public boolean process(int key) {
        switch (key) {
            case 'w':
                {
                    client.move(1, 0, 0);
                    return true;
                }
            case 's':
                {
                    client.move(-1, 0, 0);
                    return true;
                }
            case 'a':
                {
                    client.move(0, 1, 0);
                    return true;
                }
            case 'd':
                {
                    client.move(0, -1, 0);
                    return true;
                }
            case 'q':
                {
                    client.move(0, 0, 1);
                    return true;
                }
            case 'e':
                {
                    client.move(0, 0, -1);
                    return true;
                }
            default:
                return false;
        }
    }
}
