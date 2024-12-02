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
package pinorobotics.teleops.impl;

import id.xfunction.cli.CommandLineInterface;
import id.xfunction.lang.XExec;

/**
 * @deprecated This code is taken from {@link CommandLineInterface} with applied patch from commit
 *     973e0c78 (this commit did not make to xfunction v27 release). When switching to new version
 *     of xfunction this class can be removed.
 */
@Deprecated
public class CommandLineInterfaceUtils {
    /**
     * Enable or disable echo to stdout for any key user press on keyboard.
     *
     * <p>This operation works only in systems with "stty" installed. If "stty" is not found or if
     * it returns error code the {@link Exception} will be thrown.
     */
    public static void echo(boolean enabled) throws Exception {
        var exec = new XExec("stty " + (enabled ? "" : "-") + "echo");
        exec.getProcessBuilder().redirectInput(ProcessBuilder.Redirect.INHERIT);
        var proc = exec.start();
        proc.forwardStdoutAsync(false);
        proc.stderrThrow();
    }

    /**
     * Usually any read operation on {@link System#in} blocks until user press Enter (new line).
     * This operation allows to disable such behavior so that any key which user press on keyboard
     * will be immediately available.
     *
     * <p>This operation works only in systems with "stty" installed. If "stty" is not found or if
     * it returns error code the {@link Exception} will be thrown.
     */
    public static void nonBlockingSystemInput() throws Exception {
        var exec = new XExec("stty -icanon min 1");
        exec.getProcessBuilder().redirectInput(ProcessBuilder.Redirect.INHERIT);
        var proc = exec.start();
        proc.forwardStdoutAsync(false);
        proc.stderrThrow();
    }
}
