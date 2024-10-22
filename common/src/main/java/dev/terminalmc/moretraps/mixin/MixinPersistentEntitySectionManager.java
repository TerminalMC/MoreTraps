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

package dev.terminalmc.moretraps.mixin;

import dev.terminalmc.moretraps.MoreTraps;
import dev.terminalmc.moretraps.config.Config;
import dev.terminalmc.moretraps.entity.ai.goal.TrapTriggerGoal;
import dev.terminalmc.moretraps.mixin.accessor.MobAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager<T extends EntityAccess> {
    /**
     * Works with {@link MixinWorldGenRegion} and {@link MixinServerLevel} by
     * reading tags of added entities and setting a {@link TrapTriggerGoal}
     * where the trap source tag is found.
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onAddEntity(T entity, boolean existing, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.get().options.enabled) return;
        if (!(entity instanceof Mob mob)) return;
        if (mob.getTags().contains(MoreTraps.TRAP_SPAWN_TAG)) return;

        if (((MobAccessor)mob).getGoalSelector().getAvailableGoals().stream()
                .anyMatch(goal -> goal.getGoal() instanceof TrapTriggerGoal)) return;

        if (mob.getTags().contains(MoreTraps.TRAP_SOURCE_TAG)) {
            ((MobAccessor)mob).getGoalSelector().addGoal(1, new TrapTriggerGoal(mob));
            if (Config.get().options.debugMode) {
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2400, 0, false, false));
                MoreTraps.LOG.info("Added TrapTriggerGoal to tagged {} at {}",
                        mob.getName().getString(), mob.getOnPos());
            }
        }
    }
}
