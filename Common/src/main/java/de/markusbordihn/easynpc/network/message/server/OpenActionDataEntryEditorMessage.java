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

package de.markusbordihn.easynpc.network.message.server;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.data.action.ActionEventType;
import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.data.editor.EditorType;
import de.markusbordihn.easynpc.menu.MenuManager;
import de.markusbordihn.easynpc.network.message.NetworkMessage;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class OpenActionDataEntryEditorMessage extends NetworkMessage {

  public static final ResourceLocation MESSAGE_ID =
      new ResourceLocation(Constants.MOD_ID, "open_action_data_entry_editor");
  public final ActionEventType actionEventType;
  public final ConfigurationType configurationType;
  public final EditorType editorType;
  public final UUID actionDataEntryId;
  public final UUID dialogButtonId;
  public final UUID dialogId;

  public OpenActionDataEntryEditorMessage(
      UUID uuid,
      UUID dialogId,
      UUID dialogButtonId,
      UUID actionDataEntryId,
      ActionEventType actionEventType,
      ConfigurationType configurationType,
      EditorType editorType) {
    super(uuid, 0);
    this.actionDataEntryId = actionDataEntryId;
    this.actionEventType = actionEventType;
    this.configurationType = configurationType;
    this.dialogId = dialogId;
    this.dialogButtonId = dialogButtonId;
    this.editorType = editorType;
  }

  public OpenActionDataEntryEditorMessage(
      UUID uuid,
      UUID dialogId,
      UUID dialogButtonId,
      UUID actionDataEntryId,
      EditorType editorType) {
    this(
        uuid,
        dialogId,
        dialogButtonId,
        actionDataEntryId,
        ActionEventType.NONE,
        ConfigurationType.NONE,
        editorType);
  }

  public OpenActionDataEntryEditorMessage(
      UUID uuid,
      UUID actionDataEntryUUID,
      ActionEventType actionEventType,
      ConfigurationType configurationType) {
    this(
        uuid,
        Constants.EMPTY_UUID,
        Constants.EMPTY_UUID,
        actionDataEntryUUID,
        actionEventType,
        configurationType,
        EditorType.NONE);
  }

  public static OpenActionDataEntryEditorMessage decode(final FriendlyByteBuf buffer) {
    return new OpenActionDataEntryEditorMessage(
        buffer.readUUID(),
        buffer.readUUID(),
        buffer.readUUID(),
        buffer.readUUID(),
        buffer.readEnum(ActionEventType.class),
        buffer.readEnum(ConfigurationType.class),
        buffer.readEnum(EditorType.class));
  }

  public static FriendlyByteBuf encode(
      final OpenActionDataEntryEditorMessage message, final FriendlyByteBuf buffer) {
    buffer.writeUUID(message.uuid);
    buffer.writeUUID(message.dialogId);
    buffer.writeUUID(message.dialogButtonId);
    buffer.writeUUID(message.actionDataEntryId);
    buffer.writeEnum(message.actionEventType);
    buffer.writeEnum(message.configurationType);
    buffer.writeEnum(message.editorType);
    return buffer;
  }

  public static void handle(final FriendlyByteBuf buffer, final ServerPlayer serverPlayer) {
    handle(decode(buffer), serverPlayer);
  }

  public static void handle(
      OpenActionDataEntryEditorMessage message, final ServerPlayer serverPlayer) {
    if (!message.handleMessage(serverPlayer)) {
      return;
    }

    // Open action data entry editor
    MenuManager.getMenuHandler()
        .openEditorMenu(
            EditorType.ACTION_DATA_ENTRY,
            serverPlayer,
            message.getEasyNPC(),
            message.getDialogId(),
            message.getDialogButtonId(),
            message.getActionDataEntryId(),
            message.getActionEventType(),
            message.getConfigurationType(),
            message.getEditorType(),
            0);
  }

  @Override
  public FriendlyByteBuf encode() {
    return encode(this, new FriendlyByteBuf(Unpooled.buffer()));
  }

  public UUID getActionDataEntryId() {
    return this.actionDataEntryId;
  }

  public ActionEventType getActionEventType() {
    return this.actionEventType;
  }

  public ConfigurationType getConfigurationType() {
    return this.configurationType;
  }

  public EditorType getEditorType() {
    return this.editorType;
  }

  public UUID getDialogId() {
    return this.dialogId;
  }

  public UUID getDialogButtonId() {
    return this.dialogButtonId;
  }
}