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

package de.markusbordihn.easynpc.network.message;

import de.markusbordihn.easynpc.data.skin.SkinType;
import de.markusbordihn.easynpc.entity.LivingEntityManager;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.SkinData;
import de.markusbordihn.easynpc.network.NetworkMessage;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class MessageSkinTypeChange extends NetworkMessage {

  protected final SkinType skinType;

  public MessageSkinTypeChange(UUID uuid, SkinType skinType) {
    super(uuid);
    this.skinType = skinType;
  }

  public static MessageSkinTypeChange decode(final FriendlyByteBuf buffer) {
    return new MessageSkinTypeChange(buffer.readUUID(), buffer.readEnum(SkinType.class));
  }

  public static void encode(final MessageSkinTypeChange message, final FriendlyByteBuf buffer) {
    buffer.writeUUID(message.uuid);
    buffer.writeEnum(message.getSkinType());
  }

  public static void handle(MessageSkinTypeChange message, CustomPayloadEvent.Context context) {
    context.enqueueWork(() -> handlePacket(message, context));
    context.setPacketHandled(true);
  }

  public static void handlePacket(
      MessageSkinTypeChange message, CustomPayloadEvent.Context context) {
    ServerPlayer serverPlayer = context.getSender();
    UUID uuid = message.getUUID();
    if (serverPlayer == null || !NetworkMessage.checkAccess(uuid, serverPlayer)) {
      return;
    }

    // Validate skin type.
    SkinType skinType = message.getSkinType();
    if (skinType == null) {
      log.error("Invalid skin type {} for {} from {}", skinType, message, serverPlayer);
      return;
    }

    // Validate skin data.
    EasyNPC<?> easyNPC = LivingEntityManager.getEasyNPCEntityByUUID(uuid, serverPlayer);
    SkinData<?> skinData = easyNPC.getEasyNPCSkinData();
    if (skinData == null) {
      log.error("Unable to get valid skin data for {} from {}", message, serverPlayer);
      return;
    }

    // Perform action.
    log.debug(
        "Change skin type of {} from {} to {} from {}",
        easyNPC,
        skinData.getSkinType(),
        skinType,
        serverPlayer);
    skinData.setSkinType(skinType);
  }

  public SkinType getSkinType() {
    return this.skinType;
  }
}
