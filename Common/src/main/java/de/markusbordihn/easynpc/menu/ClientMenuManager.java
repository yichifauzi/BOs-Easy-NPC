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

package de.markusbordihn.easynpc.menu;

import de.markusbordihn.easynpc.data.screen.AdditionalScreenData;
import de.markusbordihn.easynpc.data.screen.ScreenData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public class ClientMenuManager {

  private static ScreenData screenData;
  private static AdditionalScreenData additionalScreenData;
  private static CompoundTag menuData;
  private static UUID menuId;

  private ClientMenuManager() {}

  public static void setMenuData(UUID menuId, CompoundTag menuData) {
    ClientMenuManager.clearMenuData();
    ClientMenuManager.menuId = menuId;
    ClientMenuManager.menuData = menuData;

    // Decode screen data and additional screen data, if available.
    if (ScreenData.hasScreenData(menuData)) {
      ClientMenuManager.screenData = ScreenData.decode(menuData);
      ClientMenuManager.additionalScreenData =
          ClientMenuManager.screenData != null
              ? new AdditionalScreenData(ClientMenuManager.screenData.additionalData())
              : null;
    }
  }

  public static CompoundTag getMenuData() {
    return ClientMenuManager.menuData;
  }

  public static ScreenData getScreenData() {
    return ClientMenuManager.screenData;
  }

  public static AdditionalScreenData getAdditionalScreenData() {
    return ClientMenuManager.additionalScreenData;
  }

  public static boolean hasAdditionalScreenData() {
    return ClientMenuManager.additionalScreenData != null;
  }

  public static UUID getMenuId() {
    return ClientMenuManager.menuId;
  }

  public static void clearMenuData() {
    ClientMenuManager.menuId = null;
    ClientMenuManager.menuData = null;
    ClientMenuManager.screenData = null;
    ClientMenuManager.additionalScreenData = null;
  }
}
