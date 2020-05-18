package draylar.bossloottables.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends LivingEntity {

    private WitherEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        if(this.world.getServer() != null) {
            Identifier lootTableID = this.getLootTable();
            LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableID);
            LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);

            // drop loot as "coveted", which takes longer to despawn
            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), stack -> {
                ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stack);
                itemEntity.setToDefaultPickupDelay();
                itemEntity.setCovetedItem();
                this.world.spawnEntity(itemEntity);
            });
        }
    }

    /**
     * Prevents {@link WitherEntity#dropEquipment(DamageSource, int, boolean)} from spawning an extra {@link net.minecraft.item.Items#NETHER_STAR} on death.
     *
     * @author Draylar
     * @param source  source of damage
     * @param lootingMultiplier  amount of looting in current kill context
     * @param allowDrops  whether or not drops are allowed
     */
    @Overwrite
    public void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {

    }
}
