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

package de.markusbordihn.easynpc.server.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.renderer.manager.EntityTypeManager;
import de.markusbordihn.easynpc.commands.Command;
import de.markusbordihn.easynpc.debug.DebugManager;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class DebugCommand extends Command {

  private DebugCommand() {}

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("debug")
        .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
        .then(
            Commands.literal("log")
                .then(
                    Commands.argument("enable", BoolArgumentType.bool())
                        .executes(
                            context ->
                                setDebug(
                                    context.getSource(),
                                    BoolArgumentType.getBool(context, "enable")))))
        .then(
            Commands.literal("info")
                .then(
                    Commands.literal("entity_types")
                        .executes(context -> getEntityTypes(context.getSource()))));
  }

  public static int setDebug(CommandSourceStack context, boolean enable) {
    if (enable) {
      sendSuccessMessage(
          context,
          "► Enable debug for "
              + Constants.MOD_NAME
              + ", please check debug.log for the full output.",
          ChatFormatting.GREEN);
      sendSuccessMessage(
          context,
          "> Use '/" + Constants.MOD_COMMAND + " debug false' to disable the debug!",
          ChatFormatting.WHITE);
    } else {
      sendSuccessMessage(
          context, "■ Disable debug for " + Constants.MOD_NAME + "!", ChatFormatting.RED);
      sendSuccessMessage(
          context,
          "> Please check the latest.log and/or debug.log for the full output.",
          ChatFormatting.WHITE);
    }
    DebugManager.enableDebugLevel(enable);
    return Command.SINGLE_SUCCESS;
  }

  public static int getEntityTypes(CommandSourceStack context) {
    Set<EntityType<? extends Entity>> supportedEntityTypes =
        EntityTypeManager.getSupportedEntityTypes();
    Set<EntityType<? extends Entity>> unsupportedEntityTypes =
        EntityTypeManager.getUnsupportedEntityTypes();
    Set<EntityType<? extends Entity>> unknownEntityTypes =
        EntityTypeManager.getUnknownEntityTypes();
    sendSuccessMessage(
        context,
        "► Found "
            + supportedEntityTypes.size()
            + " supported, "
            + unsupportedEntityTypes.size()
            + " unsupported and "
            + unknownEntityTypes.size()
            + " unknown entity types.",
        ChatFormatting.GREEN);
    sendSuccessMessage(
        context,
        "> Please check the latest.log and/or debug.log for the full output.",
        ChatFormatting.WHITE);
    log.info("Supported entity types: {}", supportedEntityTypes);
    log.info("Unsupported entity types: {}", unsupportedEntityTypes);
    log.info("Unknown entity types: {}", unknownEntityTypes);
    return Command.SINGLE_SUCCESS;
  }
}
