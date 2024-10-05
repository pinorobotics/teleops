/*
 * Copyright 2021 jrosmoveit project
 *
 * Website: https://github.com/pinorobotics/jrosmoveit
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
/**
 * Java module which allows to interact with MoveIt in ROS2 (Robot Operating System).
 *
 * <p>For usage examples see <a href="http://pinoweb.freetzi.com/jrosmoveit">Documentation</a>
 *
 * @see <a href="http://pinoweb.freetzi.com/jrosmoveit">Documentation</a>
 * @see <a href="https://github.com/pinorobotics/jros2moveit/releases">Download</a>
 * @see <a href="https://github.com/pinorobotics/jros2moveit">GitHub repository</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
module teleops {
    requires jros2client;
    requires jros2services;
    requires id.xfunction;
    requires jrosclient;
    requires jros2messages;

    exports pinorobotics.teleops;
}
