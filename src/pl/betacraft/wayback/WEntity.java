package pl.betacraft.wayback;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class WEntity extends EntityListener {

	@Override
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Sheep)) {
			return;
		}
		if (e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.CONTACT) {
			return;
		}
		Sheep sheep = (Sheep)e.getEntity();
		if (sheep.isDead() || sheep.isSheared()) {
			return;
		}
		Random r = new Random();
		int random = r.nextInt(4);
		if (random == 0) {
			random = 1;
		}
		sheep.getWorld().dropItemNaturally(sheep.getEyeLocation(), new ItemStack(Material.WOOL, random, sheep.getColor().getData()));
		sheep.setSheared(true);
	}
}