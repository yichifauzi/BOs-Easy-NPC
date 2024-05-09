/*
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen.configuration.dialog;

import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.client.screen.configuration.ConfigurationScreen;
import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.data.dialog.DialogDataSet;
import de.markusbordihn.easynpc.menu.configuration.dialog.DialogConfigurationMenu;
import de.markusbordihn.easynpc.network.ServerNetworkMessageHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DialogConfigurationScreen<T extends DialogConfigurationMenu>
    extends ConfigurationScreen<T> {

  protected final DialogDataSet dialogDataSet;
  protected Button noneDialogButton = null;
  protected Button basicDialogButton = null;
  protected Button yesNoDialogButton = null;
  protected Button advancedDialogButton = null;

  public DialogConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.dialogDataSet = menu.getDialogDataSet();
  }

  @Override
  public void init() {
    super.init();

    // Dialog Types
    this.noneDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.buttonLeftPos,
                this.buttonTopPos,
                60,
                "disable_dialog",
                onPress ->
                    ServerNetworkMessageHandler.openConfiguration(
                        uuid, ConfigurationType.NONE_DIALOG)));
    this.basicDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.buttonLeftPos + this.noneDialogButton.getWidth(),
                this.buttonTopPos,
                70,
                "basic",
                onPress ->
                    ServerNetworkMessageHandler.openConfiguration(
                        uuid, ConfigurationType.BASIC_DIALOG)));
    this.yesNoDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.buttonLeftPos
                    + this.noneDialogButton.getWidth()
                    + this.basicDialogButton.getWidth(),
                this.buttonTopPos,
                70,
                "yes_no_dialog",
                onPress ->
                    ServerNetworkMessageHandler.openConfiguration(
                        uuid, ConfigurationType.YES_NO_DIALOG)));
    this.advancedDialogButton =
        this.addRenderableWidget(
            new TextButton(
                this.buttonLeftPos
                    + this.noneDialogButton.getWidth()
                    + this.basicDialogButton.getWidth()
                    + this.yesNoDialogButton.getWidth(),
                this.buttonTopPos,
                70,
                "advanced",
                onPress ->
                    ServerNetworkMessageHandler.openConfiguration(
                        uuid, ConfigurationType.ADVANCED_DIALOG)));

    // Default button stats
    this.noneDialogButton.active =
        this.hasPermissions(
            COMMON.noneDialogConfigurationEnabled.get(),
            COMMON.noneDialogConfigurationAllowInCreative.get(),
            COMMON.noneDialogConfigurationPermissionLevel.get());
    this.basicDialogButton.active =
        this.hasPermissions(
            COMMON.basicDialogConfigurationEnabled.get(),
            COMMON.basicDialogConfigurationAllowInCreative.get(),
            COMMON.basicDialogConfigurationPermissionLevel.get());
    this.yesNoDialogButton.active =
        this.hasPermissions(
            COMMON.yesNoDialogConfigurationEnabled.get(),
            COMMON.yesNoDialogConfigurationAllowInCreative.get(),
            COMMON.yesNoDialogConfigurationPermissionLevel.get());
    this.advancedDialogButton.active =
        this.hasPermissions(
            COMMON.advancedDialogConfigurationEnabled.get(),
            COMMON.advancedDialogConfigurationAllowInCreative.get(),
            COMMON.advancedDialogConfigurationPermissionLevel.get());
  }
}
