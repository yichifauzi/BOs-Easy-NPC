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

package de.markusbordihn.easynpc.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MaterialColor;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.easynpc.Annotations.TemplateEntryPoint;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.entity.npc.Humanoid;
import de.markusbordihn.easynpc.entity.npc.ModEntityType;
import de.markusbordihn.easynpc.entity.npc.Villager;
import de.markusbordihn.easynpc.tabs.EasyNPCTab;

public class ModItems {

  protected ModItems() {

  }

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  @TemplateEntryPoint("Register Items")

  @TemplateEntryPoint("Register Block Items")

  @TemplateEntryPoint("Register Spawn Eggs")

  public static final RegistryObject<Item> HUMANOID_NPC_SPAWN_EGG =
      ITEMS.register(Humanoid.ID + "_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.HUMANOID::get, MaterialColor.COLOR_RED.col,
              MaterialColor.COLOR_GREEN.col,
              new Item.Properties().rarity(Rarity.EPIC).tab(EasyNPCTab.TAB_SPAWN_EGGS)));

  public static final RegistryObject<Item> VILLAGER_NPC_SPAWN_EGG =
      ITEMS.register(Villager.ID + "_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.VILLAGER::get, MaterialColor.COLOR_RED.col,
              MaterialColor.COLOR_YELLOW.col,
              new Item.Properties().rarity(Rarity.EPIC).tab(EasyNPCTab.TAB_SPAWN_EGGS)));

}
