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

import static java.util.stream.Collectors.joining;

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import id.jros2messages.control_msgs.JointJogMessage;
import id.jros2messages.geometry_msgs.TwistStampedMessage;
import id.jros2messages.sensor_msgs.JointStateMessage;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosclient.TopicSubscriber;
import id.xfunction.Preconditions;
import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.cli.CommandOptions;
import id.xfunction.function.Unchecked;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import pinorobotics.jros2services.JRos2ServiceClientFactory;
import pinorobotics.jrosservices.std_srvs.TriggerRequestMessage;
import pinorobotics.jrosservices.std_srvs.TriggerServiceDefinition;

public class TeleopTwistKeyboardApp {
    private static final XLogger LOGGER = XLogger.getLogger(TeleopTwistKeyboardApp.class);
    private static final String DEFAULT_TWIST_TOPIC_NAME = "/servo_node/delta_twist_cmds";
    private static final String DEFAULT_JOG_TOPIC_NAME = "/servo_node/delta_joint_cmds";
    private static final String DEFAULT_JOINT_STATES_TOPIC_NAME = "/joint_states";

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
        var twistTopicName = properties.getOption("twistTopic").orElse(DEFAULT_TWIST_TOPIC_NAME);
        var jogTopicName = properties.getOption("jogTopic").orElse(DEFAULT_JOG_TOPIC_NAME);
        var frameName = properties.getOption("frame").orElse("");
        var cli = new CommandLineInterface();
        var twistMessageProvider = new TwistStampedMessageProvider(frameName);
        cli.print("Publishing to the topic %s", twistTopicName);
        try (var client = new JRos2ClientFactory().createClient();
                var publisherTwist =
                        new TopicSubmissionPublisher<>(TwistStampedMessage.class, twistTopicName);
                var publisherJog =
                        new TopicSubmissionPublisher<>(JointJogMessage.class, jogTopicName)) {
            client.publish(publisherTwist);
            client.publish(publisherJog);

            var jointStatesTopic =
                    properties
                            .getOption("jointStatesTopic")
                            .orElse(DEFAULT_JOINT_STATES_TOPIC_NAME);
            var jogKeys = "Jog commands are disabled";
            List<String> joints =
                    properties.isOptionTrue("enableJog")
                            ? readJoints(client, jointStatesTopic)
                            : List.of();
            if (!joints.isEmpty()) {
                jogKeys =
                        """
                        Use numeric keys to jog (rotate) joints in the following order:

                        %s

                        Press 'r' to reverse jog direction.
                        """
                                .formatted(
                                        IntStream.range(0, joints.size())
                                                .mapToObj(i -> i + " - " + joints.get(i))
                                                .collect(joining("\n")));
                if (joints.size() > 9)
                    jogKeys +=
                            "Total number of joints is greater than 6 DOF so some joints are"
                                    + " ignored";
            }
            var jogMessageProvider = new JointJogMessageProvider(frameName, joints);
            cli.print(
                    """

                    Use following keys to send move commands in Cartesian space:

                    Keys w, s - move along x axis
                    Keys a, d - move along y axis
                    Keys q, e - move along z axis

                    %s

                    Additional keys:

                    Ctrl-C - quit
                    """
                            .formatted(jogKeys));
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
                        "Console setup error. Using non interactive console: " + e.getMessage());
            }
            if (properties.getOption("startServo").isPresent()) startServo(client);
            int key = 0;
            boolean isReversed = false;
            while ((key = System.in.read()) != -1) {
                System.out.println("" + key);
                if (key == '\n') continue;
                if (key == 'r') {
                    isReversed = !isReversed;
                    continue;
                }
                twistMessageProvider
                        .get(key)
                        .ifPresent(
                                message -> {
                                    LOGGER.info(message.toString());
                                    // publishing message
                                    publisherTwist.submit(message);
                                });
                jogMessageProvider
                        .get(new JointJogMessageProvider.Command(key - '0', isReversed))
                        .ifPresent(
                                message -> {
                                    LOGGER.info(message.toString());
                                    // publishing message
                                    publisherJog.submit(message);
                                });
            }
        }
    }

    private static List<String> readJoints(JRosClient client, String jointStatesTopic) {
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
