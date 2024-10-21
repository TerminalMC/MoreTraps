/*
 * Copyright 2024 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.moretraps;

import dev.terminalmc.moretraps.command.Commands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;

public class MoreTrapsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Commands
        CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, commandSelection) ->
                new Commands<CommandSourceStack>().register(dispatcher, buildContext, commandSelection)));

        // Tick events
        ServerTickEvents.END_SERVER_TICK.register(MoreTraps::onEndTick);

        // Main initialization
        MoreTraps.init();
    }
}
