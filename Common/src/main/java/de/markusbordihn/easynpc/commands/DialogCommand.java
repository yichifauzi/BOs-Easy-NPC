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

package de.markusbordihn.easynpc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.entity.LivingEntityManager;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DialogCommand implements Command<CommandSourceStack> {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final DialogCommand command = new DialogCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("dialog")
        .requires(cs -> cs.hasPermission(Commands.LEVEL_GAMEMASTERS))
        .executes(command)
        .then(
            Commands.literal("open")
                .then(
                    Commands.argument("uuid", UuidArgument.uuid())
                        .suggests(DialogCommand::suggestEasyNPCs)
                        .then(
                            Commands.argument("player", EntityArgument.player())
                                .executes(command)
                                .then(
                                    Commands.argument("dialog", StringArgumentType.string())
                                        .executes(command)))));
  }

  protected static CompletableFuture<Suggestions> suggestEasyNPCs(
      CommandContext<CommandSourceStack> context, SuggestionsBuilder build)
      throws CommandSyntaxException {
    // Return all EasyNPCs for creative mode and only the EasyNPCs of the player.
    ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
    return SharedSuggestionProvider.suggest(
        serverPlayer.isCreative()
            ? LivingEntityManager.getUUIDStrings()
            : LivingEntityManager.getUUIDStringsByOwner(serverPlayer),
        build);
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    CommandSourceStack commandSource = context.getSource();
    UUID uuid = UuidArgument.getUuid(context, "uuid");
    ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");
    String dialogLabel = "";
    try {
      dialogLabel = StringArgumentType.getString(context, "dialog");
    } catch (Exception ignored) {

    }

    // Verify Player
    if (!serverPlayer.isAlive()) {
      commandSource.sendFailure(Component.literal("Player is death!"));
      return 0;
    }

    // Verify NPC
    EasyNPC<?> easyNPC = LivingEntityManager.getEasyNPCEntityByUUID(uuid, commandSource.getLevel());
    if (easyNPC == null) {
      commandSource.sendFailure(Component.literal("EasyNPC with UUID " + uuid + " not found!"));
      return 0;
    }

    // Verify dialog data
    if (easyNPC.getEasyNPCDialogData() == null || !easyNPC.getEasyNPCDialogData().hasDialog()) {
      commandSource.sendFailure(
          Component.literal("Found no Dialog data for EasyNPC with UUID " + uuid + "!"));
      return 0;
    }

    // Verify dialog label, if any
    if (!dialogLabel.isEmpty() && !easyNPC.getEasyNPCDialogData().hasDialog(dialogLabel)) {
      commandSource.sendFailure(
          Component.literal(
              "Found no Dialog with label "
                  + dialogLabel
                  + " for EasyNPC with UUID "
                  + uuid
                  + "!"));
      return 0;
    }

    // Open dialog
    commandSource.sendSuccess(
        Component.literal(
                "► Open dialog for "
                    + easyNPC
                    + " with "
                    + serverPlayer
                    + " and dialog "
                    + dialogLabel)
            .withStyle(ChatFormatting.GREEN),
        false);
    easyNPC.getEasyNPCDialogData().openDialog(serverPlayer, dialogLabel);
    return Command.SINGLE_SUCCESS;
  }
}
