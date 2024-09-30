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

import id.jros2messages.geometry_msgs.TwistStampedMessage;
import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosmessages.geometry_msgs.TwistMessage;
import id.jrosmessages.geometry_msgs.Vector3Message;
import id.jrosmessages.primitives.Time;
import java.util.Map;
import java.util.Optional;

public class TwistStampedMessageProvider {
    private static final Map<Character, TwistMessage> KEY_MAP =
            Map.of(
                    'w',
                    new TwistMessage()
                            .withLinear(new Vector3Message(1, 0, 0))
                            .withAngular(new Vector3Message(0, 0, 0)),
                    's',
                    new TwistMessage()
                            .withLinear(new Vector3Message(-1, 0, 0))
                            .withAngular(new Vector3Message(0, 0, 0)),
                    'a',
                    new TwistMessage()
                            .withLinear(new Vector3Message(0, 1, 0))
                            .withAngular(new Vector3Message(0, 0, 0)),
                    'd',
                    new TwistMessage()
                            .withLinear(new Vector3Message(0, -1, 0))
                            .withAngular(new Vector3Message(0, 0, 0)),
                    'q',
                    new TwistMessage()
                            .withLinear(new Vector3Message(0, 0, 1))
                            .withAngular(new Vector3Message(0, 0, 0)),
                    'e',
                    new TwistMessage()
                            .withLinear(new Vector3Message(0, 0, -1))
                            .withAngular(new Vector3Message(0, 0, 0)));
    private String frameName;

    public TwistStampedMessageProvider(String frameName) {
        this.frameName = frameName;
    }

    public Optional<TwistStampedMessage> get(int key) {
        var twist = KEY_MAP.get((char) key);
        if (twist == null) return Optional.empty();
        return Optional.of(
                new TwistStampedMessage()
                        .withHeader(
                                new HeaderMessage().withStamp(Time.now()).withFrameId(frameName))
                        .withTwist(twist));
    }
}
