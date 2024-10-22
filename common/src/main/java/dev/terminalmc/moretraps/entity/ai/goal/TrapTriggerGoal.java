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

package dev.terminalmc.moretraps.entity.ai.goal;

import dev.terminalmc.moretraps.MoreTraps;
import dev.terminalmc.moretraps.config.Config;
import dev.terminalmc.moretraps.config.Trap;
import dev.terminalmc.moretraps.mixin.accessor.MobAccessor;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;

public class TrapTriggerGoal extends Goal {
    private final Mob entity;

    public TrapTriggerGoal(Mob entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.level().hasNearbyAlivePlayer(
                entity.getX(), entity.getY(), entity.getZ(), Config.get().options.activationRange);
    }

    @Override
    public void tick() {
        if (Config.get().options.debugMode) {
            entity.removeEffect(MobEffects.GLOWING);
            MoreTraps.LOG.info("Trap triggered for {} at {}",
                    entity.getName().getString(), entity.getOnPos());
        }
        ((MobAccessor)entity).getGoalSelector().removeGoal(this);

        if (!Config.get().options.enabled) return;
        @Nullable Trap trap = Trap.getByType(this.entity.getType());
        if (trap == null) return;
        boolean flip = trap.invertRiding;

        ServerLevel world = (ServerLevel)entity.level();
        DifficultyInstance localDiff = world.getCurrentDifficultyAt(entity.blockPosition());

        // Spawn lightning
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
            lightning.setVisualOnly(true);
            world.addFreshEntity(lightning);

            // Tag source entity
            entity.addTag(MoreTraps.TRAP_SPAWN_TAG);

            // Apply effects to source entity
            applyEffects(entity, trap.effectNum);

            // Spawn passenger on source entity
            if (trap.passengerType != null && trap.passengerOnSource) {
                Mob passenger = getEntity(world, localDiff, trap.passengerType, 0);
                if (passenger != null) {
                    if (!flip) {
                        passenger.startRiding(entity);
                        world.tryAddFreshEntityWithPassengers(passenger);
                    } else {
                        if (world.tryAddFreshEntityWithPassengers(passenger)) {
                            entity.startRiding(passenger);
                        }
                    }
                }
            }

            // Spawn trap entities
            EntityType<Mob> spawnType = flip ? trap.passengerType : trap.spawnType;
            EntityType<Mob> passengerType = flip ? trap.spawnType : trap.passengerType;

            if (spawnType != null) {
                for (int i = 0; i < trap.spawnNum; i++) {
                    // Spawn trap entity
                    Mob trapSpawn = getEntity(world, localDiff, spawnType, trap.effectNum);
                    if (trapSpawn != null) {
                        // Add passenger
                        if (passengerType != null) {
                            Mob passenger = getEntity(world, localDiff, passengerType, 0);
                            if (passenger != null) {
                                passenger.startRiding(trapSpawn);
                            }
                        }

                        // Apply spread velocity
                        trapSpawn.push(entity.getRandom().triangle(0.0, 1.1485),
                                0.0, entity.getRandom().triangle(0.0, 1.1485));
                        world.tryAddFreshEntityWithPassengers(trapSpawn);
                    }
                }
            }
        }
    }

    private @Nullable Mob getEntity(ServerLevel world, DifficultyInstance localDiff, EntityType<Mob> type, int effectNum) {
        Mob newEntity = type.create(world);
        if (newEntity != null) {
            newEntity.finalizeSpawn(world, localDiff, MobSpawnType.TRIGGERED, null);
            newEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
            if (effectNum > 0) applyEffects(newEntity, effectNum);
            newEntity.addTag(MoreTraps.TRAP_SPAWN_TAG);
        }
        return newEntity;
    }

    private void applyEffects(Mob mob, int effectNum) {
        Config.Options options = Config.get().options;
        List<MobEffect> effects = BuiltInRegistries.MOB_EFFECT.stream().collect(
                Collectors.filtering(MobEffect::isBeneficial, Collectors.toList()));
        StringBuilder effectsBuilder = new StringBuilder("Applied effects to ");
        effectsBuilder.append(mob.getName().getString());

        while (effectNum > 0) {
            MobEffect effect = effects.remove(entity.getRandom().nextInt(effects.size()));
            int amplifier = entity.getRandom().nextInt(effectNum);
            effectNum -= amplifier + 1;
            mob.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect),
                    options.effectDuration == -1 ? -1 : options.effectDuration * 20,
                    amplifier, false, options.showParticles));
            effectsBuilder.append("; ");
            effectsBuilder.append(effect.getDisplayName().getString());
            effectsBuilder.append(" ");
            effectsBuilder.append(amplifier + 1);
        }
        if (options.debugMode) MoreTraps.LOG.info(effectsBuilder.toString());
    }
}
