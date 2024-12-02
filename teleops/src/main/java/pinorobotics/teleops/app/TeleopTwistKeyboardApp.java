/*
 * Copyright 2022 pinorobotics
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
package pinorobotics.teleops.app;

import static java.util.stream.Collectors.joining;

import id.jros2client.JRos2ClientFactory;
import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.cli.CommandOptions;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import pinorobotics.teleops.MoveItServoClient;
import pinorobotics.teleops.TeleopsClient;
import pinorobotics.teleops.TeleopsClientFactory;
import pinorobotics.teleops.TeleopsUtils;
import pinorobotics.teleops.app.keyprocessors.CartesianMoveKeyProcessor;
import pinorobotics.teleops.app.keyprocessors.JointJogKeyProcessor;
import pinorobotics.teleops.impl.CommandLineInterfaceUtils;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class TeleopTwistKeyboardApp {
    private static final XLogger LOGGER = XLogger.getLogger(TeleopTwistKeyboardApp.class);

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
        LOGGER.fine("Input arguments {0}", properties);
        var twistTopicName =
                properties
                        .getOption("twistTopic")
                        .orElse(TeleopsClientFactory.DEFAULT_TWIST_TOPIC_NAME);
        var jogTopicName =
                properties
                        .getOption("jogTopic")
                        .orElse(TeleopsClientFactory.DEFAULT_JOG_TOPIC_NAME);
        var frameName = properties.getOption("frame").orElse("");
        var jointStatesTopic =
                properties
                        .getOption("jointStatesTopic")
                        .orElse(TeleopsUtils.DEFAULT_JOINT_STATES_TOPIC_NAME);
        try (var client = new JRos2ClientFactory().createClient()) {
            List<String> joints =
                    properties.isOptionTrue("enableJog")
                            ? new TeleopsUtils().readJoints(client, jointStatesTopic)
                            : List.of();
            if (properties.getOption("startServo").isPresent())
                new MoveItServoClient(client).startServo();
            try (var teleopsClient =
                    new TeleopsClientFactory()
                            .createClient(
                                    client, frameName, joints, twistTopicName, jogTopicName)) {
                run(frameName, joints, teleopsClient);
            }
        }
    }

    private static void run(String frameName, List<String> joints, TeleopsClient teleopsClient)
            throws IOException {
        var cli = new CommandLineInterface();
        var jogKeys = "Jog commands are disabled (see -enableJog option)";
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
        cli.print(
                """

                Use following keys to send move commands in Cartesian space:

                Keys w, s - move along x axis
                Keys a, d - move along y axis
                Keys q, e - move along z axis

                %s

                Additional keys:

                Ctrl-C - quit

                To see more help, run 'teleops' with -h option
                """
                        .formatted(jogKeys));
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    CommandLineInterfaceUtils.echo(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        try {
            CommandLineInterfaceUtils.echo(false);
            CommandLineInterfaceUtils.nonBlockingSystemInput();
        } catch (Exception e) {
            cli.printerr(
                    "Console setup error. Switching to non interactive console: " + e.getMessage());
        }

        var jointJogKeyProcessor = new JointJogKeyProcessor(teleopsClient, joints);
        var cartesianMoveKeyProcessor = new CartesianMoveKeyProcessor(teleopsClient);
        int key = 0;
        while ((key = System.in.read()) != -1) {
            if (key == '\n') continue;
            if (key == 'r') {
                jointJogKeyProcessor.reverse();
                continue;
            }
            if (cartesianMoveKeyProcessor.process(key)) continue;
            if (jointJogKeyProcessor.process(key)) continue;
        }
    }
}
