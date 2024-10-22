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
import dev.terminalmc.moretraps.config.Trap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class MixinServerLevel {
    /**
     * Handles most non-generated mob spawns by applying a tag on entity spawn,
     * which is subsequently read by
     * {@link MixinPersistentEntitySectionManager}.
     */
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void onSpawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.get().options.enabled) return;
        if (!(entity instanceof Mob mob)) return;
        if (mob.getTags().contains(MoreTraps.TRAP_SPAWN_TAG)) return;
        if (mob.getTags().contains(MoreTraps.TRAP_SOURCE_TAG)) return;

        @Nullable Trap trap = Trap.getByType(entity.getType());
        if (trap != null && entity.getRandom().nextFloat() < trap.chance) {
            mob.addTag(MoreTraps.TRAP_SOURCE_TAG);
            MoreTraps.LOG.debug("Added TRAP_SOURCE_TAG to {} at {}",
                    mob.getName().getString(), mob.getOnPos());
        }
    }
}
