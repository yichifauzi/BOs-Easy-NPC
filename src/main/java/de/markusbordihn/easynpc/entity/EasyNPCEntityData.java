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

package de.markusbordihn.easynpc.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.level.Level;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.entity.data.ActionData;
import de.markusbordihn.easynpc.entity.data.DataSerializers;
import de.markusbordihn.easynpc.entity.data.DialogData;
import de.markusbordihn.easynpc.entity.data.ModelData;
import de.markusbordihn.easynpc.entity.data.ScaleData;
import de.markusbordihn.easynpc.entity.data.SkinData;
import de.markusbordihn.easynpc.utils.TextUtils;

public class EasyNPCEntityData extends AgeableMob
    implements Npc, ActionData, DialogData, ModelData, ScaleData, SkinData {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);



  // Default Variants
  private enum Variant {
    STEVE
  }

  // Synced Data
  private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID =
      SynchedEntityData.defineId(EasyNPCEntityData.class, EntityDataSerializers.OPTIONAL_UUID);
  private static final EntityDataAccessor<Profession> DATA_PROFESSION =
      SynchedEntityData.defineId(EasyNPCEntityData.class, DataSerializers.PROFESSION);

  private static final EntityDataAccessor<String> DATA_VARIANT =
      SynchedEntityData.defineId(EasyNPCEntityData.class, EntityDataSerializers.STRING);

  // Stored Entity Data Tags
  private static final String DATA_OWNER_TAG = "Owner";
  private static final String DATA_POSE_TAG = "Pose";
  private static final String DATA_PROFESSION_TAG = "Profession";
  private static final String DATA_VARIANT_TAG = "Variant";

  public EasyNPCEntityData(EntityType<? extends EasyNPCEntity> entityType, Level level) {
    super(entityType, level);
  }

  public Pose getPose(String pose) {
    return Pose.valueOf(pose);
  }

  public Profession getDefaultProfession() {
    return Profession.NONE;
  }

  public Profession getProfession() {
    return this.entityData.get(DATA_PROFESSION);
  }

  public Profession getProfession(String name) {
    return Profession.valueOf(name);
  }

  public void setProfession(Profession profession) {
    this.entityData.set(DATA_PROFESSION, profession);
  }

  public void setProfession(String name) {
    Profession profession = getProfession(name);
    if (profession != null) {
      setProfession(profession);
    } else {
      log.error("Unknown profession {} for {}", name, this);
    }
  }

  public boolean hasProfessions() {
    return false;
  }

  public Profession[] getProfessions() {
    return Profession.values();
  }

  public boolean hasProfession() {
    return false;
  }

  public Component getProfessionName() {
    Enum<?> profession = getProfession();
    return profession != null ? TextUtils.normalizeName(profession.name()) : new TextComponent("");
  }

  public Enum<?> getDefaultVariant() {
    return Variant.STEVE;
  }

  public Enum<?> getVariant() {
    return getVariant(this.entityData.get(DATA_VARIANT));
  }

  public Enum<?> getVariant(String name) {
    return Variant.valueOf(name);
  }

  public void setVariant(Enum<?> variant) {
    this.entityData.set(DATA_VARIANT, variant != null ? variant.name() : "");
  }

  public void setVariant(String name) {
    Enum<?> variant = getVariant(name);
    if (variant != null) {
      setVariant(variant);
    } else {
      log.error("Unknown variant {} for {}", name, this);
    }
  }

  public Enum<?>[] getVariants() {
    return Variant.values();
  }

  public Component getVariantName() {
    Enum<?> variant = getVariant();
    return variant != null ? TextUtils.normalizeName(variant.name()) : this.getTypeName();
  }

  @Override
  public Component getName() {
    Component component = this.getCustomName();
    return component != null ? TextUtils.removeAction(component) : this.getTypeName();
  }

  @Nullable
  public UUID getOwnerUUID() {
    return this.entityData.get(DATA_OWNER_UUID_ID).orElse((UUID) null);
  }

  public void setOwnerUUID(@Nullable UUID uuid) {
    this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(uuid));
  }

  public boolean hasOwner() {
    return this.getOwnerUUID() != null;
  }

  @Override
  public <T> void setEntityData(EntityDataAccessor<T> entityDataAccessor, T entityData) {
    this.entityData.set(entityDataAccessor, entityData);
  }

  @Override
  public <T> T getEntityData(EntityDataAccessor<T> entityDataAccessor) {
    return this.entityData.get(entityDataAccessor);
  }

  @Override
  public <T> void defineEntityData(EntityDataAccessor<T> entityDataAccessor, T entityData) {
    this.entityData.define(entityDataAccessor, entityData);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.defineSynchedActionData();
    this.defineSynchedDialogData();
    this.defineSynchedModelData();
    this.defineSynchedScaleData();
    this.defineSynchedSkinData();
    this.entityData.define(DATA_OWNER_UUID_ID, Optional.empty());
    this.entityData.define(DATA_PROFESSION, this.getDefaultProfession());
    this.entityData.define(DATA_VARIANT, this.getDefaultVariant().name());
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compoundTag) {
    super.addAdditionalSaveData(compoundTag);
    this.addAdditionalActionData(compoundTag);
    this.addAdditionalDialogData(compoundTag);
    this.addAdditionalModelData(compoundTag);
    this.addAdditionalScaleData(compoundTag);
    this.addAdditionalSkinData(compoundTag);
    if (this.getOwnerUUID() != null) {
      compoundTag.putUUID(DATA_OWNER_TAG, this.getOwnerUUID());
    }
    if (this.getPose() != null) {
      compoundTag.putString(DATA_POSE_TAG, this.getPose().name());
    }
    if (this.getProfession() != null) {
      compoundTag.putString(DATA_PROFESSION_TAG, this.getProfession().name());
    }
    if (this.getVariant() != null) {
      compoundTag.putString(DATA_VARIANT_TAG, this.getVariant().name());
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    this.readAdditionalActionData(compoundTag);
    this.readAdditionalDialogData(compoundTag);
    this.readAdditionalModelData(compoundTag);
    this.readAdditionalScaleData(compoundTag);
    this.readAdditionalSkinData(compoundTag);
    if (compoundTag.hasUUID(DATA_OWNER_TAG)) {
      UUID uuid = compoundTag.getUUID(DATA_OWNER_TAG);
      if (uuid != null) {
        this.setOwnerUUID(uuid);
      }
    }
    if (compoundTag.contains(DATA_POSE_TAG)) {
      String pose = compoundTag.getString(DATA_POSE_TAG);
      if (pose != null && !pose.isEmpty()) {
        this.setPose(this.getPose(pose));
      }
    }
    if (compoundTag.contains(DATA_PROFESSION_TAG)) {
      String profession = compoundTag.getString(DATA_PROFESSION_TAG);
      if (profession != null && !profession.isEmpty()) {
        this.setProfession(this.getProfession(profession));
      }
    }
    if (compoundTag.contains(DATA_VARIANT_TAG)) {
      String variant = compoundTag.getString(DATA_VARIANT_TAG);
      if (variant != null && !variant.isEmpty()) {
        this.setVariant(this.getVariant(variant));
      }
    }
  }

  @Override
  public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }

  @SuppressWarnings("java:S3400")
  public int getEntityGuiScaling() {
    return 30;
  }

  @SuppressWarnings("java:S3400")
  public int getEntityGuiTop() {
    return 0;
  }

  @SuppressWarnings("java:S3400")
  public int getEntityDialogTop() {
    return 0;
  }

  @SuppressWarnings("java:S3400")
  public int getEntityDialogScaling() {
    return 50;
  }

}
