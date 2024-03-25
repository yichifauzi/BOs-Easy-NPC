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

package de.markusbordihn.easynpc.entity.easynpc.data;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface SpawnData<T extends PathfinderMob> extends EasyNPC<T> {

  MobCategory CATEGORY = MobCategory.MISC;

  default void onInitialSpawn(ServerLevel level, Player player, ItemStack itemStack) {
    // Set automatic owner for EasyNPCs spawned by player.
    if (player != null) {
      log.debug("Set owner {} for {} ...", player, this);
      OwnerData<?> ownerData = this.getEasyNPCOwnerData();
      if (ownerData != null) {
        ownerData.setOwnerUUID(player.getUUID());
      }
    }

    // Add standard objective for EasyNPCs spawned by player.
    ObjectiveData<?> objectiveData = this.getEasyNPCObjectiveData();
    if (objectiveData != null) {
      objectiveData.registerStandardObjectives();
    }
  }
}
