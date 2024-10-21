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

package dev.terminalmc.moretraps.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.terminalmc.moretraps.MoreTraps;
import dev.terminalmc.moretraps.config.Config;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.commands.Commands.literal;

@SuppressWarnings("unchecked")
public class Commands<S> extends CommandDispatcher<S> {
    public void register(CommandDispatcher<S> dispatcher, CommandBuildContext buildContext,
                         net.minecraft.commands.Commands.CommandSelection selection) {
        dispatcher.register((LiteralArgumentBuilder<S>)literal(MoreTraps.MOD_ID)
                .then(literal("enable")
                        .executes(ctx -> {
                            MutableComponent msg = MoreTraps.PREFIX.copy();
                            if (Config.get().options.enabled) {
                                msg.append("Already enabled");
                            } else {
                                Config.get().options.enabled = true;
                                Config.save();
                                msg.append("Enabled");
                            }
                            ctx.getSource().getPlayer().sendSystemMessage(msg);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(literal("disable")
                        .executes(ctx -> {
                            MutableComponent msg = MoreTraps.PREFIX.copy();
                            if (!Config.get().options.enabled) {
                                msg.append("Already disabled");
                            } else {
                                Config.get().options.enabled = false;
                                Config.save();
                                msg.append("Disabled");
                            }
                            ctx.getSource().getPlayer().sendSystemMessage(msg);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(literal("reload")
                        .executes(ctx -> {
                            MutableComponent msg = MoreTraps.PREFIX.copy();
                            Config.loadAndSave();
                            msg.append("Config reloaded");
                            ctx.getSource().getPlayer().sendSystemMessage(msg);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
