/**
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

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.network.NetworkMessage;

public class MessagePresetExport {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final UUID uuid;
  protected final String name;

  public MessagePresetExport(UUID uuid, String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getName() {
    return this.name;
  }

  public static void handle(MessagePresetExport message,
      Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> handlePacket(message, context));
    context.setPacketHandled(true);
  }

  public static void handlePacket(MessagePresetExport message, NetworkEvent.Context context) {
    ServerPlayer serverPlayer = context.getSender();
    UUID uuid = message.getUUID();
    if (serverPlayer == null || !MessageHelper.checkAccess(uuid, serverPlayer)) {
      return;
    }

    // Validate name.
    String name = message.getName();
    if (name == null || name.isEmpty()) {
      log.warn("Export preset name is empty for {}", uuid);
      return;
    }
    if (!name.endsWith(Constants.NPC_NBT_SUFFIX)) {
      name += Constants.NPC_NBT_SUFFIX;
    }

    // Perform action.
    NetworkMessage.exportPresetClient(uuid, name, serverPlayer);
  }

}