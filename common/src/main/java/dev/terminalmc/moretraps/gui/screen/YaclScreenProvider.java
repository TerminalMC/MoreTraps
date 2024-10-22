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

package dev.terminalmc.moretraps.gui.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.terminalmc.moretraps.MoreTraps;
import dev.terminalmc.moretraps.config.Config;
import dev.terminalmc.moretraps.config.Trap;
import dev.terminalmc.moretraps.mixin.accessor.YACLScreenAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.terminalmc.moretraps.util.Localization.localized;

public class YaclScreenProvider {
    /**
     * Builds and returns a YACL options screen.
     * @param parent the current screen.
     * @return a new options {@link Screen}.
     * @throws NoClassDefFoundError if the YACL mod is not available.
     */
    static Screen getConfigScreen(Screen parent) {
        Config.Options options = Config.get().options;

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(localized("screen", "options"))
                .save(Config::save);

        ConfigCategory.Builder general = ConfigCategory.createBuilder()
                .name(localized("option", "general"));

        general.option(Option.<Boolean>createBuilder()
                .name(localized("option", "general.enabled"))
                .binding(Config.Options.defaultEnabled,
                        () -> options.enabled,
                        val -> options.enabled = val)
                .controller(option -> BooleanControllerBuilder.create(option)
                        .coloured(true)
                        .yesNoFormatter())
                .build());

        general.option(Option.<Boolean>createBuilder()
                .name(localized("option", "general.debugMode"))
                .description((val) -> OptionDescription.of(
                        localized("option", "general.debugMode.tooltip")))
                .binding(Config.Options.defaultDebugMode,
                        () -> options.debugMode,
                        val -> options.debugMode = val)
                .controller(option -> BooleanControllerBuilder.create(option)
                        .coloured(true)
                        .yesNoFormatter())
                .build());

        general.option(Option.<Float>createBuilder()
                .name(localized("option", "general.trapRange"))
                .description(OptionDescription.of(
                        localized("option", "general.trapRange.tooltip")))
                .binding(Config.Options.defaultActivationRange,
                        () -> options.activationRange,
                        val -> options.activationRange = val)
                .controller(option -> FloatSliderControllerBuilder.create(option)
                        .range(1F, 50F)
                        .step(1F))
                .build());

        general.option(Option.<Boolean>createBuilder()
                .name(localized("option", "general.allowInstant"))
                .description((val) -> OptionDescription.of(
                        localized("option", "general.allowInstant.tooltip")))
                .binding(Config.Options.defaultAllowInstant,
                        () -> options.allowInstant,
                        val -> options.allowInstant = val)
                .controller(option -> BooleanControllerBuilder.create(option)
                        .coloured(true)
                        .yesNoFormatter())
                .build());

        general.option(Option.<Integer>createBuilder()
                .name(localized("option", "general.effectDuration"))
                .description(OptionDescription.of(
                        localized("option", "general.effectDuration.tooltip")))
                .binding(Config.Options.defaultEffectDuration,
                        () -> options.effectDuration,
                        val -> options.effectDuration = val)
                .controller(option -> IntegerFieldControllerBuilder.create(option)
                        .range(-1, 86400))
                .build());

        general.option(Option.<Boolean>createBuilder()
                .name(localized("option", "general.showParticles"))
                .description((val) -> OptionDescription.of(
                        localized("option", "general.showParticles.tooltip")))
                .binding(Config.Options.defaultShowParticles,
                        () -> options.showParticles,
                        val -> options.showParticles = val)
                .controller(option -> BooleanControllerBuilder.create(option)
                        .coloured(true)
                        .yesNoFormatter())
                .build());

        ConfigCategory.Builder traps = ConfigCategory.createBuilder()
                .name(localized("option", "traps"));

        List<String> entities = BuiltInRegistries.ENTITY_TYPE.keySet()
                .stream().filter(id -> MoreTraps.isMob(BuiltInRegistries.ENTITY_TYPE.get(id)))
                .map(ResourceLocation::toString).toList();

        int i = 0;
        for (Trap trap : options.traps) {
            i++;
            OptionGroup.Builder trapGroup = OptionGroup.createBuilder();
            trapGroup.name(localized("option", "trapGroup", i));
            trapGroup.collapsed(true);

            trapGroup.option(Option.<String>createBuilder()
                    .name(localized("option", "trapGroup.sourceId"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.sourceId.tooltip")))
                    .binding(Trap.defaultSourceId,
                            () -> trap.sourceId,
                            val -> trap.sourceId = val)
                    .controller(option -> DropdownStringControllerBuilder.create(option)
                            .values(entities)
                            .allowAnyValue(false)
                            .allowEmptyValue(true))
                    .build());

            trapGroup.option(Option.<String>createBuilder()
                    .name(localized("option", "trapGroup.spawnId"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.spawnId.tooltip")))
                    .binding(Trap.defaultSpawnId,
                            () -> trap.spawnId,
                            val -> trap.spawnId = val)
                    .controller(option -> DropdownStringControllerBuilder.create(option)
                            .values(entities)
                            .allowAnyValue(false)
                            .allowEmptyValue(true))
                    .build());

            trapGroup.option(Option.<String>createBuilder()
                    .name(localized("option", "trapGroup.passengerId"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.passengerId.tooltip")))
                    .binding(Trap.defaultPassengerId,
                            () -> trap.passengerId,
                            val -> trap.passengerId = val)
                    .controller(option -> DropdownStringControllerBuilder.create(option)
                            .values(entities)
                            .allowAnyValue(false)
                            .allowEmptyValue(true))
                    .build());

            trapGroup.option(Option.<Boolean>createBuilder()
                    .name(localized("option", "trapGroup.passengerOnSource"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.passengerOnSource.tooltip")))
                    .binding(Trap.defaultPassengerOnSource,
                            () -> trap.passengerOnSource,
                            val -> trap.passengerOnSource = val)
                    .controller(option -> BooleanControllerBuilder.create(option)
                            .coloured(true)
                            .yesNoFormatter())
                    .build());

            trapGroup.option(Option.<Boolean>createBuilder()
                    .name(localized("option", "trapGroup.invertRiding"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.invertRiding.tooltip")))
                    .binding(Trap.defaultInvertRiding,
                            () -> trap.invertRiding,
                            val -> trap.invertRiding = val)
                    .controller(option -> BooleanControllerBuilder.create(option)
                            .coloured(true)
                            .yesNoFormatter())
                    .build());

            trapGroup.option(Option.<Float>createBuilder()
                    .name(localized("option", "trapGroup.chance"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.chance.tooltip")))
                    .binding(Trap.defaultChance * 100F,
                            () -> trap.chance * 100F,
                            val -> trap.chance = val / 100F)
                    .controller(option -> FloatSliderControllerBuilder.create(option)
                            .range(0F, 100F)
                            .step(0.1F))
                    .build());

            trapGroup.option(Option.<Integer>createBuilder()
                    .name(localized("option", "trapGroup.spawnNum"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.spawnNum.tooltip")))
                    .binding(Trap.defaultSpawnNum,
                            () -> trap.spawnNum,
                            val -> trap.spawnNum = val)
                    .controller(option -> IntegerSliderControllerBuilder.create(option)
                            .range(0, 20)
                            .step(1))
                    .build());

            trapGroup.option(Option.<Integer>createBuilder()
                    .name(localized("option", "trapGroup.effectNum"))
                    .description(OptionDescription.of(
                            localized("option", "trapGroup.effectNum.tooltip")))
                    .binding(Trap.defaultEffectNum,
                            () -> trap.effectNum,
                            val -> trap.effectNum = val)
                    .controller(option -> IntegerSliderControllerBuilder.create(option)
                            .range(0, 20)
                            .step(1))
                    .build());

            trapGroup.option(ButtonOption.createBuilder()
                    .name(localized("option", "trapGroup.delete")
                            .withStyle(ChatFormatting.RED))
                    .action((screen, buttonOption) -> {
                        options.traps.remove(trap);
                        reload(screen, ((YACLScreenAccessor)screen).getParent());
                    })
                    .build());

            traps.group(trapGroup.build());
        }

        ButtonOption.Builder addButton = ButtonOption.createBuilder();
        addButton.name(localized("option", "trapGroup.add")
                .withStyle(ChatFormatting.GREEN));
        addButton.action((screen, buttonOption) -> {
            options.traps.add(new Trap());
            reload(screen, ((YACLScreenAccessor)screen).getParent());
        });
        traps.option(addButton.build());

        // Assemble
        builder.category(general.build());
        builder.category(traps.build());

        YetAnotherConfigLib yacl = builder.build();
        return yacl.generateScreen(parent);
    }

    /**
     * Creates a new YACL screen and switches to it.
     *
     * <p>Intended for use by {@link ButtonOption} instances that modify the
     * config, such as by adding or removing config objects configured by
     * {@link OptionGroup} instances.</p>
     *
     * @param screen the current screen.
     * @param parent the current screen's parent.
     */
    private static void reload(YACLScreen screen, Screen parent) {
        int tab = screen.tabNavigationBar == null ? 0
                : screen.tabNavigationBar.getTabs().indexOf(screen.tabManager.getCurrentTab());
        if (tab == -1) tab = 0;
        screen.finishOrSave();
        screen.onClose(); // In case finishOrSave doesn't close it.
        YACLScreen newScreen = (YACLScreen)ConfigScreenProvider.getConfigScreen(parent);
        newScreen.init(Minecraft.getInstance(), screen.width, screen.height);
        try {
            newScreen.tabNavigationBar.selectTab(tab, false);
        } catch (IndexOutOfBoundsException e) {
            MoreTraps.LOG.warn("YACL reload hack attempted to select tab {} but max index was {}",
                    tab, newScreen.tabNavigationBar.getTabs().size() - 1);
        }
        Minecraft.getInstance().setScreen(newScreen);
    }
}
