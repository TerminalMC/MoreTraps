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

package dev.terminalmc.moretraps.config;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class Trap {
    public static final String defaultSourceId = "";
    public String sourceId = defaultSourceId;

    public static final String defaultSpawnId = "";
    public String spawnId = defaultSpawnId;

    public static final String defaultPassengerId = "";
    public String passengerId = defaultPassengerId;

    public static final boolean defaultPassengerOnSource = true;
    public boolean passengerOnSource = defaultPassengerOnSource;

    public static final boolean defaultInvertRiding = false;
    public boolean invertRiding = defaultInvertRiding;

    public static final float defaultChance = 0.05F;
    public float chance = defaultChance;

    public static final int defaultSpawnNum = 3;
    public int spawnNum = defaultSpawnNum;

    public static final int defaultEffectNum = 0;
    public int effectNum = defaultEffectNum;

    public @Nullable transient EntityType<Mob> sourceType;
    public @Nullable transient EntityType<Mob> spawnType;
    public @Nullable transient EntityType<Mob> passengerType;

    public Trap() {
    }

    public Trap(
            String sourceId,
            String spawnId,
            String passengerId,
            boolean passengerOnSource,
            boolean inverted,
            float chance,
            int spawnNum,
            int effectNum
    ) {
        this.sourceId = sourceId;
        this.spawnId = spawnId;
        this.passengerId = passengerId;
        this.passengerOnSource = passengerOnSource;
        this.invertRiding = inverted;
        this.chance = chance;
        this.spawnNum = spawnNum;
        this.effectNum = effectNum;
    }

    public static @Nullable Trap getByType(EntityType<?> type) {
        for (Trap trap : Config.get().options.traps) {
            if (trap.sourceType != null && trap.sourceType.equals(type)) {
                return trap;
            }
        }
        return null;
    }
}
