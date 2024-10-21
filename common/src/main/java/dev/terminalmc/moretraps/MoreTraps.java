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

import dev.terminalmc.moretraps.config.Config;
import dev.terminalmc.moretraps.config.Trap;
import dev.terminalmc.moretraps.util.ModLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.Nullable;

public class MoreTraps {
    public static final String MOD_ID = "moretraps";
    public static final String MOD_NAME = "MoreTraps";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);
    public static final Component PREFIX = Component.empty()
            .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(MOD_NAME).withStyle(ChatFormatting.GOLD))
            .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
            .withStyle(ChatFormatting.GRAY);
    public static final String TRAP_SOURCE_TAG = MOD_ID + ":trap_source";
    public static final String TRAP_SPAWN_TAG = MOD_ID + ":trap_spawn";

    public static void init() {
        Config.getAndSave();
    }

    public static void onEndTick(MinecraftServer mc) {

    }

    public static void onConfigSaved(Config config) {
        // If you are maintaining caches based on config values, update them here.
        for (Trap trap : config.options.traps) {
            trap.spawnType = null;
            trap.passengerType = null;

            @Nullable ResourceLocation id = ResourceLocation.tryParse(trap.sourceId);
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
                if (isMob(entityType)) trap.sourceType = (EntityType<Mob>)entityType;
            }

            id = ResourceLocation.tryParse(trap.spawnId);
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
                if (isMob(entityType)) trap.spawnType = (EntityType<Mob>)entityType;
            }

            id = ResourceLocation.tryParse(trap.passengerId);
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
                if (isMob(entityType)) trap.passengerType = (EntityType<Mob>)entityType;
            }
        }
    }

    public static boolean isMob(EntityType<?> entityType) {
        MobCategory cat = entityType.getCategory();
        return cat.equals(MobCategory.WATER_AMBIENT) || cat.equals(MobCategory.WATER_CREATURE)
                || cat.equals(MobCategory.AMBIENT) || cat.equals(MobCategory.MONSTER)
                || cat.equals(MobCategory.CREATURE) || cat.equals(MobCategory.AXOLOTLS)
                || cat.equals(MobCategory.UNDERGROUND_WATER_CREATURE);
    }
}
