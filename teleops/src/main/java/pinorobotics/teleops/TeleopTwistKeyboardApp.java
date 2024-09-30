/*
 * Copyright 2022 teleops project
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
import id.jros2client.JRos2ClientFactory;
import id.jros2messages.geometry_msgs.TwistStampedMessage;
import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosmessages.geometry_msgs.TwistMessage;
import id.jrosmessages.geometry_msgs.Vector3Message;
import id.jrosmessages.primitives.Time;
import id.xfunction.Preconditions;
import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.cli.CommandOptions;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import pinorobotics.jros2services.JRos2ServiceClientFactory;
import pinorobotics.jrosservices.std_srvs.TriggerRequestMessage;
import pinorobotics.jrosservices.std_srvs.TriggerServiceDefinition;

public class TeleopTwistKeyboardApp {
    private static final XLogger LOGGER = XLogger.getLogger(TeleopTwistKeyboardApp.class);
    private static String DEFAULT_TOPIC_NAME = "/servo_node/delta_twist_cmds";
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

    private static void usage() throws IOException {
        new ResourceUtils().readResourceAsStream("README-teleops.md").forEach(System.out::println);
    }

    public static void main(String... args) throws Exception {
        CommandOptions properties = null;
        try {
            properties = CommandOptions.collectOptions(args);
            if (properties.getOption("h").isPresent() || properties.getOption("help").isPresent())
                throw new ArgumentParsingException("");
        } catch (ArgumentParsingException e) {
            usage();
            return;
        }
        XLogger.load(
                properties.isOptionTrue("debug")
                        ? "logging-teleops-debug.properties"
                        : "logging-teleops.properties");
        var topicName = properties.getOption("topic").orElse(DEFAULT_TOPIC_NAME);
        var frameName = properties.getOption("frame").orElse("");
        var cli = new CommandLineInterface();
        cli.print("Publishing to the topic %s", topicName);
        try (var client = new JRos2ClientFactory().createClient();
                var publisher =
                        new TopicSubmissionPublisher<>(TwistStampedMessage.class, topicName)) {
            client.publish(publisher);

            cli.print(
                    """

                    Use keys to send move commands:

                    Keys w, s - move along x axis
                    Keys a, d - move along y axis
                    Keys q, e - move along z axis

                    Ctrl-C - quit
                    """);
            Runtime.getRuntime()
                    .addShutdownHook(
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        CommandLineInterface.echo(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
            try {
                CommandLineInterface.echo(false);
                CommandLineInterface.nonBlockingSystemInput();
            } catch (Exception e) {
                cli.printerr(
                        "Console setup error. Using non interactive console : " + e.getMessage());
            }
            if (properties.getOption("startServo").isPresent()) startServo(client);
            int key = 0;
            while ((key = System.in.read()) != -1) {
                if (key == '\n') continue;
                var twist = KEY_MAP.get((char) key);
                if (twist == null) continue;
                var message =
                        new TwistStampedMessage()
                                .withHeader(
                                        new HeaderMessage()
                                                .withStamp(Time.now())
                                                .withFrameId(frameName))
                                .withTwist(twist);
                LOGGER.info(message.toString());
                // publishing message
                publisher.submit(message);
            }
        }
    }

    private static void startServo(JRos2Client client) {
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
